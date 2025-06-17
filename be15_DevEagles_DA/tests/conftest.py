"""Pytest configuration and shared fixtures."""

import pytest
from unittest.mock import Mock, patch
import tempfile
import os
from pathlib import Path
from typing import Generator

from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from analytics.core.config import Settings


@pytest.fixture(scope="session")
def test_settings() -> Settings:
    """Test settings with temporary database paths."""
    return Settings(
        debug=True,
        log_level="DEBUG",
        analytics_db_path=":memory:",  # In-memory DuckDB for tests
        crm_database_url="sqlite:///:memory:",  # In-memory SQLite for tests
        model_storage_path="tests/fixtures/models",
        etl_timestamp_file="tests/fixtures/last_etl.txt",
        scheduler_jobstore_url="sqlite:///:memory:",
    )


@pytest.fixture(scope="function")
def temp_dir() -> Generator[Path, None, None]:
    """Create a temporary directory for tests."""
    with tempfile.TemporaryDirectory() as temp_dir:
        yield Path(temp_dir)


@pytest.fixture(scope="function")
def mock_crm_db():
    """Mock CRM database connection."""
    engine = create_engine("sqlite:///:memory:")
    
    # Create test tables
    with engine.connect() as conn:
        conn.execute("""
            CREATE TABLE customers (
                id INTEGER PRIMARY KEY,
                name TEXT,
                phone TEXT,
                birth_date DATE,
                gender TEXT,
                created_at TIMESTAMP,
                last_visit_at TIMESTAMP,
                updated_at TIMESTAMP,
                risk_tags TEXT,
                risk_score REAL,
                last_analyzed TIMESTAMP
            )
        """)
        
        conn.execute("""
            CREATE TABLE reservations (
                id INTEGER PRIMARY KEY,
                customer_id INTEGER,
                service_id INTEGER,
                reserved_at TIMESTAMP,
                completed_at TIMESTAMP,
                amount DECIMAL(10,2),
                employee_id INTEGER,
                status TEXT,
                updated_at TIMESTAMP
            )
        """)
        
        conn.execute("""
            CREATE TABLE services (
                id INTEGER PRIMARY KEY,
                name TEXT,
                category TEXT,
                price DECIMAL(10,2),
                duration_minutes INTEGER
            )
        """)
        
        conn.commit()
    
    return engine


@pytest.fixture(scope="function")
def mock_analytics_db():
    """Mock Analytics DuckDB connection."""
    import duckdb
    
    conn = duckdb.connect(":memory:")
    
    # Create test views
    conn.execute("""
        CREATE TABLE customers (
            id INTEGER,
            name VARCHAR,
            birth_date DATE,
            gender VARCHAR,
            created_at TIMESTAMP
        )
    """)
    
    conn.execute("""
        CREATE TABLE reservations (
            id INTEGER,
            customer_id INTEGER,
            service_id INTEGER,
            completed_at TIMESTAMP,
            amount DECIMAL(10,2),
            status VARCHAR
        )
    """)
    
    return conn


@pytest.fixture(scope="function")
def sample_customers_data():
    """Sample customer data for testing."""
    return [
        {
            "id": 1,
            "name": "테스트 고객1",
            "phone": "010-1234-5678",
            "birth_date": "1990-01-01",
            "gender": "F",
            "created_at": "2024-01-01 10:00:00",
        },
        {
            "id": 2,
            "name": "테스트 고객2",
            "phone": "010-2345-6789",
            "birth_date": "1985-05-15",
            "gender": "F",
            "created_at": "2024-02-01 14:30:00",
        },
    ]


@pytest.fixture(scope="function")
def sample_reservations_data():
    """Sample reservation data for testing."""
    return [
        {
            "id": 1,
            "customer_id": 1,
            "service_id": 1,
            "completed_at": "2024-11-01 10:00:00",
            "amount": 50000,
            "status": "completed",
        },
        {
            "id": 2,
            "customer_id": 1,
            "service_id": 2,
            "completed_at": "2024-11-15 14:00:00",
            "amount": 80000,
            "status": "completed",
        },
        {
            "id": 3,
            "customer_id": 2,
            "service_id": 1,
            "completed_at": "2024-10-01 11:00:00",
            "amount": 50000,
            "status": "completed",
        },
    ]


@pytest.fixture(scope="function")
def client(test_settings, mock_crm_db, mock_analytics_db) -> TestClient:
    """FastAPI test client with mocked dependencies."""
    with patch("analytics.core.config.get_settings", return_value=test_settings):
        with patch("analytics.core.database.get_crm_db", return_value=mock_crm_db):
            with patch("analytics.core.database.get_analytics_db", return_value=mock_analytics_db):
                from analytics.api.main import app
                return TestClient(app)


@pytest.fixture(scope="function")
def mock_model_manager():
    """Mock ML model manager."""
    manager = Mock()
    manager.load_model.return_value = Mock()
    manager.save_model.return_value = "test_version_123"
    manager.list_models.return_value = ["churn_model", "segmentation_model"]
    return manager


# Markers for different test types
pytest.mark.unit = pytest.mark.mark(name="unit")
pytest.mark.integration = pytest.mark.mark(name="integration")
pytest.mark.slow = pytest.mark.mark(name="slow") 