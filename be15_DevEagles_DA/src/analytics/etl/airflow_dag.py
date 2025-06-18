"""Airflow DAG for customer analytics ETL pipeline."""

from datetime import datetime, timedelta
from typing import Dict, Any

try:
    from airflow import DAG
    from airflow.operators.python import PythonOperator
    from airflow.operators.bash import BashOperator
    from airflow.sensors.filesystem import FileSensor
    from airflow.utils.dates import days_ago
    from airflow.utils.task_group import TaskGroup
    AIRFLOW_AVAILABLE = True
except ImportError:
    AIRFLOW_AVAILABLE = False

from analytics.core.logging import get_logger
from .config import DEFAULT_AIRFLOW_CONFIG, DEFAULT_ETL_CONFIG
from .pipeline import create_pipeline


logger = get_logger("AirflowDAG")


def run_etl_pipeline(**context) -> Dict[str, Any]:
    """ETL 파이프라인 실행 태스크."""
    import asyncio
    
    # 실행 날짜 추출
    execution_date = context['execution_date']
    incremental = context.get('dag_run').conf.get('incremental', True)
    use_spark = context.get('dag_run').conf.get('use_spark', False)
    
    logger.info(f"Starting ETL pipeline - execution_date: {execution_date}, incremental: {incremental}")
    
    # 파이프라인 생성 및 실행
    pipeline = create_pipeline(use_spark=use_spark, config=DEFAULT_ETL_CONFIG)
    
    # 비동기 실행
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    
    try:
        results = loop.run_until_complete(pipeline.run(incremental=incremental))
        
        # 결과 로깅
        for step, result in results.items():
            if result.success:
                logger.info(f"{step}: {result.records_processed} processed, {result.processing_time_seconds:.2f}s")
            else:
                logger.error(f"{step} failed: {result.error_message}")
        
        # 전체 성공 여부 확인
        overall_success = all(r.success for r in results.values())
        if not overall_success:
            raise Exception("One or more ETL steps failed")
        
        return {
            "success": True,
            "results": {k: v.__dict__ for k, v in results.items()},
            "execution_date": execution_date.isoformat()
        }
        
    finally:
        loop.close()


def check_data_quality(**context) -> Dict[str, Any]:
    """데이터 품질 검증 태스크."""
    from analytics.core.database import get_analytics_db
    
    logger.info("Starting data quality checks")
    
    conn = get_analytics_db()
    
    # 기본 데이터 품질 체크
    checks = {
        "customer_analytics_count": "SELECT COUNT(*) FROM customer_analytics",
        "visit_analytics_count": "SELECT COUNT(*) FROM visit_analytics", 
        "service_preferences_count": "SELECT COUNT(*) FROM customer_service_preferences",
        "service_tags_count": "SELECT COUNT(*) FROM customer_service_tags",
        "null_customer_names": "SELECT COUNT(*) FROM customer_analytics WHERE name IS NULL",
        "duplicate_customers": """
            SELECT COUNT(*) FROM (
                SELECT customer_id, COUNT(*) as cnt 
                FROM customer_analytics 
                GROUP BY customer_id 
                HAVING COUNT(*) > 1
            )
        """
    }
    
    results = {}
    
    for check_name, query in checks.items():
        try:
            result = conn.execute(query).fetchone()
            count = result[0] if result else 0
            results[check_name] = count
            logger.info(f"{check_name}: {count}")
        except Exception as e:
            logger.error(f"Quality check {check_name} failed: {e}")
            results[check_name] = -1
    
    # 품질 기준 검증
    quality_issues = []
    
    if results.get("null_customer_names", 0) > 0:
        quality_issues.append(f"Found {results['null_customer_names']} customers with null names")
    
    if results.get("duplicate_customers", 0) > 0:
        quality_issues.append(f"Found {results['duplicate_customers']} duplicate customers")
    
    if quality_issues:
        logger.warning(f"Data quality issues found: {quality_issues}")
        # 품질 이슈가 있어도 실패시키지 않음 (경고만)
    
    return {
        "success": True,
        "quality_checks": results,
        "quality_issues": quality_issues
    }


def send_notification(**context) -> Dict[str, Any]:
    """알림 전송 태스크."""
    # TODO: 디스코드, 이메일 등으로 알림 전송
    
    task_instance = context['task_instance']
    dag_run = context['dag_run']
    
    # 이전 태스크 결과 확인
    etl_result = task_instance.xcom_pull(task_ids='run_etl_pipeline')
    quality_result = task_instance.xcom_pull(task_ids='check_data_quality')
    
    message = f"""
    ETL Pipeline Completed
    
    Execution Date: {dag_run.execution_date}
    ETL Success: {etl_result.get('success', False) if etl_result else False}
    Quality Checks: {len(quality_result.get('quality_issues', [])) if quality_result else 0} issues
    
    Results: {etl_result.get('results', {}) if etl_result else {}}
    """
    
    logger.info(f"Notification: {message}")
    
    return {"success": True, "message": message}


def create_customer_analytics_dag():
    """고객 분석 ETL DAG 생성."""
    if not AIRFLOW_AVAILABLE:
        logger.warning("Airflow not available. DAG creation skipped.")
        return None
    
    # DAG 기본 설정
    default_args = DEFAULT_AIRFLOW_CONFIG.to_airflow_args()
    default_args.update({
        'owner': 'deveagles-analytics',
        'email_on_failure': True,
        'email_on_retry': False,
        'email': ['64etuor@gmail.com'],
    })
    
    dag = DAG(
        'customer_analytics_etl',
        default_args=default_args,
        description='Customer Analytics ETL Pipeline',
        schedule_interval='0 2 * * *',  # 매일 새벽 2시
        start_date=days_ago(1),
        catchup=False,
        max_active_runs=1,
        tags=['analytics', 'etl', 'customer'],
    )
    
    # 1. 데이터 가용성 체크 (선택사항)
    check_crm_data = BashOperator(
        task_id='check_crm_data_availability',
        bash_command='echo "Checking CRM data availability..."',
        dag=dag,
    )
    
    # 2. ETL 파이프라인 실행
    run_etl = PythonOperator(
        task_id='run_etl_pipeline',
        python_callable=run_etl_pipeline,
        dag=dag,
    )
    
    # 3. 데이터 품질 검증
    quality_check = PythonOperator(
        task_id='check_data_quality',
        python_callable=check_data_quality,
        dag=dag,
    )
    
    # 4. 알림 전송
    notify = PythonOperator(
        task_id='send_notification',
        python_callable=send_notification,
        dag=dag,
    )
    
    # 태스크 의존성 설정
    check_crm_data >> run_etl >> quality_check >> notify
    
    return dag


def create_spark_etl_dag():
    """Spark 기반 대용량 ETL DAG 생성."""
    if not AIRFLOW_AVAILABLE:
        logger.warning("Airflow not available. DAG creation skipped.")
        return None
    
    # Spark 전용 설정
    spark_args = DEFAULT_AIRFLOW_CONFIG.to_airflow_args()
    spark_args.update({
        'owner': 'deveagles-analytics',
        'execution_timeout': timedelta(hours=2),  # Spark 작업은 더 긴 타임아웃
        'email_on_failure': True,
        'email': ['analytics@deveagles.com'],
    })
    
    dag = DAG(
        'customer_analytics_spark_etl',
        default_args=spark_args,
        description='Customer Analytics Spark ETL Pipeline (Large Data)',
        schedule_interval='0 1 * * 0',  # 매주 일요일 새벽 1시 (주간 전체 처리)
        start_date=days_ago(1),
        catchup=False,
        max_active_runs=1,
        tags=['analytics', 'etl', 'spark', 'weekly'],
    )
    
    # Spark ETL 실행 (전체 데이터 처리)
    run_spark_etl = PythonOperator(
        task_id='run_spark_etl_pipeline',
        python_callable=run_etl_pipeline,
        op_kwargs={'use_spark': True, 'incremental': False},
        dag=dag,
    )
    
    # 품질 검증
    quality_check = PythonOperator(
        task_id='check_data_quality',
        python_callable=check_data_quality,
        dag=dag,
    )
    
    # 알림
    notify = PythonOperator(
        task_id='send_notification',
        python_callable=send_notification,
        dag=dag,
    )
    
    # 태스크 의존성
    run_spark_etl >> quality_check >> notify
    
    return dag


# DAG 인스턴스 생성 (Airflow가 자동으로 감지)
if AIRFLOW_AVAILABLE:
    # 일일 증분 ETL
    daily_etl_dag = create_customer_analytics_dag()
    
    # 주간 전체 ETL (Spark)
    weekly_spark_dag = create_spark_etl_dag()
    
    # 글로벌 네임스페이스에 DAG 등록
    globals()['customer_analytics_etl'] = daily_etl_dag
    globals()['customer_analytics_spark_etl'] = weekly_spark_dag
else:
    logger.warning("Airflow not available. DAGs not created.")


# 수동 실행을 위한 헬퍼 함수들
def run_etl_manually(use_spark: bool = False, incremental: bool = True):
    """ETL 파이프라인 수동 실행."""
    import asyncio
    
    logger.info(f"Running ETL manually - spark: {use_spark}, incremental: {incremental}")
    
    pipeline = create_pipeline(use_spark=use_spark, config=DEFAULT_ETL_CONFIG)
    
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    
    try:
        results = loop.run_until_complete(pipeline.run(incremental=incremental))
        
        for step, result in results.items():
            if result.success:
                print(f"✓ {step}: {result.records_processed} processed ({result.processing_time_seconds:.2f}s)")
            else:
                print(f"✗ {step}: {result.error_message}")
        
        return results
        
    finally:
        loop.close()


if __name__ == "__main__":
    # 직접 실행 시 수동 테스트
    print("Running ETL pipeline manually...")
    results = run_etl_manually(use_spark=False, incremental=True)
    print(f"Completed with {len([r for r in results.values() if r.success])} successful steps") 