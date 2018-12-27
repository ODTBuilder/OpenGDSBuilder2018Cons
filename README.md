<a name="korean"></a>
OpenGDSBuilder2018Cons (공간자료 검증도구) 
=======
이 프로젝트는 국토공간정보연구사업 중 [공간정보 SW 활용을 위한 오픈소스 가공기술 개발]과제의 4차년도 연구성과 입니다.<br>
정식 버전은 차후에 통합된 환경에서 제공될 예정입니다.<br>
이 프로그램들은 완성되지 않았으며, 최종 완료 전 까지 문제가 발생할 수도 있습니다.<br>
발생된 문제는 최종 사용자에게 있으며, 완료가 된다면 제시된 라이선스 및 규약을 적용할 예정입니다.<br>

감사합니다.<br>
공간정보기술(주) 연구소 <link>http://www.git.co.kr/<br>
OpenGeoDT 팀


특징
=====
- 공간자료 검증도구 v1.0은 자사 GIS 통합 솔루션인 GeoDT 2.2 기반의 웹 기반 공간데이터 편집/검수 솔루션임.
- 웹 페이지상에서 공간정보의 기하학적/논리적 구조와 속성값에 대한 검수편집 기능을 제공함.
- 다양한 웹 브라우저 지원가능, 플러그인 및 ActiveX 설치 없이 사용 가능함.
- JavaScript, Java 라이브러리 형태로 개발되어 사용자 요구사항에 따라 커스터 마이징 및 확장이 가능함.
- OGC 표준준수, 국내 수치지형도 작성 작업규정을 따르는 20여종의 검수기능을 제공함. 


연구기관
=====
- 세부 책임 : 부산대학교 <link>http://www.pusan.ac.kr/<br>
- 연구 책임 : 국토연구원 <link>http://www.krihs.re.kr/


Getting Started
=====
### 1. 개발환경 ###
- Java - OpenGDK 1.8.0.111 64 bit
- Tomcat - Tomcat8.0.43 64bit
- eclipse neon 
- PostgreSQL 9.4 
- Geoserver 2.13.0

### 2. 소스코드 설치 및 프로젝트 실행 ###
- https://github.com/ODTBuilder/OpenGDSBuilder2018Cons 접속 후 소스코드 다운로드
- eclipse 실행 후 zip 파일 형태로 Project Import
- 프로젝트 경로내 src/main/resources/application.yml 접근 후 다음과 같이 수정
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 spring:
  rabbitmq:
    host: 래빗앰큐 서버 주소
    port: 래빗앰큐 서버 포트번호
    virtual-host: 래빗앰큐 가상 계정 이름
    username: 래빗앰큐 실제 접속 계정
    password: 래빗앰큐 실제 접속 비밀번호
    template:
      exchange: 사용할 익스체인지 이름
      routing-key: 웹 검수에 사용할 라우팅키 값
      routing-key-mobile: 모바일 검수에 사용할 라우팅키 값
  
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:postgresql://데이터베이스 주소:포트번호/데이터베이스 이름?charSet=UTF-8&prepareThreshold=1
    username: 데이터베이스 계정명
    password: 데이터베이스 비밀번호
    driver-class-name: org.postgresql.Driver  
   
 server:
  servlet:
    context-path: /geodt
    port: 연결될 OpenGDSBuilder2018Prod 서버의 포트번호
 gitrnd:
  serverhost: 연결될 OpenGDSBuilder2018Prod 서버의 주소
  rabbitmq:
    queue: 웹 검수에 사용할 래빗앰큐 큐 이름
    mobilequeue: 모바일 검수에 사용할 래빗앰큐 큐 이름
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
### 3. 빌드 및 실행 ###
 - eclipse에서 프로젝트 오른클릭 > Rus as > Maven Build...
 - Goals에 package 입력 후 Run 클릭
 - 프로젝트 경로 내 target/opengds2018cons-0.0.1-SNAPSHOT.jar 복사 후 실행할 경로에 붙여넣기
 - jar파일을 넣은 경로에서 shift+오른클릭 후 여기서 명령창 열기
 - java -jar opengds2018cons-0.0.1-SNAPSHOT.jar 입력 후 엔터
 - 정상시작 로그 확인

사용 라이브러리
=====
GeoTools 16.5 (LGPL) http://www.geotools.org/
등

Mail
=====
Developer : SG.LEE
ghre55@git.co.kr



-----
