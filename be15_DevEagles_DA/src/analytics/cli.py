"""Command Line Interface for analytics service."""

import asyncio
from typing import Optional

import typer
import uvicorn
from rich.console import Console
from rich.table import Table

from analytics.core.config import settings
from analytics.core.logging import get_logger

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


if __name__ == "__main__":
    app() 