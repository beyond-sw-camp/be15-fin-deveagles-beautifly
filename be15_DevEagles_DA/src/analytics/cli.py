"""Command Line Interface for analytics service."""

import asyncio
from typing import Optional

import typer
import uvicorn
from rich.console import Console
from rich.table import Table
from sqlalchemy import text

from analytics.core.config import settings, get_settings
from analytics.core.logging import get_logger
from analytics.core.database import get_analytics_db, get_crm_db

app = typer.Typer(
    name="analytics",
    help="DevEagles Customer Analytics Service CLI",
    no_args_is_help=True,
)
console = Console()
logger = get_logger("cli")


@app.command()
def serve(
    host: str = typer.Option(default=None, help="Server host"),
    port: int = typer.Option(default=None, help="Server port"),
    workers: int = typer.Option(default=None, help="Number of workers"),
    reload: bool = typer.Option(default=None, help="Enable auto-reload"),
    log_level: str = typer.Option(default=None, help="Log level"),
    env: str = typer.Option(default=None, help="Environment (development/staging/production)"),
) -> None:
    """Start the FastAPI server."""
    # Override environment if specified
    if env:
        import os
        os.environ["ANALYTICS_ENVIRONMENT"] = env
        # Reload settings to pick up new environment
        from analytics.core.config import get_settings
        get_settings.cache_clear()
        settings = get_settings()
    
    # Use CLI args or fall back to settings
    actual_host = host or settings.host
    actual_port = port or settings.port
    actual_workers = workers or settings.workers
    actual_reload = reload if reload is not None else settings.reload
    actual_log_level = (log_level or settings.log_level).lower()
    
    console.print(f"🚀 Starting {settings.app_name} v{settings.app_version}")
    console.print(f"🌍 Environment: {settings.environment}")
    console.print(f"📍 Server will be available at http://{actual_host}:{actual_port}")
    
    if actual_reload:
        console.print("🔄 Auto-reload enabled (development mode)")
    
    uvicorn.run(
        "analytics.api.main:app",
        host=actual_host,
        port=actual_port,
        workers=actual_workers if not actual_reload else 1,
        reload=actual_reload,
        log_level=actual_log_level,
        access_log=True,
    )


@app.command()
def etl(
    full: bool = typer.Option(default=False, help="Run full ETL (not incremental)"),
    spark: bool = typer.Option(default=False, help="Use Spark for large data processing"),
    dry_run: bool = typer.Option(default=False, help="Dry run mode"),
) -> None:
    """Run ETL pipeline."""
    from analytics.etl.pipeline import create_pipeline
    from analytics.etl.config import DEVELOPMENT_ETL_CONFIG, DEFAULT_ETL_CONFIG
    from datetime import datetime
    
    console.print("🔄 Starting ETL pipeline...")
    console.print(f"Mode: {'Full' if full else 'Incremental'}")
    console.print(f"Engine: {'Spark' if spark else 'Pandas'}")
    console.print(f"Dry Run: {dry_run}")
    
    try:
        if dry_run:
            console.print("🧪 Running in dry-run mode - validation only")
            # 파이프라인 생성 테스트만 수행
            pipeline = create_pipeline(use_spark=spark, config=DEVELOPMENT_ETL_CONFIG)
            console.print("✅ ETL pipeline validation completed")
            return
        
        # 개발 환경에서는 작은 배치 크기 사용
        config = DEVELOPMENT_ETL_CONFIG if not spark else DEFAULT_ETL_CONFIG
        pipeline = create_pipeline(use_spark=spark, config=config)
        
        start_time = datetime.now()
        results = asyncio.run(pipeline.run(incremental=not full))
        end_time = datetime.now()
        
        # 결과 테이블 생성
        table = Table(title="ETL Results")
        table.add_column("Step", style="cyan")
        table.add_column("Status", style="green")
        table.add_column("Processed", style="yellow")
        table.add_column("Time (s)", style="magenta")
        
        total_processed = 0
        failed_steps = []
        
        for step, result in results.items():
            if result.success:
                status = "✅ Success"
                total_processed += result.records_processed
            else:
                status = "❌ Failed"
                failed_steps.append(step)
            
            table.add_row(
                step,
                status,
                str(result.records_processed),
                f"{result.processing_time_seconds:.2f}"
            )
        
        console.print(table)
        
        # 요약 정보
        summary_table = Table(title="Summary")
        summary_table.add_column("Metric", style="cyan")
        summary_table.add_column("Value", style="green")
        
        summary_table.add_row("Total Duration", f"{(end_time - start_time).total_seconds():.2f}s")
        summary_table.add_row("Total Records", str(total_processed))
        summary_table.add_row("Failed Steps", str(len(failed_steps)))
        
        console.print(summary_table)
        
        if failed_steps:
            console.print(f"❌ Failed steps: {', '.join(failed_steps)}", style="red")
            raise typer.Exit(1)
        else:
            console.print("✅ All ETL steps completed successfully!", style="green")
        
    except Exception as e:
        console.print(f"❌ ETL failed: {e}", style="red")
        logger.error(f"ETL failed: {e}")
        raise typer.Exit(1)


@app.command()
def train(
    model_type: str = typer.Option(default="all", help="Model type to train"),
    force: bool = typer.Option(default=False, help="Force retrain even if model exists"),
) -> None:
    """Train ML models."""
    from analytics.ml.training import ModelTrainer
    
    console.print("🎯 Starting model training...")
    
    try:
        trainer = ModelTrainer()
        
        if model_type == "all":
            models = ["churn", "segmentation"]
        else:
            models = [model_type]
        
        for model in models:
            console.print(f"Training {model} model...")
            result = asyncio.run(trainer.train(model, force=force))
            
            console.print(f"✅ {model} model trained successfully")
            console.print(f"   Score: {result.get('score', 0):.3f}")
            console.print(f"   Version: {result.get('version', 'unknown')}")
    
    except Exception as e:
        console.print(f"❌ Training failed: {e}", style="red")
        logger.error(f"Training failed: {e}")
        raise typer.Exit(1)


@app.command()
def tag(
    customer_id: Optional[int] = typer.Option(default=None, help="Tag specific customer"),
    dry_run: bool = typer.Option(default=False, help="Dry run mode"),
) -> None:
    """Run customer tagging."""
    from analytics.services.tagging import TaggingService
    
    console.print("🏷️ Starting customer tagging...")
    
    try:
        service = TaggingService()
        
        if customer_id:
            result = asyncio.run(service.tag_customer(customer_id, dry_run=dry_run))
            console.print(f"✅ Customer {customer_id} tagged: {result}")
        else:
            result = asyncio.run(service.tag_all_customers(dry_run=dry_run))
            console.print(f"✅ Tagged {result.get('customers_tagged', 0)} customers")
    
    except Exception as e:
        console.print(f"❌ Tagging failed: {e}", style="red")
        logger.error(f"Tagging failed: {e}")
        raise typer.Exit(1)


@app.command()
def segment(
    customer_id: Optional[int] = typer.Option(default=None, help="Segment specific customer"),
    show_distribution: bool = typer.Option(default=False, help="Show segment distribution"),
    show_insights: Optional[str] = typer.Option(default=None, help="Show insights for specific segment"),
    show_trends: bool = typer.Option(default=False, help="Show segment trends"),
) -> None:
    """Run customer segmentation."""
    from analytics.services.segmentation import CustomerSegmentationService
    
    console.print("🎯 Customer Segmentation")
    
    try:
        service = CustomerSegmentationService()
        
        if customer_id:
            # 특정 고객 세그멘테이션
            segment = service.segment_customer(customer_id)
            console.print(f"✅ Customer {customer_id} segmented as: [bold]{segment}[/bold]")
            
        elif show_distribution:
            # 세그먼트 분포 보기
            distribution = service.get_segment_distribution()
            
            table = Table(title="Customer Segment Distribution")
            table.add_column("Segment", style="cyan")
            table.add_column("Count", style="green")
            table.add_column("Avg Visits", style="yellow")
            table.add_column("Avg Amount", style="magenta")
            table.add_column("Avg Days Since Visit", style="blue")
            
            for segment, stats in distribution.items():
                table.add_row(
                    segment.upper(),
                    str(stats['count']),
                    str(stats['avg_visits']),
                    f"{stats['avg_amount']:,.0f}",
                    str(stats['avg_days_since_visit'])
                )
            
            console.print(table)
            
        elif show_insights:
            # 특정 세그먼트 인사이트
            insights = service.get_segment_insights(show_insights)
            
            if "error" in insights:
                console.print(f"❌ {insights['error']}", style="red")
                return
            
            console.print(f"\n📊 [bold]{show_insights.upper()}[/bold] Segment Insights")
            console.print(f"Total Customers: {insights['total_customers']}")
            console.print(f"Average Visits: {insights['avg_visits']}")
            console.print(f"Average Amount: {insights['avg_amount']:,.0f}")
            console.print(f"Average Days Since Visit: {insights['avg_days_since_visit']}")
            console.print(f"Amount Range: {insights['amount_range']['min']:,.0f} - {insights['amount_range']['max']:,.0f}")
            
            console.print("\n🎯 [bold]Recommended Actions:[/bold]")
            for i, action in enumerate(insights['recommended_actions'], 1):
                console.print(f"  {i}. {action}")
                
        elif show_trends:
            # 세그먼트 트렌드 분석
            trends = service.analyze_segment_trends()
            
            console.print("\n📈 [bold]Segment Trends (Last 30 days)[/bold]")
            console.print(f"Total Customers: {trends['total_customers']}")
            
            if trends['recent_changes']:
                console.print("\n🔄 Recent Segment Changes:")
                for segment, count in trends['recent_changes'].items():
                    console.print(f"  {segment}: {count} customers")
            else:
                console.print("\n🔄 No recent segment changes")
                
        else:
            # 전체 고객 세그멘테이션
            console.print("🔄 Running segmentation for all customers...")
            results = service.segment_all_customers()
            
            if not results:
                console.print("⚠️ No customers found for segmentation")
                return
            
            table = Table(title="Segmentation Results")
            table.add_column("Segment", style="cyan")
            table.add_column("Customer Count", style="green")
            
            total_customers = sum(results.values())
            
            for segment, count in results.items():
                percentage = (count / total_customers * 100) if total_customers > 0 else 0
                table.add_row(
                    segment.upper(),
                    f"{count} ({percentage:.1f}%)"
                )
            
            console.print(table)
            console.print(f"\n✅ Segmentation completed for {total_customers} customers")
    
    except Exception as e:
        console.print(f"❌ Segmentation failed: {e}", style="red")
        logger.error(f"Segmentation failed: {e}")
        raise typer.Exit(1)


@app.command()
def status() -> None:
    """Show service status and health."""
    console.print(f"📊 {settings.app_name} Status")
    
    table = Table()
    table.add_column("Component", style="cyan")
    table.add_column("Status", style="green")
    table.add_column("Details", style="yellow")
    
    # Check database connectivity
    try:
        from analytics.core.database import get_analytics_db
        db = get_analytics_db()
        db.execute("SELECT 1").fetchone()
        table.add_row("Analytics DB", "✅ Connected", f"Path: {settings.analytics_db_path}")
    except Exception as e:
        table.add_row("Analytics DB", "❌ Error", str(e))
    
    # Check CRM database connectivity
    try:
        from analytics.core.database import get_crm_db
        engine = get_crm_db()
        with engine.connect() as conn:
            conn.execute("SELECT 1")
        table.add_row("CRM DB", "✅ Connected", "MariaDB connection OK")
    except Exception as e:
        table.add_row("CRM DB", "❌ Error", str(e))
    
    # Check model availability
    try:
        from analytics.ml.model_manager import ModelManager
        manager = ModelManager()
        models = manager.list_models()
        table.add_row("ML Models", "✅ Available", f"{len(models)} models")
    except Exception as e:
        table.add_row("ML Models", "❌ Error", str(e))
    
    console.print(table)


@app.command()
def config(
    env: str = typer.Option(default=None, help="Environment (development/staging/production)"),
) -> None:
    """Show current configuration."""
    # Override environment if specified
    if env:
        import os
        os.environ["ANALYTICS_ENVIRONMENT"] = env
        # Reload settings to pick up new environment
        from analytics.core.config import get_settings
        get_settings.cache_clear()
        current_settings = get_settings()
    else:
        current_settings = settings
    
    console.print(f"⚙️ {current_settings.app_name} Configuration")
    
    table = Table()
    table.add_column("Setting", style="cyan")
    table.add_column("Value", style="green")
    
    # Environment
    table.add_row("Environment", current_settings.environment)
    
    # Core settings
    table.add_row("App Name", current_settings.app_name)
    table.add_row("Version", current_settings.app_version)
    table.add_row("Debug Mode", str(current_settings.debug))
    table.add_row("Log Level", current_settings.log_level)
    
    # Server settings
    table.add_row("Host", current_settings.host)
    table.add_row("Port", str(current_settings.port))
    table.add_row("Workers", str(current_settings.workers))
    table.add_row("Reload", str(current_settings.reload))
    
    # Database settings
    table.add_row("Analytics DB", current_settings.analytics_db_path)
    table.add_row("CRM DB URL", current_settings.crm_database_url.replace("password", "***"))  # 비밀번호 마스킹
    
    # ML settings
    table.add_row("Model Storage", current_settings.ml.model_storage_path)
    table.add_row("Retrain Threshold", str(current_settings.ml.retrain_threshold))
    
    # ETL settings
    table.add_row("ETL Batch Size", str(current_settings.etl.batch_size))
    table.add_row("ETL Incremental", str(current_settings.etl.incremental))
    
    # API settings
    table.add_row("API Prefix", current_settings.api_prefix)
    table.add_row("Docs URL", str(current_settings.docs_url))
    
    # Monitoring
    table.add_row("Metrics Enabled", str(current_settings.enable_metrics))
    
    console.print(table)


@app.command()
def init_db():
    """데이터베이스 초기화."""
    console.print("[bold blue]Analytics 데이터베이스 초기화...[/bold blue]")
    
    try:
        from analytics.core.database import get_analytics_db
        conn = get_analytics_db()
        
        # 기본 테이블들 생성
        tables_sql = [
            """
            CREATE TABLE IF NOT EXISTS customer_analytics (
                customer_id INTEGER PRIMARY KEY,
                name VARCHAR NOT NULL,
                phone VARCHAR NOT NULL,
                email VARCHAR,
                birth_date DATE,
                gender VARCHAR,
                first_visit_date TIMESTAMP,
                last_visit_date TIMESTAMP,
                total_visits INTEGER DEFAULT 0,
                total_amount DECIMAL(10,2) DEFAULT 0.00,
                avg_visit_amount DECIMAL(10,2) DEFAULT 0.00,
                lifecycle_days INTEGER DEFAULT 0,
                days_since_last_visit INTEGER,
                visit_frequency DOUBLE DEFAULT 0.0,
                preferred_services VARCHAR[],
                preferred_employees VARCHAR[],
                visits_3m INTEGER DEFAULT 0,
                amount_3m DECIMAL(10,2) DEFAULT 0.00,
                segment VARCHAR DEFAULT 'new',
                segment_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                churn_risk_score DOUBLE DEFAULT 0.0,
                churn_risk_level VARCHAR DEFAULT 'low',
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS visit_analytics (
                visit_id INTEGER PRIMARY KEY,
                customer_id INTEGER NOT NULL,
                employee_id INTEGER,
                visit_date TIMESTAMP NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                discount_amount DECIMAL(10,2) DEFAULT 0.00,
                final_amount DECIMAL(10,2) NOT NULL,
                service_count INTEGER DEFAULT 0,
                service_categories VARCHAR[],
                service_names VARCHAR[],
                duration_minutes INTEGER,
                is_first_visit BOOLEAN DEFAULT FALSE,
                days_since_previous_visit INTEGER,
                visit_sequence INTEGER DEFAULT 1,
                visit_hour INTEGER DEFAULT 0,
                visit_weekday INTEGER DEFAULT 0,
                visit_month INTEGER DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS etl_metadata (
                table_name VARCHAR PRIMARY KEY,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                records_processed INTEGER DEFAULT 0,
                records_inserted INTEGER DEFAULT 0,
                records_updated INTEGER DEFAULT 0,
                records_deleted INTEGER DEFAULT 0,
                processing_time_seconds DOUBLE DEFAULT 0.0,
                status VARCHAR DEFAULT 'completed',
                error_message VARCHAR
            )
            """
        ]
        
        for sql in tables_sql:
            conn.execute(sql)
        
        # 초기 메타데이터 삽입
        conn.execute("""
            INSERT OR REPLACE INTO etl_metadata (table_name, status) 
            VALUES 
                ('customer_analytics', 'initialized'),
                ('visit_analytics', 'initialized')
        """)
        
        console.print("[green]✓ Analytics 데이터베이스 초기화 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]Analytics 데이터베이스 초기화 실패: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def create_sample():
    """샘플 데이터 생성 (테스트용)."""
    console.print("[bold blue]샘플 데이터 생성...[/bold blue]")
    
    try:
        from datetime import datetime, timedelta
        import random
        from analytics.core.database import get_analytics_db
        
        conn = get_analytics_db()
        
        # 샘플 고객 데이터
        customers = []
        for i in range(1, 11):
            customers.append({
                'customer_id': i,
                'name': f'고객{i}',
                'phone': f'010-0000-{i:04d}',
                'email': f'customer{i}@example.com',
                'birth_date': f'199{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
                'gender': random.choice(['M', 'F']),
                'first_visit_date': datetime.now() - timedelta(days=random.randint(30, 365)),
                'last_visit_date': datetime.now() - timedelta(days=random.randint(1, 30)),
                'total_visits': random.randint(1, 20),
                'total_amount': random.randint(50000, 500000),
                'segment': random.choice(['new', 'growing', 'loyal', 'vip'])
            })
        
        # 고객 데이터 삽입
        for customer in customers:
            conn.execute("""
                INSERT OR REPLACE INTO customer_analytics 
                (customer_id, name, phone, email, birth_date, gender, 
                 first_visit_date, last_visit_date, total_visits, total_amount, segment)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                customer['customer_id'], customer['name'], customer['phone'], 
                customer['email'], customer['birth_date'], customer['gender'],
                customer['first_visit_date'], customer['last_visit_date'],
                customer['total_visits'], customer['total_amount'], customer['segment']
            ))
        
        console.print(f"[green]✓ {len(customers)}개의 샘플 고객 데이터 생성 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]샘플 데이터 생성 실패: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def test_db():
    """데이터베이스 연결 테스트."""
    settings = get_settings()
    console.print("[bold blue]데이터베이스 연결 테스트 시작...[/bold blue]")
    
    # CRM 데이터베이스 테스트
    console.print("\n[yellow]1. CRM 데이터베이스 연결 테스트[/yellow]")
    try:
        engine = get_crm_db()
        with engine.connect() as conn:
            result = conn.execute(text("SELECT 1 as test"))
            row = result.fetchone()
            if row and row[0] == 1:
                console.print("[green]✓ CRM 데이터베이스 연결 성공[/green]")
            else:
                console.print("[red]✗ CRM 데이터베이스 연결 실패[/red]")
                return
    except Exception as e:
        console.print(f"[red]✗ CRM 데이터베이스 연결 실패: {e}[/red]")
        return
    
    # Analytics 데이터베이스 테스트  
    console.print("\n[yellow]2. Analytics 데이터베이스 연결 테스트[/yellow]")
    try:
        conn = get_analytics_db()
        result = conn.execute("SELECT 1 as test").fetchone()
        if result and result[0] == 1:
            console.print("[green]✓ Analytics 데이터베이스 연결 성공[/green]")
        else:
            console.print("[red]✗ Analytics 데이터베이스 연결 실패[/red]")
            return
    except Exception as e:
        console.print(f"[red]✗ Analytics 데이터베이스 연결 실패: {e}[/red]")
        return
    
    console.print("\n[bold green]모든 데이터베이스 연결 테스트 통과! 🎉[/bold green]")


@app.command()
def check_crm_tables():
    """CRM 데이터베이스 테이블 확인."""
    console.print("[bold blue]CRM 데이터베이스 테이블 확인...[/bold blue]")
    
    try:
        engine = get_crm_db()
        with engine.connect() as conn:
            # 테이블 목록 조회
            result = conn.execute(text("SHOW TABLES"))
            tables = [row[0] for row in result.fetchall()]
            
            if not tables:
                console.print("[yellow]테이블이 없습니다.[/yellow]")
                return
            
            table = Table(title="CRM Database Tables")
            table.add_column("Table Name", style="cyan")
            table.add_column("Row Count", style="green")
            
            for table_name in tables:
                try:
                    count_result = conn.execute(text(f"SELECT COUNT(*) FROM {table_name}"))
                    count = count_result.fetchone()[0]
                    table.add_row(table_name, str(count))
                except Exception as e:
                    table.add_row(table_name, f"Error: {e}")
            
            console.print(table)
            
    except Exception as e:
        console.print(f"[red]CRM 테이블 확인 실패: {e}[/red]")


@app.command()
def init_analytics_db():
    """Analytics 데이터베이스 초기화."""
    console.print("[bold blue]Analytics 데이터베이스 초기화...[/bold blue]")
    
    try:
        conn = get_analytics_db()
        
        # 기본 테이블들 생성
        tables_sql = [
            """
            CREATE TABLE IF NOT EXISTS customer_analytics (
                customer_id INTEGER PRIMARY KEY,
                name VARCHAR NOT NULL,
                phone VARCHAR NOT NULL,
                email VARCHAR,
                birth_date DATE,
                gender VARCHAR,
                first_visit_date TIMESTAMP,
                last_visit_date TIMESTAMP,
                total_visits INTEGER DEFAULT 0,
                total_amount DECIMAL(10,2) DEFAULT 0.00,
                avg_visit_amount DECIMAL(10,2) DEFAULT 0.00,
                lifecycle_days INTEGER DEFAULT 0,
                days_since_last_visit INTEGER,
                visit_frequency DOUBLE DEFAULT 0.0,
                preferred_services VARCHAR[],
                preferred_employees VARCHAR[],
                visits_3m INTEGER DEFAULT 0,
                amount_3m DECIMAL(10,2) DEFAULT 0.00,
                segment VARCHAR DEFAULT 'new',
                segment_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                churn_risk_score DOUBLE DEFAULT 0.0,
                churn_risk_level VARCHAR DEFAULT 'low',
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS visit_analytics (
                visit_id INTEGER PRIMARY KEY,
                customer_id INTEGER NOT NULL,
                employee_id INTEGER,
                visit_date TIMESTAMP NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                discount_amount DECIMAL(10,2) DEFAULT 0.00,
                final_amount DECIMAL(10,2) NOT NULL,
                service_count INTEGER DEFAULT 0,
                service_categories VARCHAR[],
                service_names VARCHAR[],
                duration_minutes INTEGER,
                is_first_visit BOOLEAN DEFAULT FALSE,
                days_since_previous_visit INTEGER,
                visit_sequence INTEGER DEFAULT 1,
                visit_hour INTEGER DEFAULT 0,
                visit_weekday INTEGER DEFAULT 0,
                visit_month INTEGER DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS etl_metadata (
                table_name VARCHAR PRIMARY KEY,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                records_processed INTEGER DEFAULT 0,
                records_inserted INTEGER DEFAULT 0,
                records_updated INTEGER DEFAULT 0,
                records_deleted INTEGER DEFAULT 0,
                processing_time_seconds DOUBLE DEFAULT 0.0,
                status VARCHAR DEFAULT 'completed',
                error_message VARCHAR
            )
            """
        ]
        
        for sql in tables_sql:
            conn.execute(sql)
        
        # 초기 메타데이터 삽입
        conn.execute("""
            INSERT OR REPLACE INTO etl_metadata (table_name, status) 
            VALUES 
                ('customer_analytics', 'initialized'),
                ('visit_analytics', 'initialized')
        """)
        
        console.print("[green]✓ Analytics 데이터베이스 초기화 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]Analytics 데이터베이스 초기화 실패: {e}[/red]")


@app.command()
def create_sample_data():
    """샘플 데이터 생성 (테스트용)."""
    console.print("[bold blue]샘플 데이터 생성...[/bold blue]")
    
    try:
        from datetime import datetime, timedelta
        import random
        
        conn = get_analytics_db()
        
        # 샘플 고객 데이터
        customers = []
        for i in range(1, 11):
            customers.append({
                'customer_id': i,
                'name': f'고객{i}',
                'phone': f'010-0000-{i:04d}',
                'email': f'customer{i}@example.com',
                'birth_date': f'199{random.randint(0,9)}-{random.randint(1,12):02d}-{random.randint(1,28):02d}',
                'gender': random.choice(['M', 'F']),
                'first_visit_date': datetime.now() - timedelta(days=random.randint(30, 365)),
                'last_visit_date': datetime.now() - timedelta(days=random.randint(1, 30)),
                'total_visits': random.randint(1, 20),
                'total_amount': random.randint(50000, 500000),
                'segment': random.choice(['new', 'growing', 'loyal', 'vip'])
            })
        
        # 고객 데이터 삽입
        for customer in customers:
            conn.execute("""
                INSERT OR REPLACE INTO customer_analytics 
                (customer_id, name, phone, email, birth_date, gender, 
                 first_visit_date, last_visit_date, total_visits, total_amount, segment)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                customer['customer_id'], customer['name'], customer['phone'], 
                customer['email'], customer['birth_date'], customer['gender'],
                customer['first_visit_date'], customer['last_visit_date'],
                customer['total_visits'], customer['total_amount'], customer['segment']
            ))
        
        console.print(f"[green]✓ {len(customers)}개의 샘플 고객 데이터 생성 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]샘플 데이터 생성 실패: {e}[/red]")


@app.command()
def show_analytics_data():
    """Analytics 데이터 확인."""
    console.print("[bold blue]Analytics 데이터 확인...[/bold blue]")
    
    try:
        conn = get_analytics_db()
        
        # 고객 분석 데이터 조회
        result = conn.execute("""
            SELECT customer_id, name, segment, total_visits, total_amount, churn_risk_level
            FROM customer_analytics 
            ORDER BY customer_id 
            LIMIT 10
        """).fetchall()
        
        if not result:
            console.print("[yellow]분석 데이터가 없습니다. create-sample-data 명령어를 먼저 실행하세요.[/yellow]")
            return
        
        table = Table(title="Customer Analytics Data")
        table.add_column("ID", style="cyan")
        table.add_column("Name", style="green")
        table.add_column("Segment", style="yellow")
        table.add_column("Visits", style="blue")
        table.add_column("Amount", style="magenta")
        table.add_column("Risk Level", style="red")
        
        for row in result:
            table.add_row(
                str(row[0]), row[1], row[2], 
                str(row[3]), f"{row[4]:,}", row[5]
            )
        
        console.print(table)
        
    except Exception as e:
        console.print(f"[red]Analytics 데이터 조회 실패: {e}[/red]")


@app.command()  
def cleanup():
    """리소스 정리."""
    console.print("[bold blue]리소스 정리 중...[/bold blue]")
    try:
        get_analytics_db().close()
        console.print("[green]✓ 데이터베이스 연결 정리 완료[/green]")
    except Exception as e:
        console.print(f"[red]리소스 정리 실패: {e}[/red]")


@app.command()
def add_preference_tables():
    """선호 시술 분석 테이블 추가."""
    console.print("[bold blue]선호 시술 분석 테이블 추가...[/bold blue]")
    
    try:
        from analytics.core.database import get_analytics_db
        conn = get_analytics_db()
        
        # 선호 시술 분석 테이블들 생성
        preference_tables_sql = [
            """
            CREATE TABLE IF NOT EXISTS customer_service_preferences (
                customer_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                service_name VARCHAR NOT NULL,
                service_category VARCHAR,
                total_visits INTEGER DEFAULT 0,
                total_amount DECIMAL(10,2) DEFAULT 0.00,
                avg_amount DECIMAL(10,2) DEFAULT 0.00,
                first_service_date TIMESTAMP,
                last_service_date TIMESTAMP,
                preference_rank INTEGER DEFAULT 1,
                visit_ratio DOUBLE DEFAULT 0.0,
                amount_ratio DOUBLE DEFAULT 0.0,
                recent_visits_3m INTEGER DEFAULT 0,
                days_since_last_service INTEGER,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (customer_id, service_id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS customer_service_tags (
                customer_id INTEGER PRIMARY KEY,
                top_service_1 VARCHAR,
                top_service_2 VARCHAR,
                top_service_3 VARCHAR,
                preferred_categories VARCHAR[],
                service_variety_score DOUBLE DEFAULT 0.0,
                loyalty_services VARCHAR[],
                avg_service_price DECIMAL(10,2) DEFAULT 0.00,
                preferred_price_range VARCHAR DEFAULT 'medium',
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """
        ]
        
        for sql in preference_tables_sql:
            conn.execute(sql)
        
        # 메타데이터 업데이트
        conn.execute("""
            INSERT OR REPLACE INTO etl_metadata (table_name, status) 
            VALUES 
                ('customer_service_preferences', 'initialized'),
                ('customer_service_tags', 'initialized')
        """)
        
        console.print("[green]✓ 선호 시술 분석 테이블 추가 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]선호 시술 분석 테이블 추가 실패: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def create_sample_preferences():
    """선호 시술 샘플 데이터 생성."""
    console.print("[bold blue]선호 시술 샘플 데이터 생성...[/bold blue]")
    
    try:
        from datetime import datetime, timedelta
        import random
        from analytics.core.database import get_analytics_db
        
        conn = get_analytics_db()
        
        # 샘플 서비스 목록
        services = [
            {"id": 1, "name": "컷", "category": "헤어"},
            {"id": 2, "name": "펌", "category": "헤어"},
            {"id": 3, "name": "염색", "category": "헤어"},
            {"id": 4, "name": "트리트먼트", "category": "헤어"},
            {"id": 5, "name": "페이셜", "category": "스킨케어"},
            {"id": 6, "name": "마사지", "category": "스킨케어"},
            {"id": 7, "name": "네일아트", "category": "네일"},
            {"id": 8, "name": "젤네일", "category": "네일"},
            {"id": 9, "name": "속눈썹연장", "category": "아이케어"},
            {"id": 10, "name": "눈썹정리", "category": "아이케어"}
        ]
        
        # 고객별 선호 시술 데이터 생성 (고객 1-10)
        for customer_id in range(1, 11):
            # 각 고객마다 3-6개의 서비스 이용 기록 생성
            customer_services = random.sample(services, random.randint(3, 6))
            
            total_customer_visits = 0
            total_customer_amount = 0
            preferences = []
            
            for rank, service in enumerate(customer_services, 1):
                visits = random.randint(1, 10)
                amount_per_visit = random.randint(30000, 150000)
                total_amount = visits * amount_per_visit
                
                total_customer_visits += visits
                total_customer_amount += total_amount
                
                preferences.append({
                    'customer_id': customer_id,
                    'service_id': service['id'],
                    'service_name': service['name'],
                    'service_category': service['category'],
                    'total_visits': visits,
                    'total_amount': total_amount,
                    'avg_amount': amount_per_visit,
                    'first_service_date': datetime.now() - timedelta(days=random.randint(100, 300)),
                    'last_service_date': datetime.now() - timedelta(days=random.randint(1, 30)),
                    'preference_rank': rank,
                    'recent_visits_3m': random.randint(0, visits),
                    'days_since_last_service': random.randint(1, 30)
                })
            
            # 비율 계산 및 데이터 삽입
            for pref in preferences:
                pref['visit_ratio'] = pref['total_visits'] / total_customer_visits
                pref['amount_ratio'] = pref['total_amount'] / total_customer_amount
                
                conn.execute("""
                    INSERT OR REPLACE INTO customer_service_preferences 
                    (customer_id, service_id, service_name, service_category, 
                     total_visits, total_amount, avg_amount, first_service_date, 
                     last_service_date, preference_rank, visit_ratio, amount_ratio,
                     recent_visits_3m, days_since_last_service)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (
                    pref['customer_id'], pref['service_id'], pref['service_name'],
                    pref['service_category'], pref['total_visits'], pref['total_amount'],
                    pref['avg_amount'], pref['first_service_date'], pref['last_service_date'],
                    pref['preference_rank'], pref['visit_ratio'], pref['amount_ratio'],
                    pref['recent_visits_3m'], pref['days_since_last_service']
                ))
            
            # 고객 서비스 태그 생성
            top_services = sorted(preferences, key=lambda x: x['preference_rank'])[:3]
            categories = list(set([p['service_category'] for p in preferences]))
            avg_price = sum([p['avg_amount'] for p in preferences]) / len(preferences)
            
            price_range = "low" if avg_price < 50000 else "high" if avg_price > 100000 else "medium"
            variety_score = len(preferences) / 10.0  # 최대 10개 서비스 기준
            
            conn.execute("""
                INSERT OR REPLACE INTO customer_service_tags 
                (customer_id, top_service_1, top_service_2, top_service_3,
                 preferred_categories, service_variety_score, avg_service_price, preferred_price_range)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                customer_id,
                top_services[0]['service_name'] if len(top_services) > 0 else None,
                top_services[1]['service_name'] if len(top_services) > 1 else None,
                top_services[2]['service_name'] if len(top_services) > 2 else None,
                categories,
                variety_score,
                avg_price,
                price_range
            ))
        
        console.print("[green]✓ 10명 고객의 선호 시술 샘플 데이터 생성 완료[/green]")
        
    except Exception as e:
        console.print(f"[red]선호 시술 샘플 데이터 생성 실패: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def preference(
    customer_id: Optional[int] = typer.Option(default=None, help="특정 고객 선호도 분석"),
    show_ranking: bool = typer.Option(default=False, help="서비스 인기도 순위 표시"),
    show_categories: bool = typer.Option(default=False, help="카테고리별 분포 표시"),
    show_trends: bool = typer.Option(default=False, help="서비스 트렌드 분석"),
    recommendations: bool = typer.Option(default=False, help="고객 추천 서비스"),
    similar_customers: bool = typer.Option(default=False, help="유사한 고객 찾기"),
    journey: bool = typer.Option(default=False, help="고객 서비스 여정 분석"),
) -> None:
    """고객 선호 시술 분석."""
    from analytics.services.preference import CustomerServicePreferenceService
    
    console.print("🎨 고객 선호 시술 분석")
    
    try:
        service = CustomerServicePreferenceService()
        
        if customer_id:
            # 특정 고객 분석
            console.print(f"\n📊 고객 {customer_id} 선호 시술 분석")
            
            # 선호 시술 목록
            preferences = service.get_customer_preferences(customer_id)
            if preferences:
                table = Table(title=f"고객 {customer_id} 선호 시술")
                table.add_column("순위", style="cyan")
                table.add_column("서비스명", style="green")
                table.add_column("카테고리", style="yellow")
                table.add_column("방문횟수", style="magenta")
                table.add_column("총금액", style="red")
                table.add_column("평균금액", style="blue")
                table.add_column("방문비율", style="cyan")
                
                for pref in preferences:
                    table.add_row(
                        str(pref['preference_rank']),
                        pref['service_name'],
                        pref['service_category'],
                        str(pref['total_visits']),
                        f"{pref['total_amount']:,.0f}원",
                        f"{pref['avg_amount']:,.0f}원",
                        f"{pref['visit_ratio']:.1%}"
                    )
                
                console.print(table)
            
            # 서비스 태그
            tags = service.get_customer_service_tags(customer_id)
            if tags:
                console.print(f"\n🏷️ 고객 {customer_id} 서비스 태그")
                console.print(f"• 선호 서비스: {tags['top_service_1']}, {tags['top_service_2']}, {tags['top_service_3']}")
                console.print(f"• 선호 카테고리: {', '.join(tags['preferred_categories'])}")
                console.print(f"• 다양성 점수: {tags['service_variety_score']:.2f}")
                console.print(f"• 평균 가격대: {tags['avg_service_price']:,.0f}원 ({tags['preferred_price_range']})")
                console.print(f"• 모든 태그: {', '.join(tags['all_preference_tags'])}")
            
            # 추천 서비스
            if recommendations:
                recs = service.get_service_recommendations(customer_id)
                if recs:
                    console.print(f"\n💡 고객 {customer_id} 추천 서비스")
                    rec_table = Table()
                    rec_table.add_column("서비스명", style="green")
                    rec_table.add_column("카테고리", style="yellow")
                    rec_table.add_column("추천점수", style="red")
                    rec_table.add_column("이유", style="cyan")
                    
                    for rec in recs:
                        rec_table.add_row(
                            rec['service_name'],
                            rec['service_category'],
                            f"{rec['recommendation_score']:.2f}",
                            rec['reason']
                        )
                    
                    console.print(rec_table)
            
            # 유사한 고객
            if similar_customers:
                similar = service.find_similar_customers(customer_id)
                if similar:
                    console.print(f"\n👥 고객 {customer_id}와 유사한 고객")
                    sim_table = Table()
                    sim_table.add_column("고객ID", style="cyan")
                    sim_table.add_column("공통서비스수", style="green")
                    sim_table.add_column("유사도점수", style="red")
                    sim_table.add_column("공통서비스", style="yellow")
                    
                    for sim in similar:
                        sim_table.add_row(
                            str(sim['customer_id']),
                            str(sim['common_services_count']),
                            f"{sim['similarity_score']:.2f}",
                            ', '.join(sim['common_services'][:3])  # 상위 3개만
                        )
                    
                    console.print(sim_table)
            
            # 고객 여정 분석
            if journey:
                journey_data = service.get_customer_journey_analysis(customer_id)
                if journey_data:
                    console.print(f"\n🛣️ 고객 {customer_id} 서비스 여정")
                    console.print(f"• 총 시도한 서비스: {journey_data['total_services_tried']}개")
                    console.print(f"• 선호도 진화: {journey_data['preference_evolution']['description']}")
                    console.print(f"• 충성도 패턴: {journey_data['loyalty_pattern']['description']} (점수: {journey_data['loyalty_pattern']['score']:.2f})")
                    console.print(f"• 지출 패턴: {journey_data['spending_pattern']['description']}")
        
        elif show_ranking:
            # 서비스 인기도 순위
            rankings = service.get_service_popularity_ranking()
            if rankings:
                console.print("\n🏆 서비스 인기도 순위")
                rank_table = Table()
                rank_table.add_column("순위", style="cyan")
                rank_table.add_column("서비스명", style="green")
                rank_table.add_column("카테고리", style="yellow")
                rank_table.add_column("고객수", style="magenta")
                rank_table.add_column("총방문", style="red")
                rank_table.add_column("총매출", style="blue")
                rank_table.add_column("인기점수", style="cyan")
                
                for rank in rankings:
                    rank_table.add_row(
                        str(rank['rank']),
                        rank['service_name'],
                        rank['service_category'],
                        str(rank['customer_count']),
                        str(rank['total_visits']),
                        f"{rank['total_revenue']:,.0f}원",
                        f"{rank['popularity_score']:.1f}"
                    )
                
                console.print(rank_table)
        
        elif show_categories:
            # 카테고리별 분포
            distribution = service.get_category_preferences_distribution()
            if distribution['categories']:
                console.print("\n📊 카테고리별 선호도 분포")
                cat_table = Table()
                cat_table.add_column("카테고리", style="green")
                cat_table.add_column("고객수", style="cyan")
                cat_table.add_column("고객비율", style="yellow")
                cat_table.add_column("총방문", style="magenta")
                cat_table.add_column("총매출", style="red")
                cat_table.add_column("매출비율", style="blue")
                
                for category, data in distribution['categories'].items():
                    cat_table.add_row(
                        category,
                        str(data['customer_count']),
                        f"{data['customer_ratio']:.1%}",
                        str(data['total_visits']),
                        f"{data['total_revenue']:,.0f}원",
                        f"{data['revenue_ratio']:.1%}"
                    )
                
                console.print(cat_table)
                console.print(f"\n📈 총 고객: {distribution['total_customers']}명, 총 매출: {distribution['total_revenue']:,.0f}원")
        
        elif show_trends:
            # 서비스 트렌드
            trends = service.analyze_service_trends()
            if trends['trends']:
                console.print(f"\n📈 서비스 트렌드 (최근 {trends['analysis_period_days']}일)")
                trend_table = Table()
                trend_table.add_column("서비스명", style="green")
                trend_table.add_column("카테고리", style="yellow")
                trend_table.add_column("현재고객", style="cyan")
                trend_table.add_column("최근방문", style="magenta")
                trend_table.add_column("활성고객", style="red")
                trend_table.add_column("활성률", style="blue")
                trend_table.add_column("트렌드점수", style="cyan")
                
                for trend in trends['trends']:
                    trend_table.add_row(
                        trend['service_name'],
                        trend['service_category'],
                        str(trend['current_customers']),
                        f"{trend['avg_recent_visits']:.1f}",
                        str(trend['active_customers']),
                        f"{trend['activity_rate']:.1%}",
                        f"{trend['trend_score']:.2f}"
                    )
                
                console.print(trend_table)
        
        else:
            # 전체 요약
            console.print("\n📋 선호 시술 분석 요약")
            console.print("사용 가능한 옵션:")
            console.print("• --customer-id <ID> : 특정 고객 분석")
            console.print("• --show-ranking : 서비스 인기도 순위")
            console.print("• --show-categories : 카테고리별 분포")
            console.print("• --show-trends : 서비스 트렌드")
            console.print("• --recommendations : 고객 추천 서비스 (--customer-id와 함께)")
            console.print("• --similar-customers : 유사한 고객 (--customer-id와 함께)")
            console.print("• --journey : 고객 여정 분석 (--customer-id와 함께)")
    
    except Exception as e:
        console.print(f"[red]선호 시술 분석 실패: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def scheduler(
    action: str = typer.Argument(..., help="Action: start, stop, status, run-now"),
    dry_run: bool = typer.Option(False, "--dry-run", help="Dry run mode (for run-now)"),
):
    """고객 이탈분석 스케줄러 관리."""
    console.print(f"[bold blue]고객 이탈분석 스케줄러 - {action.upper()}[/bold blue]")
    
    try:
        if action == "start":
            # 스케줄러 시작
            async def start_scheduler():
                from analytics.services.churn_analysis_scheduler import start_churn_analysis_scheduler
                await start_churn_analysis_scheduler()
                console.print("[green]✓ 스케줄러가 시작되었습니다.[/green]")
                console.print("스케줄러를 중지하려면 Ctrl+C를 누르세요.")
                
                # 무한 대기 (Ctrl+C로 중지)
                try:
                    while True:
                        await asyncio.sleep(60)  # 1분마다 체크
                except KeyboardInterrupt:
                    console.print("\n[yellow]스케줄러를 중지합니다...[/yellow]")
                    from analytics.services.churn_analysis_scheduler import stop_churn_analysis_scheduler
                    await stop_churn_analysis_scheduler()
                    console.print("[green]✓ 스케줄러가 중지되었습니다.[/green]")
            
            asyncio.run(start_scheduler())
            
        elif action == "stop":
            # 스케줄러 중지
            async def stop_scheduler():
                from analytics.services.churn_analysis_scheduler import stop_churn_analysis_scheduler
                await stop_churn_analysis_scheduler()
                console.print("[green]✓ 스케줄러가 중지되었습니다.[/green]")
            
            asyncio.run(stop_scheduler())
            
        elif action == "status":
            # 스케줄러 상태 및 최근 작업 조회
            async def show_status():
                from analytics.services.churn_analysis_scheduler import get_churn_scheduler
                scheduler = await get_churn_scheduler()
                
                # 설정 정보 표시
                config = scheduler.config
                console.print(f"\n[bold]스케줄러 설정:[/bold]")
                console.print(f"• 활성화: {config.enabled}")
                console.print(f"• 실행 시간: 매일 {config.schedule_hour:02d}:{config.schedule_minute:02d} ({config.timezone})")
                console.print(f"• 세그먼트 업데이트 방식: {config.segment_update_method}")
                console.print(f"• 자동 세그먼트 적용: {config.auto_apply_segments}")
                console.print(f"• 알림 활성화: {config.notification.enabled}")
                
                # 최근 작업 목록
                recent_jobs = scheduler.get_recent_jobs(5)
                if recent_jobs:
                    console.print(f"\n[bold]최근 작업 (최근 5개):[/bold]")
                    
                    table = Table()
                    table.add_column("작업 ID", style="cyan")
                    table.add_column("시작 시간", style="green")
                    table.add_column("상태", style="yellow")
                    table.add_column("고객수", style="blue")
                    table.add_column("고위험", style="red")
                    table.add_column("처리시간", style="magenta")
                    
                    for job in recent_jobs:
                        status_color = {
                            'completed': '[green]완료[/green]',
                            'completed_with_errors': '[yellow]완료(오류)[/yellow]',
                            'failed': '[red]실패[/red]',
                            'running': '[blue]실행중[/blue]'
                        }.get(job['status'], job['status'])
                        
                        table.add_row(
                            job['job_id'][-12:],  # 마지막 12자리만
                            job['start_time'][:16] if job['start_time'] else '',
                            status_color,
                            str(job['total_customers']),
                            str(job['high_risk_customers']),
                            f"{job['processing_time_seconds']:.1f}s"
                        )
                    
                    console.print(table)
                else:
                    console.print(f"\n[yellow]최근 작업이 없습니다.[/yellow]")
            
            asyncio.run(show_status())
            
        elif action == "run-now":
            # 즉시 실행
            async def run_now():
                from analytics.services.churn_analysis_scheduler import get_churn_scheduler
                scheduler = await get_churn_scheduler()
                
                console.print(f"[yellow]고객 이탈분석을 즉시 실행합니다... (dry_run={dry_run})[/yellow]")
                
                result = await scheduler.run_manual_analysis(dry_run=dry_run)
                
                if result['status'] == 'completed':
                    console.print("[green]✓ 분석이 완료되었습니다.[/green]")
                else:
                    console.print(f"[red]분석 실패: {result.get('message', 'Unknown error')}[/red]")
            
            asyncio.run(run_now())
            
        else:
            console.print(f"[red]Unknown action: {action}[/red]")
            console.print("Available actions: start, stop, status, run-now")
            raise typer.Exit(1)
    
    except Exception as e:
        console.print(f"[red]Error: {e}[/red]")
        raise typer.Exit(1)


@app.command()
def risk_tagging(
    customer_id: Optional[int] = typer.Option(None, "--customer-id", help="특정 고객 ID"),
    tag_all: bool = typer.Option(False, "--tag-all", help="모든 고객 태깅"),
    dry_run: bool = typer.Option(False, "--dry-run", help="실제 적용 없이 시뮬레이션"),
    show_distribution: bool = typer.Option(False, "--show-distribution", help="위험 분포 표시"),
    high_risk: bool = typer.Option(False, "--high-risk", help="고위험 고객 목록"),
    show_trends: bool = typer.Option(False, "--show-trends", help="위험 트렌드 표시"),
    days: int = typer.Option(30, "--days", help="트렌드 분석 기간 (일)"),
    limit: int = typer.Option(50, "--limit", help="결과 제한 수")
):
    """고객 이탈위험 태깅 시스템."""
    console.print("[bold blue]고객 이탈위험 태깅 시스템[/bold blue]")
    
    try:
        from analytics.services.risk_tagging import CustomerRiskTaggingService
        service = CustomerRiskTaggingService()
        
        if customer_id:
            # 특정 고객 위험 분석
            risk_analysis = service.analyze_customer_risk(customer_id)
            
            if 'error' in risk_analysis:
                console.print(f"[red]Error: {risk_analysis['error']}[/red]")
                return
            
            console.print(f"\n[bold green]고객 {customer_id} 위험 분석:[/bold green]")
            console.print(f"• 위험 점수: {risk_analysis['risk_score']:.1f}/100")
            console.print(f"• 위험 수준: {risk_analysis['risk_level']}")
            
            # 위험 요인
            console.print(f"\n[bold]위험 요인:[/bold]")
            risk_factors = risk_analysis['risk_factors']
            
            visit_pattern = risk_factors.get('visit_pattern', {})
            console.print(f"• 마지막 방문: {visit_pattern.get('days_since_last_visit', 0)}일 전")
            console.print(f"• 총 방문 횟수: {visit_pattern.get('total_visits', 0)}회")
            if visit_pattern.get('is_overdue'):
                console.print("  [red]⚠️ 방문 지연[/red]")
            
            segment_risk = risk_factors.get('segment_risk', {})
            console.print(f"• 고객 세그먼트: {segment_risk.get('segment', 'unknown')}")
            if segment_risk.get('is_new_customer_at_risk'):
                console.print("  [yellow]⚠️ 신규 고객 위험[/yellow]")
            if segment_risk.get('is_loyal_customer_at_risk'):
                console.print("  [red]⚠️ 충성 고객 위험[/red]")
            
            # 추천 태그
            console.print(f"\n[bold]추천 태그:[/bold]")
            for tag in risk_analysis['recommended_tags']:
                console.print(f"• {tag}")
            
            # 추천 액션
            console.print(f"\n[bold]추천 액션:[/bold]")
            for action in risk_analysis['recommended_actions']:
                console.print(f"• {action}")
            
            # 고객 태그 조회
            customer_tags = service.get_customer_risk_tags(customer_id)
            if customer_tags:
                console.print(f"\n[bold]현재 적용된 위험 태그:[/bold]")
                for tag in customer_tags:
                    console.print(f"• {tag['tag_type']}: {tag['tag_value']} "
                                f"(우선순위: {tag['priority']})")
        
        elif tag_all:
            # 모든 고객 태깅
            console.print(f"\n[bold yellow]모든 고객 태깅 {'(시뮬레이션)' if dry_run else '(실제 적용)'}...[/bold yellow]")
            
            results = service.tag_all_customers(dry_run=dry_run)
            
            if 'error' in results:
                console.print(f"[red]Error: {results['error']}[/red]")
                return
            
            console.print(f"\n[bold green]태깅 결과:[/bold green]")
            console.print(f"• 총 고객 수: {results['total_customers']}")
            console.print(f"• 태깅 완료: {results['tagged_customers']}")
            console.print(f"• 고위험 고객: {results['high_risk_customers']}")
            console.print(f"• 중위험 고객: {results['medium_risk_customers']}")
            console.print(f"• 저위험 고객: {results['low_risk_customers']}")
            
            if not dry_run:
                console.print(f"• 생성된 태그: {results['tags_created']}")
            
            if results['errors']:
                console.print(f"\n[bold red]오류 발생:[/bold red]")
                for error in results['errors'][:5]:  # 최대 5개만 표시
                    console.print(f"• {error}")
                if len(results['errors']) > 5:
                    console.print(f"• ... 외 {len(results['errors']) - 5}개")
        
        elif show_distribution:
            # 위험 분포 표시
            distribution = service.get_risk_distribution()
            
            if not distribution:
                console.print("[yellow]위험 분포 데이터가 없습니다.[/yellow]")
                return
            
            console.print(f"\n[bold green]위험 수준별 고객 분포:[/bold green]")
            console.print(f"총 고객 수: {distribution['total_customers']}")
            
            for risk_level, data in distribution['distribution'].items():
                console.print(f"\n• {risk_level.upper()} 위험:")
                console.print(f"  고객 수: {data['customer_count']}명 ({data['percentage']:.1f}%)")
                console.print(f"  평균 위험 점수: {data['avg_risk_score']:.1f}")
                console.print(f"  평균 방문 횟수: {data['avg_visits']:.1f}회")
                console.print(f"  평균 총 금액: {data['avg_amount']:,.0f}원")
                console.print(f"  평균 미방문 일수: {data['avg_days_since_visit']:.0f}일")
        
        elif high_risk:
            # 고위험 고객 목록
            high_risk_customers = service.get_high_risk_customers(limit)
            
            if not high_risk_customers:
                console.print("[yellow]고위험 고객이 없습니다.[/yellow]")
                return
            
            console.print(f"\n[bold red]고위험 고객 목록 (상위 {len(high_risk_customers)}명):[/bold red]")
            
            for customer in high_risk_customers:
                console.print(f"\n• 고객 {customer['customer_id']}: {customer['name']}")
                console.print(f"  연락처: {customer['phone']}")
                console.print(f"  위험 점수: {customer['churn_risk_score']:.1f}/100 ({customer['churn_risk_level']})")
                console.print(f"  긴급도: {customer['urgency_score']:.1f}")
                console.print(f"  미방문: {customer['days_since_last_visit']}일")
                console.print(f"  총 방문: {customer['total_visits']}회")
                console.print(f"  총 금액: {customer['total_amount']:,.0f}원")
                console.print(f"  세그먼트: {customer['segment']}")
        
        elif show_trends:
            # 위험 트렌드 표시
            trends = service.get_risk_trends(days)
            
            if not trends or not trends.get('trends'):
                console.print(f"[yellow]최근 {days}일간 위험 트렌드 데이터가 없습니다.[/yellow]")
                return
            
            console.print(f"\n[bold green]최근 {days}일간 위험 트렌드:[/bold green]")
            
            for date, risk_data in sorted(trends['trends'].items(), reverse=True)[:10]:
                console.print(f"\n• {date}:")
                for risk_level, data in risk_data.items():
                    console.print(f"  {risk_level}: {data['customer_count']}명 "
                                f"(평균 점수: {data['avg_risk_score']:.1f})")
        
        else:
            # 전체 통계 및 도움말
            console.print("\n[bold green]위험 태깅 시스템 옵션:[/bold green]")
            console.print("• --customer-id <ID>: 특정 고객 위험 분석")
            console.print("• --tag-all: 모든 고객 태깅")
            console.print("• --tag-all --dry-run: 태깅 시뮬레이션")
            console.print("• --show-distribution: 위험 분포 표시")
            console.print("• --high-risk: 고위험 고객 목록")
            console.print("• --show-trends: 위험 트렌드 분석")
            console.print("• --days <N>: 트렌드 분석 기간 설정")
    
    except Exception as e:
        console.print(f"[red]Error: {e}[/red]")
        raise typer.Exit(1)


if __name__ == "__main__":
    app() 