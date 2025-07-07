#!/usr/bin/env python3
"""Database schema inspection script."""

import sys
from pathlib import Path
from sqlalchemy import create_engine, text, inspect

# 프로젝트 루트를 Python 경로에 추가
sys.path.insert(0, str(Path(__file__).parent.parent.parent / "src"))

def inspect_table_schema(engine, table_name):
    """테이블 스키마 상세 정보 조회."""
    print(f"\n📋 Table: {table_name}")
    print("=" * 60)
    
    try:
        inspector = inspect(engine)
        columns = inspector.get_columns(table_name)
        
        print("Columns:")
        for col in columns:
            nullable = "NULL" if col['nullable'] else "NOT NULL"
            default = f" DEFAULT {col['default']}" if col['default'] else ""
            print(f"  - {col['name']}: {col['type']} {nullable}{default}")
        
        # 기본 키 정보
        pk_constraint = inspector.get_pk_constraint(table_name)
        if pk_constraint['constrained_columns']:
            print(f"Primary Key: {pk_constraint['constrained_columns']}")
        
        # 외래 키 정보
        fk_constraints = inspector.get_foreign_keys(table_name)
        if fk_constraints:
            print("Foreign Keys:")
            for fk in fk_constraints:
                print(f"  - {fk['constrained_columns']} -> {fk['referred_table']}.{fk['referred_columns']}")
        
        # 인덱스 정보
        indexes = inspector.get_indexes(table_name)
        if indexes:
            print("Indexes:")
            for idx in indexes:
                unique = "UNIQUE" if idx['unique'] else ""
                print(f"  - {idx['name']}: {idx['column_names']} {unique}")
        
        # 데이터 샘플 조회
        with engine.connect() as conn:
            result = conn.execute(text(f"SELECT COUNT(*) FROM {table_name}"))
            count = result.fetchone()[0]
            print(f"Total records: {count}")
            
            if count > 0:
                # 처음 3개 레코드 조회
                result = conn.execute(text(f"SELECT * FROM {table_name} LIMIT 3"))
                records = result.fetchall()
                if records:
                    print("\nSample data:")
                    for i, record in enumerate(records, 1):
                        print(f"  Record {i}: {dict(record._mapping)}")
        
    except Exception as e:
        print(f"❌ Error inspecting table {table_name}: {e}")

def main():
    """메인 함수."""
    print("🔍 Inspecting beautifly database schema...")
    print("=" * 80)
    
    try:
        engine = create_engine("mysql+pymysql://swcamp:swcamp@localhost:3306/beautifly", echo=False)
        
        # 핵심 테이블들 먼저 확인
        key_tables = [
            'customer',      # 고객 테이블
            'staff',         # 직원 테이블
            'primary_item',  # 주요 서비스 아이템
            'secondary_item', # 부가 서비스 아이템
            'reservation',   # 예약 테이블
            'reservation_detail', # 예약 상세
            'sales',         # 판매 테이블
            'tag_by_customer', # 고객 태그
        ]
        
        for table_name in key_tables:
            inspect_table_schema(engine, table_name)
        
        print("\n" + "=" * 80)
        print("✅ Schema inspection completed!")
        
    except Exception as e:
        print(f"❌ Schema inspection failed: {e}")
        return False
    
    return True

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1) 