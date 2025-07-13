"""Database connection management."""

from contextlib import contextmanager
from typing import Generator

import duckdb
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from sqlalchemy.orm import sessionmaker

from analytics.core.config import settings
from analytics.core.logging import get_logger

logger = get_logger("database")


class DatabaseManager:
    """Database connection manager for CRM and Analytics databases."""

    def __init__(self) -> None:
        self._crm_engine: Engine | None = None
        self._analytics_conn: duckdb.DuckDBPyConnection | None = None

    def get_crm_engine(self) -> Engine:
        """Get SQLAlchemy engine for CRM database (MariaDB)."""
        if self._crm_engine is None:
            logger.info("Creating CRM DA database engine")
            self._crm_engine = create_engine(
                settings.crm_database_url,
                pool_size=settings.crm_pool_size,
                max_overflow=settings.crm_max_overflow,
                pool_pre_ping=True,
                echo=settings.debug,
            )
        return self._crm_engine

    def get_analytics_connection(self) -> duckdb.DuckDBPyConnection:
        """Get DuckDB connection for analytics database."""
        if self._analytics_conn is None:
            logger.info(f"Creating Analytics database connection: {settings.analytics_db_path}")
            try:
                # Create directory if it doesn't exist
                import os
                db_path = settings.analytics_db_path
                db_dir = os.path.dirname(db_path)
                if db_dir and not os.path.exists(db_dir):
                    os.makedirs(db_dir, exist_ok=True)
                
                # Create DuckDB connection with UTF-8 encoding
                self._analytics_conn = duckdb.connect(
                    db_path,
                    config={
                        "threads": settings.analytics_db_threads,
                        "memory_limit": "2GB",
                        "max_memory": "4GB",
                    }
                )
                
                # Initialize basic tables if they don't exist
                self._initialize_analytics_tables()
                
            except Exception as e:
                logger.error(f"Failed to create analytics database connection: {e}")
                # Create in-memory database as fallback
                logger.info("Using in-memory DuckDB as fallback")
                self._analytics_conn = duckdb.connect(
                    ":memory:",
                    config={
                        "threads": settings.analytics_db_threads,
                        "memory_limit": "2GB",
                        "max_memory": "4GB",
                    }
                )
                self._initialize_analytics_tables()
                
        return self._analytics_conn

    def _initialize_analytics_tables(self) -> None:
        """Initialize basic analytics tables if they don't exist."""
        try:
            # Create customer_analytics table with basic structure
            create_table_sql = """
            CREATE TABLE IF NOT EXISTS customer_analytics (
                customer_id INTEGER PRIMARY KEY,
                name VARCHAR,
                phone VARCHAR,
                email VARCHAR,
                birth_date DATE,
                gender VARCHAR,
                first_visit_date DATE,
                last_visit_date DATE,
                total_visits INTEGER DEFAULT 0,
                total_amount DECIMAL(15,2) DEFAULT 0.0,
                avg_visit_amount DECIMAL(15,2) DEFAULT 0.0,
                lifecycle_days INTEGER DEFAULT 0,
                days_since_last_visit INTEGER DEFAULT 0,
                visit_frequency DECIMAL(5,2) DEFAULT 0.0,
                preferred_services VARCHAR,
                preferred_employees VARCHAR,
                visits_3m INTEGER DEFAULT 0,
                amount_3m DECIMAL(15,2) DEFAULT 0.0,
                segment VARCHAR DEFAULT 'new',
                segment_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                churn_risk_score DECIMAL(5,2) DEFAULT 0.0,
                churn_risk_level VARCHAR DEFAULT 'low',
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """
            self._analytics_conn.execute(create_table_sql)
            logger.info("Analytics tables initialized successfully")
            
        except Exception as e:
            logger.error(f"Failed to initialize analytics tables: {e}")

    @contextmanager
    def crm_session(self) -> Generator:
        """Context manager for CRM database sessions."""
        engine = self.get_crm_engine()
        Session = sessionmaker(bind=engine)
        session = Session()
        try:
            yield session
            session.commit()
        except Exception:
            session.rollback()
            raise
        finally:
            session.close()

    @contextmanager
    def analytics_transaction(self) -> Generator:
        """Context manager for Analytics database transactions."""
        conn = self.get_analytics_connection()
        try:
            conn.begin()
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise

    def close_connections(self) -> None:
        """Close all database connections."""
        if self._crm_engine:
            self._crm_engine.dispose()
            self._crm_engine = None

        if self._analytics_conn:
            self._analytics_conn.close()
            self._analytics_conn = None

        logger.info("All database connections closed")


# Global database manager instance
db_manager = DatabaseManager()


def get_crm_db() -> Engine:
    """Dependency function for CRM database engine."""
    return db_manager.get_crm_engine()


def get_analytics_db() -> duckdb.DuckDBPyConnection:
    """Dependency function for Analytics database connection."""
    return db_manager.get_analytics_connection() 