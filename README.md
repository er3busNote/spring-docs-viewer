# spring-docs-viewer

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-Spreadsheet%20&%20Docs-blue?style=flat&logo=apache&logoColor=white)](https://poi.apache.org)
[![Apache PDFBox](https://img.shields.io/badge/Apache%20PDFBox-PDF%20Processing-red?style=flat&logo=adobeacrobatreader&logoColor=white)](https://pdfbox.apache.org)
[![Docx4j](https://img.shields.io/badge/Docx4j-Microsoft%20Docx-blue?style=flat&logo=docx4j&logoColor=white)](https://www.docx4java.org/trac/docx4j)

Spring 기반 문서 뷰어 구현

## Init Setting
- [Spring Data JPA](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=3.5.3&packaging=jar&jvmVersion=17&groupId=com.docs&artifactId=viewer&name=viewer&description=Spring%20project%20for%20Docs%20Viewer&packageName=com.docs.viewer&dependencies=data-jpa,validation,devtools,mariadb,lombok)

## Compatible
- JAVA: OpenJDK 17
- DB: 10.4.11-MariaDB

## Progress

### 1. `spring-docs-viewer-libre-office/{plugin,maven}`
- **구현 방식**
  - LibreOffice + JNI
  - **UNO (Universal Network Objects) API** 기반
- **한계**
  - **성능**
    - UNO는 내부적으로 **Inter-Process Communication(IPC)** 을 사용
    - 대량의 셀 처리나 문서 변환(Batch Processing) 시 **병목 현상 발생 가능**
  - **환경 구성**
    - 설정이 복잡함
    - LibreOffice 버전에 따른 **호환성 문제** 존재

---

### 2. `spring-docs-viewer-jacob`
- **구현 방식**
  - JACOB(Java-COM Bridge) 기반
  - Windows의 **COM(Component Object Model)** 을 통해 MS Office 자동화
- **특징**
  - 문서 뷰어 전용 컨트롤이 아니라 **실제 Microsoft Office 프로그램을 실행하여 조작하는 방식**
  - 일반적으로 **문서 뷰어**보다는 **자동 문서 생성/편집** 용도로 많이 사용
- **한계**
  - 해당 실행 환경에 **Microsoft Office가 반드시 설치**되어 있어야 함

---

### 3. `spring-docs-viewer-core`
- **구현 방식**
  - `Java → JNI → Go → C# NativeAOT`
  - C# 라이브러리
    - OpenXML
    - SkiaSharp
- **목적**
  - 문서 뷰어 엔진을 별도 Repository로 분리하여 활용하려는 구조
- **결론**
  - 구조가 복잡하고 **유지보수가 용이하지 않음**
  - 문서 뷰어 엔진 Repository로 활용하는 방향은 **Drop**

---

### 4. `dotnet-docs-viewer`
- **구현 방식**
  - C# / .NET
  - OpenXML + SkiaSharp
- **한계**
  - **DOCX → PNG 렌더링**
    - DOCX를 PNG로 직접 변환할 수 있는 적절한 오픈소스 솔루션이 사실상 없음
  - **OpenXML SDK의 역할 한계**
    - OpenXML SDK는 **Office 문서의 XML 구조를 읽고/수정하는 라이브러리**
    - 실제 Word의 레이아웃을 계산하고 화면에 그리는 **렌더링 엔진은 아님**
- **결론**
  - OpenXML + SkiaSharp만으로는 **Word 문서 렌더링을 완전히 구현하기 어려움**
  - **순수 .NET Core API 기반 구현은 현실적으로 어려움**

---

### 5. `spring-docs-viewer` ⭐
- **구현 방식**
  - **순수 Java API 기반**
  - Apache POI
  - Docx4j
  - PDFBox
- **목표**
  - 외부 프로그램(Microsoft Office, LibreOffice)에 의존하지 않고
  - **Java 환경에서 문서 처리 및 렌더링을 구현**
- **현재 상태**
  - **순수 Java API 기반 문서 뷰어 구현 진행 중**
  - 현재까지 검토한 방식 중 **유지보수성과 Java 환경에서의 활용성을 고려하여 최종 후보로 진행**

## Contact us
- qudwn0768@naver.com