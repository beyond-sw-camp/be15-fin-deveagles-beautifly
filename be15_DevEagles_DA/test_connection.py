#!/usr/bin/env python3
"""
연결 테스트 스크립트
백엔드와 Python 서버 간의 연결을 테스트합니다.
"""

import requests
import json
import sys
from datetime import datetime


def test_python_server():
    """Python 분석 서버 연결 테스트"""
    try:
        # 포트 8000과 8001 모두 시도
        ports = [8000, 8001]
        
        for port in ports:
            try:
                url = f"http://localhost:{port}/api/v1/analytics/customers/1/risk-analysis"
                response = requests.post(url, timeout=5)
                
                if response.status_code == 200:
                    print(f"✅ Python 서버 연결 성공 (포트 {port})")
                    return port, response.json()
                elif response.status_code == 404:
                    print(f"⚠️ Python 서버 연결됨 (포트 {port}), 하지만 고객 데이터 없음")
                    return port, {"status": "connected", "error": "no data"}
                else:
                    print(f"⚠️ Python 서버 응답 (포트 {port}): {response.status_code}")
                    
            except requests.exceptions.ConnectionError:
                print(f"❌ Python 서버 연결 실패 (포트 {port})")
                
        return None, None
        
    except Exception as e:
        print(f"❌ Python 서버 테스트 실패: {e}")
        return None, None


def test_backend_server():
    """백엔드 서버 연결 테스트"""
    try:
        # 백엔드 포트 8080 확인
        url = "http://localhost:8080/analytics/customers/1/risk-analysis"
        response = requests.post(url, timeout=5)
        
        if response.status_code in [200, 404, 500]:
            print(f"✅ 백엔드 서버 연결 성공 (응답 코드: {response.status_code})")
            return True, response.status_code
        else:
            print(f"⚠️ 백엔드 서버 예상치 못한 응답: {response.status_code}")
            return False, response.status_code
            
    except requests.exceptions.ConnectionError:
        print("❌ 백엔드 서버 연결 실패 (포트 8080)")
        return False, None
    except Exception as e:
        print(f"❌ 백엔드 서버 테스트 실패: {e}")
        return False, None


def test_cross_connection():
    """Python 서버 -> 백엔드 서버 연결 테스트"""
    try:
        # Python 서버에서 배치 세그먼트 실행 (dry_run=true)
        ports = [8000, 8001]
        
        for port in ports:
            try:
                url = f"http://localhost:{port}/api/v1/analytics/customers/batch-risk-segmenting"
                data = {"dry_run": True}
                
                response = requests.post(url, json=data, timeout=10)
                
                if response.status_code == 200:
                    result = response.json()
                    print(f"✅ 크로스 연결 테스트 성공 (포트 {port})")
                    print(f"   배치 결과: {result}")
                    return True
                elif response.status_code == 404:
                    print(f"⚠️ 크로스 연결 가능하지만 데이터 없음 (포트 {port})")
                    return True
                else:
                    print(f"⚠️ 크로스 연결 응답 (포트 {port}): {response.status_code}")
                    
            except requests.exceptions.ConnectionError:
                continue
                
        print("❌ 크로스 연결 테스트 실패")
        return False
        
    except Exception as e:
        print(f"❌ 크로스 연결 테스트 실패: {e}")
        return False


def test_segment_update():
    """세그먼트 업데이트 테스트"""
    try:
        # 백엔드로 직접 세그먼트 업데이트 요청
        url = "http://localhost:8080/analytics/customers/1/update-risk-segments"
        data = {
            "segments": ["churn_risk_high", "vip_attention_needed"]
        }
        
        response = requests.post(url, json=data, timeout=5)
        
        if response.status_code == 200:
            print("✅ 세그먼트 업데이트 테스트 성공")
            return True
        elif response.status_code == 404:
            print("⚠️ 세그먼트 업데이트 가능하지만 고객 없음")
            return True
        else:
            print(f"⚠️ 세그먼트 업데이트 응답: {response.status_code}")
            print(f"   응답 내용: {response.text}")
            return False
            
    except requests.exceptions.ConnectionError:
        print("❌ 백엔드 서버 연결 불가 - 세그먼트 업데이트 실패")
        return False
    except Exception as e:
        print(f"❌ 세그먼트 업데이트 테스트 실패: {e}")
        return False


def main():
    """메인 테스트 함수"""
    print("=" * 60)
    print("DevEagles Analytics 연결 테스트")
    print(f"테스트 시간: {datetime.now()}")
    print("=" * 60)
    
    # 1. Python 서버 테스트
    print("\n1. Python 분석 서버 테스트")
    python_port, python_result = test_python_server()
    
    # 2. 백엔드 서버 테스트  
    print("\n2. 백엔드 서버 테스트")
    backend_ok, backend_status = test_backend_server()
    
    # 3. 크로스 연결 테스트
    print("\n3. Python -> 백엔드 크로스 연결 테스트")
    cross_ok = test_cross_connection()
    
    # 4. 세그먼트 업데이트 테스트
    print("\n4. 세그먼트 업데이트 테스트")
    segment_ok = test_segment_update()
    
    # 결과 요약
    print("\n" + "=" * 60)
    print("테스트 결과 요약")
    print("=" * 60)
    
    if python_port:
        print(f"✅ Python 서버: 연결됨 (포트 {python_port})")
    else:
        print("❌ Python 서버: 연결 안됨")
        
    if backend_ok:
        print("✅ 백엔드 서버: 연결됨")
    else:
        print("❌ 백엔드 서버: 연결 안됨")
        
    if cross_ok:
        print("✅ 크로스 연결: 정상")
    else:
        print("❌ 크로스 연결: 실패")
        
    if segment_ok:
        print("✅ 세그먼트 업데이트: 정상")
    else:
        print("❌ 세그먼트 업데이트: 실패")
    
    # 전체 상태
    if python_port and backend_ok:
        print("\n🎉 모든 서버가 실행 중입니다!")
        print("💡 이제 위험도 분석과 세그먼트 업데이트를 테스트할 수 있습니다.")
    else:
        print("\n⚠️ 일부 서버가 실행되지 않고 있습니다.")
        print("💡 서버를 먼저 실행해주세요.")
        
    print("\n" + "=" * 60)


if __name__ == "__main__":
    main()