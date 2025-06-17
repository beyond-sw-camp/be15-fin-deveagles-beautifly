.PHONY: help install install-dev test test-unit test-integration lint format type-check clean build run run-dev docker-build docker-run docker-dev stop logs

# Variables
PYTHON := python
PIP := pip
DOCKER_COMPOSE := docker-compose
IMAGE_NAME := deveagles-analytics

# Default target
help: ## Show this help message
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Installation
install: ## Install production dependencies
	$(PIP) install --upgrade pip
	$(PIP) install -e .

install-dev: ## Install development dependencies
	$(PIP) install --upgrade pip
	$(PIP) install -e ".[dev,jupyter,monitoring]"
	pre-commit install

# Testing
test: ## Run all tests
	pytest

test-unit: ## Run unit tests only
	pytest -m "unit" -v

test-integration: ## Run integration tests only
	pytest -m "integration" -v

test-coverage: ## Run tests with coverage report
	pytest --cov=analytics --cov-report=html --cov-report=term

# Code Quality
lint: ## Run linter
	flake8 src tests
	isort --check-only src tests

format: ## Format code
	black src tests
	isort src tests

type-check: ## Run type checker
	mypy src

quality: lint type-check ## Run all code quality checks

fix: format ## Fix code formatting issues

# Development
run: ## Run the server locally
	analytics-server

run-dev: ## Run the server in development mode
	analytics-server --reload --log-level debug

run-etl: ## Run ETL pipeline
	analytics-etl

run-train: ## Train ML models
	analytics-train

run-tag: ## Run customer tagging
	analytics-tag

status: ## Show service status
	analytics status

# Docker
docker-build: ## Build Docker image
	docker build -t $(IMAGE_NAME) .

docker-build-dev: ## Build development Docker image
	docker build -t $(IMAGE_NAME):dev --target development .

docker-run: ## Run Docker container
	docker run -p 8000:8000 $(IMAGE_NAME)

docker-dev: ## Start development environment with Docker Compose
	$(DOCKER_COMPOSE) up --build

docker-prod: ## Start production environment with Docker Compose
	$(DOCKER_COMPOSE) -f docker-compose.yml -f docker-compose.prod.yml up --build

stop: ## Stop Docker containers
	$(DOCKER_COMPOSE) down

logs: ## Show Docker logs
	$(DOCKER_COMPOSE) logs -f

# Database
db-setup: ## Setup development database
	$(DOCKER_COMPOSE) up -d crm-db
	sleep 10
	mariadb -h 127.0.0.1 -u readonly_user -ppassword crm < scripts/sample_data.sql

db-reset: ## Reset development database
	$(DOCKER_COMPOSE) down -v
	$(DOCKER_COMPOSE) up -d crm-db

# Data Management
etl-full: ## Run full ETL pipeline
	analytics-etl --full

etl-incremental: ## Run incremental ETL pipeline
	analytics-etl

train-all: ## Train all ML models
	analytics-train --model-type all

train-churn: ## Train churn prediction model
	analytics-train --model-type churn

tag-customers: ## Tag all customers
	analytics-tag

# Monitoring
jupyter: ## Start Jupyter Lab
	$(DOCKER_COMPOSE) up jupyter

prometheus: ## Start Prometheus
	$(DOCKER_COMPOSE) up prometheus

grafana: ## Start Grafana
	$(DOCKER_COMPOSE) up grafana

monitoring: ## Start all monitoring services
	$(DOCKER_COMPOSE) up prometheus grafana

# Cleanup
clean: ## Clean temporary files
	find . -type f -name "*.pyc" -delete
	find . -type d -name "__pycache__" -delete
	find . -type d -name "*.egg-info" -exec rm -rf {} +
	rm -rf build/
	rm -rf dist/
	rm -rf .coverage
	rm -rf htmlcov/
	rm -rf .pytest_cache/
	rm -rf .mypy_cache/

clean-docker: ## Clean Docker resources
	docker system prune -f
	docker volume prune -f

clean-all: clean clean-docker ## Clean everything

# Build
build: ## Build package
	$(PYTHON) -m build

build-docker: docker-build ## Build Docker image

# Deploy (example commands)
deploy-staging: ## Deploy to staging environment
	@echo "Deploying to staging..."
	# Add staging deployment commands here

deploy-prod: ## Deploy to production environment
	@echo "Deploying to production..."
	# Add production deployment commands here

# Development workflow
dev-setup: install-dev db-setup ## Complete development setup
	@echo "Development environment ready!"
	@echo "Run 'make run-dev' to start the server"
	@echo "Run 'make jupyter' to start Jupyter Lab"

dev-reset: clean db-reset dev-setup ## Reset development environment

# CI/CD helpers
ci-test: install-dev test quality ## Run CI tests
	@echo "All CI checks passed!"

ci-build: build build-docker ## Build for CI/CD
	@echo "Build completed!" 