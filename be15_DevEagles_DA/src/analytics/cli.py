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
    dry_run: bool = typer.Option(default=False, help="Dry run mode"),
) -> None:
    """Run ETL pipeline."""
    from analytics.etl.pipeline import ETLPipeline
    
    console.print("🔄 Starting ETL pipeline...")
    
    try:
        pipeline = ETLPipeline()
        
        if dry_run:
            console.print("🧪 Running in dry-run mode")
            # Add dry-run logic
            
        result = asyncio.run(pipeline.run(incremental=not full))
        
        table = Table(title="ETL Results")
        table.add_column("Metric", style="cyan")
        table.add_column("Value", style="green")
        
        table.add_row("Duration", f"{result.get('duration', 0):.2f}s")
        table.add_row("Records Processed", str(result.get('records_processed', 0)))
        table.add_row("Status", result.get('status', 'Unknown'))
        
        console.print(table)
        
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


if __name__ == "__main__":
    app() 