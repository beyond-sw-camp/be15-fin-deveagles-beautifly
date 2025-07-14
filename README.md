# DevEagles 프로젝트 Beautifly
<p align="center">
<img width="500" height="1024" alt="image" src="https://github.com/user-attachments/assets/a292ebac-5bf6-48a3-86bf-409224a2dcae" />
</p>

## 🦅 DevEagles 팀원 소개 

<table style="width: 100%; text-align: center;"> 
  <tr>
    <td align="center"> <a href="https://github.com/gyeongmin03">김경민</a></td>
    <td align="center"> <a href="https://github.com/wishbornDev">김소원</a></td>
    <td align="center"> <a href="https://github.com/hwan1023">김태환</a></td>
    <td align="center"> <a href="https://github.com/64etuor">박양하</a></td>
    <td align="center"> <a href="https://github.com/Jayboo816">부재녕</a></td>
    <td align="center"> <a href="https://github.com/nineeko">이채은</a></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/user-attachments/assets/afb03b37-d206-4c4a-bdf5-a2c348e0fb04" width="150px" height="170px"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/3b2483aa-747e-4856-8161-bcf6cdf75835" width="150px" height="170px"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/721c61e1-99dd-4428-83d5-76a399eb5065" width="150px" height="170px"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/25e8d110-0f34-47f0-958a-293b0275845b" width="150px" height="170px"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/4fdd746d-bc5a-47cb-ae4f-53137e06148c" width="150px" height="170px"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/398a180c-2d20-4b6a-93ac-86b9440b49b4" width="150px" height="170px"/></td>
  </tr>
</table>
<br><br>

### 프로젝트 구조

```
deveagles-fin-repo/
├── be15_DevEagles_BE/     # Spring Boot 백엔드
├── be15_DevEagles_DA/     # Python 데이터 분석
└── be15_DevEagles_FE/     # Vue.js 프론트엔드
```

<br><br>

## 💻 Beautifly 개요

📑 <a href="#1">1. 프로젝트 기획</a>

  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-1">1-1. 주제</a>

  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-2">1-2. 배경 및 필요성</a>
 
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-3">1-3. 차별점</a>
 
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-4">1-4. 주요 기능</a>
  
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-5">1-5. 요구사항 명세서 </a> 

  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-6">1-6. WBS </a>  

🗃️ <a href="#2">2. DB 모델링</a>

  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2-1">2-1. 논리 /물리 모델링</a>

  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2-2">2-2. DDL </a>

📲 <a href="#3">3. 화면 설계</a>

⚙️ <a href="#4">4. 빌드 및 배포 문서 </a>

🛠️ <a href="#5">5. 단위 테스트 </a>

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#5-1">5-1. 백엔드 단위 테스트 결과 (swagger API) </a>
 
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#5-2">5-2. 프론트엔드 단위 테스트 결과 </a>

🗒️ <a href="#6">6. 프로젝트 아키텍처 구조 </a>

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#6-1">6-1. 아키텍처 구조 </a>

👾 <a href="#7">7. 기술 스택 </a>

💡 <a href="#8">8. 형상관리 </a>

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#8-1">8-1. git을 활용한 형상 관리 </a>
 
🐞 <a href="#9">9. 트러블슈팅 </a>

🧑‍💻 <a href="#10">10. 팀원 회고 </a>

<br><br>

## <p id="1"> 📑 1. 프로젝트 기획 </p>
### <p id="1-1"> 1-1. 주제 </p>
** 뷰티샵을 위한 올인원 웹 기반 고객 관리 및 마케팅 자동화 CRM 솔루션 **
> CRM 핵심 기능(예약·고객·매출 관리) + 직관적인 UI 제공 + 차별화된 마케팅 자동화 및 고객 리텐션 강화

<br>

### <p id="1-2"> 1-2. 배경 및 필요성 </p>
<img width="560" height="454" alt="image" src="https://github.com/user-attachments/assets/1ce132a0-f7f2-465a-982d-28c5718eb156" />

#### 💄 1. 뷰티 산업의 성장과 디지털 전환의 필요
- 2025년 글로벌 뷰티 및 퍼스널 케어 시장 규모는 약 **6,772억 달러**로 예상됨
- 코로나19 이후 **비대면 트렌드와 디지털 전환**이 빠르게 확산되면서, 뷰티 업계에서도 **고객 데이터를 기반으로 한 관리 및 마케팅의 중요성**이 커지고 있음
- 그러나 실제 현장에서는 여전히 **수기 작성이나 엑셀** 등 비효율적인 방식으로 고객/예약/시술 이력을 관리하는 경우가 다수 존재

#### 🧾 2. 현장 중심의 불편한 운영 실태
- 고객 정보를 수기로 적거나 따로 보관해 놓아 **접근성과 활용도가 떨어짐**
- 고객별 시술 이력, 선호도, 방문 주기 등을 파악하기 어려워 **맞춤 응대에 한계**
- 재방문 유도를 위한 **마케팅 활동(문자, 알림 등)은 수작업**으로 진행되어 시간과 비용 소모가 큼
- **예약 중복, 노쇼 방지** 등의 기능도 미비하여 운영 효율이 떨어짐

#### 💡 3. 기존 CRM 솔루션의 한계
- 대부분의 솔루션이 **예약 중심**이며, **고객 분석이나 마케팅 자동화** 기능이 부재
- UI/UX가 복잡하거나 **비용이 높아 소형 매장에서 사용하기 어려움**
- 뷰티샵 특화 기능 부족 (예: 시술 이력, 담당자 기록, 방문 간격 분석 등)

#### 🔍 4. 시장의 기회
- 1인샵, 소형 매장 중심의 **뷰티 시장은 지속적으로 성장** 중
- 고객 데이터 기반의 마케팅 자동화 수요가 급증 중
- 네이버예약, POS 등과 **연동 가능한 경량화된 CRM 솔루션**에 대한 수요 존재

<br>

### <p id="1-3"> 1-3. 차별점 </p>

#### ✅ 1. 고객 중심 마케팅 자동화
- 고객 생일, 시술 이력, 방문 주기 등을 기반으로 한 **자동 메시지 발송** 기능 제공
- 예약 후 일정 기간 내 미방문 고객에게 **재방문 유도 알림 전송** 가능
- SMS 연동 기반으로 **쉽고 빠른 고객 커뮤니케이션** 가능

#### ✅ 2. 뷰티 업종 특화된 기능 구성
- 시술 이력, 담당자 기록, 고객 선호도 등 **뷰티샵에 꼭 필요한 정보 중심 UI/UX** 설계
- 고객 맞춤형 상담과 응대를 위한 **상세 정보 입력 구조** 제공

#### ✅ 3. 사용하기 쉬운 경량화된 CRM
- 1인샵/소형 매장도 부담 없이 사용할 수 있는 **직관적이고 가벼운 인터페이스**
- 필수 기능(고객, 예약, 매출, 메시지)에만 집중하여 **불필요한 복잡도 제거**

#### ✅ 4. 통합된 고객 · 예약 · 매출 관리
- 고객 관리, 예약 내역, 결제 정보, 시술 이력을 **한 화면에서 통합 관리**
- **시술 상품 카테고리, 결제수단, 할인 등 다양한 조건 기반 리포트 제공**

#### ✅ 5. 확장성과 연동성
- 네이버예약, POS 등 **외부 시스템과의 연동을 고려한 설계**
- 향후 **모바일 앱, 타 업종(애견미용, 테라피숍 등)으로도 확장 가능한 구조**

<br>

### <p id="1-4"> 1-4. 주요 기능 </p>

#### 고객 관리
- **고객 태그 관리**: 사용자는 고객 패턴에 따른 태그를 등록/수정/삭제할 수 있다.
- **태그 클릭 조회**: 특정 태그를 클릭하면 해당 태그를 가진 고객들을 필터링해 조회할 수 있다.
- **세그멘테이션**: 고객 데이터 분석을 통해 고객 세분화를 정의하고 이탈 위험군 세그먼트 태그를 부여할 수 있다. 

#### 예약 관리
- **예약 설정 관리**: 사용자는 매장의 예약 시간, 간격, 식사 시간에 대한 설정을 등록/조회/수정/삭제할 수 있다.
- **예약 관리**: 사용자는 고객의 예약을 등록/조회/수정/삭제할 수 있다.
- **예약 프로세스 지원**: 매장 별 별도 예약 페이지를 제공한다.

#### 데이터 분석
- **전체 매장 데이터** : 관리자는 전체 매장의 데이터를 취합하여 분석된 데이터를 대시보드로 확인할 수 있다.
- **마케팅 가이드**: 고객 세그먼트 및 이탈 위험 태그를 기반으로 적합한 마케팅 방법을 제시할 수 있다.
- **워크플로우**: 사용자는 고객의 등급, 태그 별 자동 워크플로우를 생성/조회/수정/삭제할 수 있다.

#### 마케팅
- **캠페인**: 사용자는 새 마케팅 캠페인을 생성/조회/수정/삭제할 수 있다.
- **쿠폰**: 사용자는 할인 및 무료 서비스 쿠폰을 생성/조회/수정/삭제할 수 있다.
- **프로필링크**: 사용자는 매장의 다양한 채널을 모아 보여주는 프로필 링크 페이지를 생성할 수 있다. 

<br>

### <p id="1-5"> 1-5. 요구사항 명세서 </p>
🔗 요구사항 명세서 정리 문서 : [요구사항 명세서](https://docs.google.com/spreadsheets/d/1de8mq1fLxvG3lpLMdgieSpFL43okKb8f5VAxNtNWPLE/edit?gid=1154602031#gid=1154602031)

### <p id="1-6"> 1-6. WBS </p>
🔗 WBS 정리 문서 : [WBS](https://docs.google.com/spreadsheets/d/1961AfrJP8OabgMbm27YaTH3RrsD5D1Qp9p9z-w2splY/edit?gid=901636027#gid=901636027)


<br><br>

## <p id="2"> 🗃️ 2. DB모델링 </p>
### <p id="2-1">2-1. 논리 / 물리 모델링</p>
![논리/물리 모델](https://github.com/user-attachments/assets/f1f85936-54dd-4968-8cc6-be72d3786d21)

### <p id="2-2"> 2-2. DDL </p>
🔗 SCRIPT : [DDL](https://github.com/BE15-DevEagles/be15-fin-deveagles-beautifly/wiki/D6.-DDL-Script)

<br><br>

## <p id="3"> 📲 3. 화면 설계</p>

🔗 화면 설계 문서 : [화면기능명세서](https://docs.google.com/presentation/d/1HQyAsLoTswNbl2u6PCYlA3Vwb7IoGB2Y/edit?slide=id.p1#slide=id.p1)

<details>
  <summary> 회원 / 직원 </summary>
</details>

<details>
  <summary> 고객 </summary>
</details>

<details>
  <summary> SMS / 챗봇 </summary>
</details>

<details>
  <summary> 일정 관리 </summary>
</details>

<details>
  <summary> 상품 / 매출 </summary>
</details>

<details>
  <summary> 마케팅 / 데이터 분석 </summary>
</details>

<details>
  <summary> 알림 </summary>
</details>

<br><br>

##  <p id="4"> ⚙️ 4. 빌드 및 배포 문서</p>

<br><br>

##  <p id="5"> 🛠️ 5. 단위 테스트 </p>
### <p id="5-1"> 5-1. 백엔드 단위 테스트 결과 (swagger API) </p>

<details>
<summary> 🔍 Swagger API 열기 </summary>
</details>

<br><br>

### <p id="5-2"> 5-2. 프론트엔드 단위 테스트 결과 </p>
🔗 테스트 결과서 : [프론트엔드 단위테스트](https://docs.google.com/spreadsheets/d/1FzMi2wAkGpdgU23Jc0TpxOCsrnXXxjCKGR8KUUb6x08/edit?gid=0#gid=0)


<br><br>

## <p id="6"> 🗒️ 6. 프로젝트 아키텍처 구조 </p>
### <p id="6-1">6-1. 아키텍처 구조 </p>
<details>
  <summary> 🧱 아키텍처 열기 </summary>
  
  ![프로젝트 아키텍처](https://github.com/user-attachments/assets/92d33032-243a-43ec-8f65-20621c12b18e)
  
</details>

<br><br>

## <p id="7"> 👾 7. 기술 스택 </p>

### 백엔드 (be15_DevEagles_BE)

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Database**: MariaDB
- **ORM**: JPA, MyBatis
- **Security**: Spring Security + JWT
- **Documentation**: Swagger/OpenAPI
- **Search**: Elasticsearch
- **Build Tool**: Gradle

### 프론트엔드 (be15_DevEagles_FE)

- **Framework**: Vue.js 3.5.13
- **Build Tool**: Vite
- **UI Library**: PrimeVue 4.3.5
- **Charts**: Chart.js, ECharts, Vue-Chartjs
- **Styling**: TailwindCSS
- **Icons**: Lucide Vue, PrimeIcons
- **Calendar**: FullCalendar
- **State Management**: Pinia

### 데이터 분석 (be15_DevEagles_DA)

- **Framework**: FastAPI
- **Language**: Python 3.11+
- **Analytics DB**: DuckDB (어플리케이션 레벨 DB)
- **ML Libraries**: Scikit-learn, Pandas, NumPy
- **Monitoring**: Prometheus, Grafana (개발 모니터링), CloudWatch(프로덕션)

<br><br>

## <p id="8"> 💡 8. 형상 관리 </p>
### <p id="8-1">8-1. git을 통한 형상 관리 </p>
#### **1. 코드 및 Git 컨벤션**

#### **1.1. 코드 포맷팅**

> 코드 포맷팅 pre-commit git hook은 프론트엔드 루트 폴더에서 npm install을 하면 자동 설정됩니다.
> 
> 🔗 관련 문서: [개발 환경 설정 #Pre-commit](https://github.com/BE15-DevEagles/be15-fin-deveagles/wiki/D1.-%EA%B0%9C%EB%B0%9C-%ED%99%98%EA%B2%BD-%EC%84%A4%EC%A0%95#git-pre-commit-hook)

#### **Java**

- `pre-commit hook`으로 `Spotless` 컨벤션을 적용하여 코드 스타일을 강제합니다.
- **적용 규칙**:
    
    Java
    
    ```
    spotless {
        java {
            // import 순서: java, javax, org, com, 그 외
            importOrder('java', 'javax', 'org', 'com', '')
    
            // 사용하지 않는 import 문 자동 제거
            removeUnusedImports()
    
            // Google Java Format 적용 (버전 명시)
            googleJavaFormat('1.27.0')
    
            // 어노테이션 포맷팅
            formatAnnotations()
        }
    }
    ```
    

#### **Vue 3 / JavaScript**

- **Prettier**: 일관된 코드 포맷팅을 자동화합니다.
- **ESLint**: 문법 및 스타일을 검사합니다 (`eslint-config-standard`, `eslint-plugin-vue`, `@typescript-eslint` 플러그인 사용).
- **eslint-plugin-unused-imports**: 사용되지 않는 import를 자동으로 감지하고 제거합니다.
- **Husky + lint-staged**: Git Hook을 사용하여 커밋 전 `Prettier 포맷팅`, `ESLint --fix`, `미사용 import 정리`를 자동으로 실행합니다.

#### **1.2. Git 워크플로우 & 브랜치 전략**

- **브랜치 생성 정책**: GitHub 이슈당 하나의 브랜치를 생성합니다.
    - 브랜치명에는 반드시 이슈 번호를 포함합니다. (예: `feat/user/로그인-기능#90`)
- **워크플로우**: AWS 배포 전까지 **Gitflow** 전략을 따릅니다. 배포 후 전략은 상의 후 결정 `todo`
- **브랜치 유형**:
    - `feat/`: 신규 기능 개발
    - `fix/`: 버그 및 기타 수정
    - `release/`: 릴리즈 준비
    - `hotfix/`: 긴급 패치
    - `chore/`: 빌드, 인프라, 도구, 문서 등 비즈니스 로직과 무관한 작업
    - `refactor/`: 코드 리팩토링
- **PR 규칙**: 최소 1명 이상의 코드 리뷰를 거치며, 머지 전 CI 빌드가 반드시 통과해야 합니다.

#### **1.3. 이슈 및 PR 네이밍 컨벤션**

#### **이슈 네이밍**

```
[영문도메인명] 간단한 설명 - 상세 설명
```

- **예시**:
    - `[user] 로그인 API - JWT 토큰 발급 기능 구현`
    - `[notification] 상품 목록 - 무한 스크롤 및 필터링 기능 추가`
    - `[reservation] 버그 수정 - 주문 취소 시 재고 복구 오류`

#### **PR 네이밍**

```
[영문도메인명] 간단한 설명 #<이슈번호>
```

- **예시**:
    - `[user] 로그인 API 구현 #123`
    - `[sales] 주문 취소 버그 수정 #456`
    - `[user] 사용자 서비스 리팩토링 #789`

> **중요**: PR 제목에 `#이슈번호`를 포함해야 GitHub Actions가 이슈 내용을 PR에 자동으로 연결합니다.

#### **브랜치 네이밍**

```
타입/도메인/간단설명#<이슈번호>
```

- **예시**:
    - `feat/user/로그인-기능#1`
    - `fix/sales/주문취소-버그#2`
    - `chore/backend/의존성-업데이트#6`

#### **1.4. 커밋 메시지**

```
타입: 요약 설명 #<이슈번호>

(선택) 본문: 변경 이유 및 상세 설명

(선택) 꼬리말: 관련 이슈, 해결된 이슈 등
```

- **예시**:

```
feat: JWT 토큰 발급 로직 추가 #123
사용자 인증을 위해 JWT 기반의 토큰 생성 및 검증 기능을 구현했습니다.
- 만료 시간: 1시간
- 서명 알고리즘: HS256
  ```    

**필드 상세 설명**

|   |   |   |
|---|---|---|
|**필드**|**설명**|**예시**|
|**type**|변경 유형 (feat, fix, docs, style, refactor, test, chore 등)|`feat`, `fix`, `docs`|
|**이슈번호**|연결할 GitHub 이슈 번호|`#123`|
|**body**|변경 내용, 이유와 상세 설명 (무엇을, 왜 변경했는지)|`JWT 토큰 만료 로직 추가, 테스트 시나리오 보강`|

🔗 워크플로우 자동화 : [GitHub Actions 워크플로우 가이드](https://github.com/BE15-DevEagles/be15-fin-deveagles-beautifly/wiki/B3.-Github-Actions)

<br><br>

##  <p id="9"> 🐞 9. 트러블슈팅</p>

<br><br>

##  <p id="10"> 🧑‍💻 10. 팀원 회고</p>

## 주요 산출물

- [[프로젝트 기획서]](https://github.com/BE15-DevEagles/be15-fin-deveagles/wiki/P6.-%EB%B7%B0%ED%8B%B0%EC%83%B5-CRM-%EA%B8%B0%ED%9A%8D%EC%84%9C)
- [[요구사항 명세서]](https://docs.google.com/spreadsheets/d/1de8mq1fLxvG3lpLMdgieSpFL43okKb8f5VAxNtNWPLE/edit?gid=1549221980#gid=1549221980)
- [[화면 기능 명세서]](https://docs.google.com/presentation/d/1HQyAsLoTswNbl2u6PCYlA3Vwb7IoGB2Y/edit?usp=sharing&ouid=114008940664253191654&rtpof=true&sd=true)
- [[WBS]](https://docs.google.com/spreadsheets/d/1961AfrJP8OabgMbm27YaTH3RrsD5D1Qp9p9z-w2splY/edit?usp=sharing)
- [[와이어 프레임]](https://www.figma.com/board/7oXTmpeg0jRH39mmhHy8f3/CRM?node-id=479-28220&t=PIuiS7WXftFfBhEt-1)
- [[시스템 아키텍쳐]](https://github.com/BE15-DevEagles/be15-fin-deveagles/wiki/A2.-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B3%90)
- [[ERD]](https://github.com/BE15-DevEagles/be15-fin-deveagles/wiki/A3.-ERD)
