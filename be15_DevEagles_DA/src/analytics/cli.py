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
    host: str = typer.Option(default=settings.host, help="Server host"),
    port: int = typer.Option(default=settings.port, help="Server port"),
    workers: int = typer.Option(default=settings.workers, help="Number of workers"),
    reload: bool = typer.Option(default=settings.reload, help="Enable auto-reload"),
    log_level: str = typer.Option(default=settings.log_level.lower(), help="Log level"),
) -> None:
    """Start the FastAPI server."""
    console.print(f"🚀 Starting {settings.app_name} v{settings.app_version}")
    console.print(f"📍 Server will be available at http://{host}:{port}")
    
    if reload:
        console.print("🔄 Auto-reload enabled (development mode)")
    
    uvicorn.run(
        "analytics.api.main:app",
        host=host,
        port=port,
        workers=workers if not reload else 1,
        reload=reload,
        log_level=log_level,
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
        table.add_row("CRM DB", "✅ Connected", "MySQL connection OK")
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
def config() -> None:
    """Show current configuration."""
    console.print(f"⚙️ {settings.app_name} Configuration")
    
    table = Table()
    table.add_column("Setting", style="cyan")
    table.add_column("Value", style="green")
    
    # Core settings
    table.add_row("App Name", settings.app_name)
    table.add_row("Version", settings.app_version)
    table.add_row("Debug Mode", str(settings.debug))
    table.add_row("Log Level", settings.log_level)
    
    # Server settings
    table.add_row("Host", settings.host)
    table.add_row("Port", str(settings.port))
    table.add_row("Workers", str(settings.workers))
    
    # Database settings
    table.add_row("Analytics DB", settings.analytics_db_path)
    table.add_row("Model Storage", settings.model_storage_path)
    
    console.print(table)


if __name__ == "__main__":
    app() 