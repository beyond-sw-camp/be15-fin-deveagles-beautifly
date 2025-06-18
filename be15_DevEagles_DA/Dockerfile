# Multi-stage build for production
FROM python:3.11-slim as builder

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Create and activate virtual environment
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Copy and install Python dependencies
COPY pyproject.toml ./
RUN pip install --upgrade pip && \
    pip install build && \
    pip install .

# Production stage
FROM python:3.11-slim as production

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PATH="/opt/venv/bin:$PATH"

# Create non-root user
RUN groupadd -r analytics && \
    useradd -r -g analytics -d /app -s /bin/bash analytics

# Copy virtual environment from builder
COPY --from=builder /opt/venv /opt/venv

# Set working directory
WORKDIR /app

# Copy application code
COPY --chown=analytics:analytics . .

# Create necessary directories
RUN mkdir -p data models logs && \
    chown -R analytics:analytics data models logs

# Switch to non-root user
USER analytics

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD python -c "import requests; requests.get('http://localhost:8000/health')" || exit 1

# Default command
CMD ["analytics-server"]

# Development stage
FROM python:3.11-slim as development

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    ANALYTICS_DEBUG=true \
    ANALYTICS_RELOAD=true

# Install system dependencies including development tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy and install Python dependencies (including dev dependencies)
COPY pyproject.toml ./
RUN pip install --upgrade pip && \
    pip install -e ".[dev,jupyter,monitoring]"

# Copy application code
COPY . .

# Create directories
RUN mkdir -p data models logs

# Expose port
EXPOSE 8000

# Development command with auto-reload
CMD ["analytics-server", "--reload"] 