"""
Beautifly CRM Database Models

실제 beautifly 데이터베이스 스키마에 맞는 SQLAlchemy 모델들
"""

from datetime import datetime
from sqlalchemy import (
    Column, Integer, BigInteger, String, Text, Date, DateTime, 
    Enum, Boolean, ForeignKey, Index, Numeric, TIMESTAMP
)
from sqlalchemy.orm import relationship, declarative_base
from sqlalchemy.dialects.mysql import TINYINT

Base = declarative_base()


class Customer(Base):
    """고객 테이블 - beautifly.customer"""
    __tablename__ = "customer"
    
    customer_id = Column(BigInteger, primary_key=True)
    customer_grade_id = Column(BigInteger, nullable=False)
    shop_id = Column(BigInteger, nullable=False)
    staff_id = Column(BigInteger, nullable=False)
    customer_name = Column(String(100), nullable=False)
    phone_number = Column(String(11), nullable=False)
    memo = Column(String(255), nullable=True)
    visit_count = Column(Integer, nullable=False)
    total_revenue = Column(Integer, nullable=False)
    recent_visit_date = Column(Date, nullable=False)
    birthdate = Column(Date, nullable=False)
    noshow_count = Column(Integer, nullable=False)
    gender = Column(Enum('M', 'F', name='gender_enum'), nullable=True)
    marketing_consent = Column(TINYINT, nullable=False, default=0)
    marketing_consented_at = Column(TIMESTAMP, nullable=True)
    notification_consent = Column(TINYINT, nullable=False, default=0)
    last_message_sent_at = Column(TIMESTAMP, nullable=True)
    channel_id = Column(BigInteger, nullable=False)
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    modified_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    deleted_at = Column(TIMESTAMP, nullable=True)
    
    # 관계 설정
    reservations = relationship("Reservation", back_populates="customer")
    sales = relationship("Sales", back_populates="customer")
    
    def __repr__(self):
        return f"<Customer(id={self.customer_id}, name='{self.customer_name}')>"


class Staff(Base):
    """직원 테이블 - beautifly.staff"""
    __tablename__ = "staff"
    
    staff_id = Column(BigInteger, primary_key=True)
    shop_id = Column(BigInteger, nullable=False)
    login_id = Column(String(255), nullable=False)
    grade = Column(String(10), nullable=False)
    email = Column(String(255), nullable=False)
    password = Column(String(255), nullable=False)
    staff_status = Column(
        Enum('OWNER', 'STAFF', name='staff_status_enum'), 
        nullable=False, 
        default='STAFF'
    )
    staff_name = Column(String(100), nullable=False)
    phone_number = Column(String(11), nullable=False)
    profile_url = Column(String(255), nullable=True)
    joined_date = Column(Date, nullable=True)
    left_date = Column(Date, nullable=True)
    color_code = Column(String(20), nullable=False, default='#364f6b')
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    modified_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    staff_description = Column(String(255), nullable=True)
    
    # 관계 설정
    reservations = relationship("Reservation", back_populates="staff")
    sales = relationship("Sales", back_populates="staff")
    
    def __repr__(self):
        return f"<Staff(id={self.staff_id}, name='{self.staff_name}')>"


class Shop(Base):
    """매장 테이블 - beautifly.shop"""
    __tablename__ = "shop"
    
    shop_id = Column(BigInteger, primary_key=True)
    # 기본 필드들만 정의 (전체 스키마는 필요시 추가)
    
    def __repr__(self):
        return f"<Shop(id={self.shop_id})>"


class PrimaryItem(Base):
    """주요 서비스 카테고리 테이블 - beautifly.primary_item"""
    __tablename__ = "primary_item"
    
    primary_item_id = Column(BigInteger, primary_key=True)
    shop_id = Column(BigInteger, nullable=False)
    primary_item_name = Column(String(100), nullable=False)
    category = Column(
        Enum('SERVICE', 'PRODUCT', name='category_enum'), 
        nullable=False
    )
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    deleted_at = Column(TIMESTAMP, nullable=True)
    
    # 관계 설정
    secondary_items = relationship("SecondaryItem", back_populates="primary_item")
    
    def __repr__(self):
        return f"<PrimaryItem(id={self.primary_item_id}, name='{self.primary_item_name}')>"


class SecondaryItem(Base):
    """세부 서비스 테이블 - beautifly.secondary_item"""
    __tablename__ = "secondary_item"
    
    secondary_item_id = Column(BigInteger, primary_key=True)
    primary_item_id = Column(BigInteger, ForeignKey("primary_item.primary_item_id"), nullable=False)
    secondary_item_name = Column(String(100), nullable=True)
    secondary_item_price = Column(Integer, nullable=True)
    is_active = Column(TINYINT, nullable=False, default=1)
    time_taken = Column(Integer, nullable=True)
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    modified_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    deleted_at = Column(TIMESTAMP, nullable=True)
    
    # 관계 설정
    primary_item = relationship("PrimaryItem", back_populates="secondary_items")
    reservation_details = relationship("ReservationDetail", back_populates="secondary_item")
    
    def __repr__(self):
        return f"<SecondaryItem(id={self.secondary_item_id}, name='{self.secondary_item_name}')>"


class Reservation(Base):
    """예약 테이블 - beautifly.reservation"""
    __tablename__ = "reservation"
    
    reservation_id = Column(BigInteger, primary_key=True)
    staff_id = Column(BigInteger, ForeignKey("staff.staff_id"), nullable=False)
    shop_id = Column(BigInteger, nullable=False)
    customer_id = Column(BigInteger, ForeignKey("customer.customer_id"), nullable=True)
    reservation_status_name = Column(
        Enum('BOOKED', 'CONFIRMED', 'COMPLETED', 'PAID', 'CANCELLED', 'NOSHOW', name='reservation_status_enum'),
        nullable=False
    )
    staff_memo = Column(String(255), nullable=True)
    reservation_memo = Column(String(255), nullable=True)
    reservation_start_at = Column(TIMESTAMP, nullable=False)
    reservation_end_at = Column(TIMESTAMP, nullable=False)
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    modified_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    deleted_at = Column(TIMESTAMP, nullable=True)
    
    # 관계 설정
    customer = relationship("Customer", back_populates="reservations")
    staff = relationship("Staff", back_populates="reservations")
    reservation_details = relationship("ReservationDetail", back_populates="reservation")
    sales = relationship("Sales", back_populates="reservation")
    
    def __repr__(self):
        return f"<Reservation(id={self.reservation_id}, customer_id={self.customer_id})>"


class ReservationDetail(Base):
    """예약 상세 테이블 - beautifly.reservation_detail"""
    __tablename__ = "reservation_detail"
    
    reservation_detail_id = Column(BigInteger, primary_key=True)
    reservation_id = Column(BigInteger, ForeignKey("reservation.reservation_id"), nullable=False)
    secondary_item_id = Column(BigInteger, ForeignKey("secondary_item.secondary_item_id"), nullable=False)
    
    # 관계 설정
    reservation = relationship("Reservation", back_populates="reservation_details")
    secondary_item = relationship("SecondaryItem", back_populates="reservation_details")
    
    def __repr__(self):
        return f"<ReservationDetail(id={self.reservation_detail_id})>"


class Sales(Base):
    """판매 테이블 - beautifly.sales"""
    __tablename__ = "sales"
    
    sales_id = Column(BigInteger, primary_key=True)
    customer_id = Column(BigInteger, ForeignKey("customer.customer_id"), nullable=False)
    staff_id = Column(BigInteger, ForeignKey("staff.staff_id"), nullable=False)
    shop_id = Column(BigInteger, nullable=False)
    reservation_id = Column(BigInteger, ForeignKey("reservation.reservation_id"), nullable=True)
    payment_method = Column(
        Enum('CASH', 'CARD', 'PREPAID_PASS', 'SESSION_PASS', name='payment_method_enum'),
        nullable=False
    )
    discount_rate = Column(Integer, nullable=True)
    retail_price = Column(Integer, nullable=True)
    discount_amount = Column(Integer, nullable=True)
    total_amount = Column(Integer, nullable=False)
    sales_memo = Column(String(255), nullable=True)
    sales_date = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    is_refunded = Column(TINYINT, nullable=False, default=0)
    created_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    modified_at = Column(TIMESTAMP, nullable=False, default=datetime.utcnow)
    deleted_at = Column(TIMESTAMP, nullable=True)
    
    # 관계 설정
    customer = relationship("Customer", back_populates="sales")
    staff = relationship("Staff", back_populates="sales")
    reservation = relationship("Reservation", back_populates="sales")
    
    def __repr__(self):
        return f"<Sales(id={self.sales_id}, customer_id={self.customer_id}, amount={self.total_amount})>"


class Tag(Base):
    """태그 테이블 - beautifly.tag"""
    __tablename__ = "tag"
    
    tag_id = Column(BigInteger, primary_key=True)
    # 기본 필드들만 정의 (전체 스키마는 필요시 추가)
    
    def __repr__(self):
        return f"<Tag(id={self.tag_id})>"


class TagByCustomer(Base):
    """고객별 태그 테이블 - beautifly.tag_by_customer"""
    __tablename__ = "tag_by_customer"
    
    customer_id = Column(BigInteger, ForeignKey("customer.customer_id"), primary_key=True)
    tag_id = Column(BigInteger, ForeignKey("tag.tag_id"), primary_key=True)
    
    def __repr__(self):
        return f"<TagByCustomer(customer_id={self.customer_id}, tag_id={self.tag_id})>"


# 인덱스 정의
Index('idx_customer_shop_id', Customer.shop_id)
Index('idx_customer_staff_id', Customer.staff_id)
Index('idx_customer_phone', Customer.phone_number)
Index('idx_customer_recent_visit', Customer.recent_visit_date)

Index('idx_staff_shop_id', Staff.shop_id)

Index('idx_primary_item_shop_id', PrimaryItem.shop_id)
Index('idx_secondary_item_primary_id', SecondaryItem.primary_item_id)

Index('idx_reservation_customer_id', Reservation.customer_id)
Index('idx_reservation_staff_id', Reservation.staff_id)
Index('idx_reservation_shop_id', Reservation.shop_id)
Index('idx_reservation_start_at', Reservation.reservation_start_at)
Index('idx_reservation_status', Reservation.reservation_status_name)

Index('idx_sales_customer_id', Sales.customer_id)
Index('idx_sales_staff_id', Sales.staff_id)
Index('idx_sales_shop_id', Sales.shop_id)
Index('idx_sales_date', Sales.sales_date)

Index('idx_tag_by_customer_tag_id', TagByCustomer.tag_id)

plt.rcParams['font.family'] = 'Malgun Gothic'  # Windows 기본 한글폰트 