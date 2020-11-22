# 2020년 특별공채 Server 개발 과제
## 1.개발환경
+ JDK : open jdk 11.0.9.1
+ Gradle : 5.6.4
+ DataBase : H2
+ Test Framework : Spock
+ Springboot 2.2.4
+ Bean Mapper : Mapstruct
+ API Documentation : Swagger ( OAS 3.0 )

## 2.주요 기능 정의
+ Token 발급
    + 단방향 검증이 되어야 한다.
    + 뿌리기 요청정보 ( 뿌리기 요청자, 방정보, 생성정보 ) 를 기준은로 Hash 값 생성
    + HTTP Header에 적재되는 요건을 충족하기 위하여 Header value에 포함 될수 있는 안전한 값 0-9 / a-z / A-Z 총 62개의 ascii code 값사용
    + 3byte 크기의 요건을 충족 하기 위하여 Hash 크기 4byte를 3등분 하야 약 11bit씩 분할
    + 11bit 의 번위는 0~2047 으로 사용가능한 값 62개의 범위를 초과 함으로 %연산을 이용 하여 각문자열 추출
    + Token의 크기가 3byte로 비교적 작은 크기 이기 때문에 중복의 가능 성이 있으나 DB의 Unique index 값을 이용 하여 동일방에 동일 요청자로 생성 제어
+ 금액 분배
    + 뿌리기 요청시 금액 분배 정보 생성
    + 한사람에게 뿌리기의 금액이 몰리는것을 방기하기 위하여 남은 최대금액의 특정비율(70%)이내 Random으로 추출 후 최종 차수에 나머지 금액 증액
+ 동시성 제어
    + n대의 서버에서 동작 할 것을 감안하여 Database의 ORDER BY 및 LIMIT를 이용한 UPDATE를 하여 DB Lock에 위임하고 짧은 Lock을 위하여 update에 포한된 Query만 Transaction 처리함
    + Queue System 도입 하여 DB Lock 기능을 사용 하지 않고 Application Level에서 동시성 제어가 가능 하나 개발 과제 성격에는 맞지 않는 것으로 판단 DB 고유 기능에 위임함
    
## 3.실행 환경
+ 별도 설정 필요 없음
+ swagger-ui : http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## 4.기 등록 데이타 (TEST시 참고용)
+ 등록된 회원 ID : IronMan / Hulk / Thor / Captain / BPanther / AntMan / BatMan / SuperMan / WonderWoman / Aquaman / Flash / Cyborg 총 12명
+ 등록된 방 : 1 / 2 / 3 / 4 총 4개
    + 1번방 구성원 : IronMan / Hulk / Thor / Captain / BPanther / AntMan 총 6명
    + 2번방 구성원 : BatMan / SuperMan / WonderWoman / Aquaman / Flash / Cyborg 총 6명
    + 3번방 구성원 : IronMan / Hulk / Thor / Captain / BPanther / AntMan 총 6명
    + 4번방 구성원 : BatMan / SuperMan / WonderWoman / Aquaman / Flash / Cyborg 총 6명
+ 등록된 뿌리기 및 받기 
    + 1번방 뿌리기 한 ID : IronMan
        + 받은사람 : Hulk / Thor / Captain / BPanther / AntMan 총 5명
        + 뿌리기 일시 : 2020-10-01 00:00:00
        + 받기 만료 일시 : 2020-10-01 00:10:00
        + token : ABC
    + 2번방 뿌리기 한 ID : BatMan
        + 받은사람 : SuperMan / WonderWoman / Aquaman / Flash / Cyborg 총 5명
        + 뿌리기 일시 : 2020-10-01 00:00:00
        + 받기 만료 일시 : 2020-10-01 00:10:00
        + token : ABC
    + 3번방 뿌리기 한 ID : IronMan
        + 받은사람 : Hulk / Thor / Captain / BPanther / AntMan 총 5명
        + 뿌리기 일시 : 서버 시작 일시
        + 받기 만료 일시 : 2020-11-30 00:10:00 (TEST를 위하여 임의 조정)
        + token : DEF
    + 4번방 뿌리기 한 ID : BatMan
        + 받은사람 : SuperMan / WonderWoman / Aquaman / Flash / Cyborg 총 5명
        + 뿌리기 일시 : 서버 시작 일시
        + 받기 만료 일시 : 2020-11-30 00:10:00 (TEST를 위하여 임의 조정)
        + token : DEF

## 5.API CURL Sample
+ 뿌리기
    + curl -X POST "http://localhost:8080/api/spread/2/1000" -H  "accept: application/vnd.kakaopay.api.spread-V1+json" -H  "X-USER-ID: Hulk" -H  "X-ROOM-ID: 3"
+ 받기
    + curl -X PATCH "http://localhost:8080/api/receive/DEF" -H  "accept: application/vnd.kakaopay.api.spread-V1+json" -H  "X-USER-ID: Thor" -H  "X-ROOM-ID: 3"
+ 조회
    + curl -X GET "http://localhost:8080/api/result/DEF" -H  "accept: application/vnd.kakaopay.api.spread-V1+json" -H  "X-USER-ID: IronMan" -H  "X-ROOM-ID: 3"