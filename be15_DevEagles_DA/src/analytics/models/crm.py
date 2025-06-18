"""CRM database models using SQLAlchemy."""

from datetime import datetime
from typing import List, Optional

from sqlalchemy import (
    Column, Integer, String, DateTime, Decimal, Text, Boolean,
    ForeignKey, Index
)
from sqlalchemy.orm import relationship

from .base import Base, TimestampMixin


class Customer(Base, TimestampMixin):
    """고객 테이블."""
    __tablename__ = "customers"
    
    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    phone = Column(String(20), unique=True, nullable=False)
    email = Column(String(100), unique=True, nullable=True)
    birth_date = Column(DateTime, nullable=True)
    gender = Column(String(10), nullable=True)
    address = Column(Text, nullable=True)
    memo = Column(Text, nullable=True)
    is_active = Column(Boolean, default=True, nullable=False)
    
    # 관계 설정
    visits = relationship("Visit", back_populates="customer", lazy="dynamic")
    
    # 인덱스
    __table_args__ = (
        Index("idx_customer_phone", "phone"),
        Index("idx_customer_email", "email"),
        Index("idx_customer_active", "is_active"),
    )


class Employee(Base, TimestampMixin):
    """직원 테이블."""
    __tablename__ = "employees"
    
    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    phone = Column(String(20), nullable=True)
    email = Column(String(255), nullable=True)
    position = Column(String(100), nullable=True)  # 직급
    department = Column(String(100), nullable=True)  # 부서
    hire_date = Column(DateTime, nullable=True)
    is_active = Column(Boolean, default=True, nullable=False)
    specialties = Column(Text, nullable=True)  # 전문 분야 (JSON 형태)
    
    # 관계 설정
    visits = relationship("Visit", back_populates="employee", lazy="dynamic")
    visit_services = relationship("VisitService", back_populates="employee", lazy="dynamic")
    
    # 인덱스
    __table_args__ = (
        Index("idx_employee_name", "name"),
        Index("idx_employee_active", "is_active"),
    )


class Service(Base, TimestampMixin):
    """서비스 테이블."""
    __tablename__ = "services"
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    category = Column(String(100), nullable=True)
    price = Column(Decimal(10, 2), nullable=False)
    duration_minutes = Column(Integer, nullable=True)
    description = Column(Text, nullable=True)
    is_active = Column(Boolean, default=True, nullable=False)
    
    # 관계 설정
    visit_services = relationship("VisitService", back_populates="service", lazy="dynamic")
    
    # 인덱스
    __table_args__ = (
        Index("idx_service_category", "category"),
        Index("idx_service_active", "is_active"),
    )


class Visit(Base, TimestampMixin):
    """방문 테이블."""
    __tablename__ = "visits"
    
    id = Column(Integer, primary_key=True)
    customer_id = Column(Integer, ForeignKey("customers.id"), nullable=False)
    employee_id = Column(Integer, ForeignKey("employees.id"), nullable=True)
    visit_date = Column(DateTime, nullable=False)
    total_amount = Column(Decimal(10, 2), nullable=False, default=0)
    discount_amount = Column(Decimal(10, 2), nullable=False, default=0)
    final_amount = Column(Decimal(10, 2), nullable=False, default=0)
    payment_method = Column(String(50), nullable=True)
    memo = Column(Text, nullable=True)
    status = Column(String(20), default="completed", nullable=False)
    
    # 관계 설정
    customer = relationship("Customer", back_populates="visits")
    employee = relationship("Employee", back_populates="visits")
    visit_services = relationship("VisitService", back_populates="visit", lazy="dynamic")
    
    # 인덱스
    __table_args__ = (
        Index("idx_visit_customer", "customer_id"),
        Index("idx_visit_employee", "employee_id"),
        Index("idx_visit_date", "visit_date"),
        Index("idx_visit_status", "status"),
        Index("idx_visit_customer_date", "customer_id", "visit_date"),
    )


class VisitService(Base, TimestampMixin):
    """방문별 서비스 테이블."""
    __tablename__ = "visit_services"
    
    id = Column(Integer, primary_key=True)
    visit_id = Column(Integer, ForeignKey("visits.id"), nullable=False)
    service_id = Column(Integer, ForeignKey("services.id"), nullable=False)
    employee_id = Column(Integer, ForeignKey("employees.id"), nullable=True)
    quantity = Column(Integer, default=1, nullable=False)
    unit_price = Column(Decimal(10, 2), nullable=False)
    total_price = Column(Decimal(10, 2), nullable=False)
    discount_amount = Column(Decimal(10, 2), nullable=False, default=0)
    final_price = Column(Decimal(10, 2), nullable=False)
    memo = Column(Text, nullable=True)
    
    # 관계 설정
    visit = relationship("Visit", back_populates="visit_services")
    service = relationship("Service", back_populates="visit_services")
    employee = relationship("Employee", back_populates="visit_services")
    
    # 인덱스
    __table_args__ = (
        Index("idx_visit_service_visit", "visit_id"),
        Index("idx_visit_service_service", "service_id"),
        Index("idx_visit_service_employee", "employee_id"),
    )


class CustomerTag(Base, TimestampMixin):
    """고객 태그 테이블 (리스크 평가 결과 저장)."""
    __tablename__ = "customer_tags"
    
    id = Column(Integer, primary_key=True)
    customer_id = Column(Integer, ForeignKey("customers.id"), nullable=False)
    tag_type = Column(String(50), nullable=False)  # RiskTag enum values
    tag_value = Column(String(100), nullable=True)  # 추가 정보
    priority = Column(Integer, default=0, nullable=False)  # 우선순위
    expires_at = Column(DateTime, nullable=True)  # 만료일
    is_active = Column(Boolean, default=True, nullable=False)
    processed_at = Column(DateTime, nullable=True)  # 처리 완료 시간
    
    # 관계 설정
    customer = relationship("Customer", lazy="select")
    
    # 인덱스
    __table_args__ = (
        Index("idx_customer_tag_customer", "customer_id"),
        Index("idx_customer_tag_type", "tag_type"),
        Index("idx_customer_tag_active", "is_active"),
        Index("idx_customer_tag_priority", "priority"),
        Index("idx_customer_tag_expires", "expires_at"),
    ) 