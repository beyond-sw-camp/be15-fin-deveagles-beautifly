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
            self._analytics_conn = duckdb.connect(
                settings.analytics_db_path,
                config={
                    "threads": settings.analytics_db_threads,
                    "memory_limit": "2GB",
                    "max_memory": "4GB",
                }
            )
        return self._analytics_conn

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