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
1. spring-docs-viewer-libre-office-{plugin,maven}
   - LibreOffice + JNI 조합
   - **UNO**(Universal Network Objects) API 기반
   - 단점 #1 : UNO는 내부적으로 **inter-process 통신**을 사용하기 때문에 대량의 셀 처리나 문서 변환(Batch Processing) 시, **병목 현상**이 발생하기 쉬움
   - 단점 #2 : **환경설정이 복잡**하고, **버전 호환성 문제**가 존재함 
2. spring-docs-viewer-activex
   - MS Office 프로그램을 자동 조작할 수 있게 해주는 **COM 기반** C# API
   - Interop는 ⌜**문서 보기 전용 컨트롤**⌟이 아니라, ⌜**실제 Office를 띄우는 방식**⌟
   - 대게 ⌜**문서 뷰어**⌟ 보다는 ⌜**자동 문서 생성/편집**⌟에 더 자주 쓰임
   - 단점 : 해당 PC에 **Microsoft Office가 반드시 설치**되어 있어야 함
3. spring-docs-viewer-core
   - Java → JNI → Go → C# NativeAOT 조합
   - 문서뷰어 엔진 리포지토리
4. spring-docs-viewer
   - Apache POI + Docx4j + PDFBox 조합 

## Contact us
- qudwn0768@naver.com