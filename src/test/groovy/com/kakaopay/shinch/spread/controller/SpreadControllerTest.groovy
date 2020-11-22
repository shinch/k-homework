package com.kakaopay.shinch.spread.controller

import com.kakaopay.shinch.spread.mapper.ControllerMapper
import com.kakaopay.shinch.spread.service.SpreadService
import com.kakaopay.shinch.spread.service.model.ReceiveDto
import com.kakaopay.shinch.spread.service.model.SearchDto
import com.kakaopay.shinch.spread.service.model.SearchResultDto
import com.kakaopay.shinch.spread.service.model.SpreadDto
import org.assertj.core.util.Lists
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.executable.ExecutableValidator
import java.lang.reflect.Method

class SpreadControllerTest extends Specification {
    SpreadService spreadService
    ControllerMapper controllerMapper

    SpreadController spreadController
    ExecutableValidator executableValidator

    def "setup"() {
        spreadService = Mock(SpreadService)
        controllerMapper = Mock(ControllerMapper)
        spreadController = new SpreadController(spreadService, controllerMapper)
        executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables()
    }

    @Unroll
    def "뿌리기 param 유효성 확인[뿌리기수 : #personnel, 금액 : #amount, 뿌리는사람 : #userId, 방번호: #roomId, 에러수 : #validSize]"() {
        expect:
        Method testMethod = spreadController.getClass().getMethod("postSpreadV1", Integer.class, Integer.class, String.class, Integer.class)
        Object[] params = [personnel, amount, userId, roomId]
        Set<ConstraintViolation<SpreadController>> violations = executableValidator.validateParameters(spreadController, testMethod, params)
        validSize == violations.size()
        where:
        personnel|amount|userId|roomId||validSize
        null     |null  |null  |null  ||4
        0        |0     |""    |0     ||4
        10       |10000 |"USER"|13    ||0
    }

    def "뿌리기 확인"() {
        given:
        Integer personnel = 5
        Integer amount = 20000
        String userId = "USER_ID"
        Integer roomId = 3
        SpreadDto spreadDto = new SpreadDto()
        when:
        spreadController.postSpreadV1(personnel, amount, userId, roomId)
        then:
        1 * controllerMapper.toSpreadDto(personnel, amount, userId, roomId) >> spreadDto
        1 * spreadService.spread(spreadDto)
    }

    @Unroll
    def "받기 param 유효성 확인[토큰 : #token, 받는사람 : #userId, 방법호 : #roomId, 에러수 : #validSize]"() {
        expect:
        Method testMethod = spreadController.getClass().getMethod("patchReceiveV1", String.class, String.class, Integer.class)
        Object[] params = [token, userId, roomId]
        Set<ConstraintViolation<SpreadController>> violations = executableValidator.validateParameters(spreadController, testMethod, params)
        validSize == violations.size()
        where:
        token |userId|roomId||validSize
        null  |null  |null  ||3
        ""    |""    |0     ||4
        "ABCD"|""    |0     ||3
        "ABC" |"USER"|10    ||0
    }

    def "받기 확인"() {
        given:
        String token = "TOK"
        String userId = "USER_ID"
        Integer roomId = 3
        ReceiveDto receiveDto = new ReceiveDto()
        when:
        spreadController.patchReceiveV1(token, userId, roomId)
        then:
        1 * controllerMapper.toReceiveDto(token, userId, roomId) >> receiveDto
        1 * spreadService.receive(receiveDto)
    }

    @Unroll
    def "뿌리기 결과 확인 param 유효성 확인[토큰 : #token, 받는사람 : #userId, 방법호 : #roomId, 에러수 : #validSize]"() {
        expect:
        Method testMethod = spreadController.getClass().getMethod("getResultV1", String.class, String.class, Integer.class)
        Object[] params = [token, userId, roomId]
        Set<ConstraintViolation<SpreadController>> violations = executableValidator.validateParameters(spreadController, testMethod, params)
        validSize == violations.size()
        where:
        token |userId|roomId||validSize
        null  |null  |null  ||3
        ""    |""    |0     ||4
        "ABCD"|""    |0     ||3
        "ABC" |"USER"|10    ||0
    }

    def "뿌리기 결과 확인"() {
        given:
        String token = "TOK"
        String userId = "USER_ID"
        Integer roomId = 3
        SearchDto searchDto = new SearchDto()
        List<SearchResultDto> results = Lists.newArrayList()
        when:
        spreadController.getResultV1(token, userId, roomId)
        then:
        1 * controllerMapper.toSearchDto(token, userId, roomId) >> searchDto
        1 * spreadService.search(searchDto) >> results
        1 * controllerMapper.toResultResponseV1s(results)
    }

}
