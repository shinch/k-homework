package com.kakaopay.shinch.spread.mapper

import com.kakaopay.shinch.spread.controller.model.ResultResponseV1
import com.kakaopay.shinch.spread.service.model.ReceiveDto
import com.kakaopay.shinch.spread.service.model.SearchDto
import com.kakaopay.shinch.spread.service.model.SearchResultDto
import com.kakaopay.shinch.spread.service.model.SpreadDto
import org.assertj.core.util.Lists
import org.mapstruct.factory.Mappers
import spock.lang.Specification

import java.time.LocalDateTime

class ControllerMapperTest extends Specification {
    ControllerMapper controllerMapper

    def "setup"() {
        controllerMapper = Mappers.getMapper(ControllerMapper.class)
    }

    def "SpreadDto로 변경 확인"() {
        given:
        Integer personnel = 3
        Integer amount = 10000
        String userId = "USER-ID"
        Integer roomId = 100
        when:
        SpreadDto spreadDto = controllerMapper.toSpreadDto(personnel, amount, userId, roomId)
        then:
        spreadDto.personnel == personnel
        spreadDto.amount == amount
        spreadDto.userId == userId
        spreadDto.roomId == roomId
    }

    def "SpreadDto로 변경 NULL 확인"() {
        given:
        Integer personnel = null
        Integer amount = null
        String userId = null
        Integer roomId = null
        when:
        SpreadDto spreadDto = controllerMapper.toSpreadDto(personnel, amount, userId, roomId)
        then:
        spreadDto == null
    }

    def "ReceiveDto로 변경 확인"() {
        given:
        String token = "9Aa"
        String userId = "USER-ID"
        Integer roomId = 100
        when:
        ReceiveDto receiveDto = controllerMapper.toReceiveDto(token, userId, roomId);
        then:
        receiveDto.token == token
        receiveDto.userId == userId
        receiveDto.roomId == roomId
    }

    def "ReceiveDto로 변경 NULL 확인"() {
        given:
        String token = null
        String userId = null
        Integer roomId = null
        when:
        ReceiveDto receiveDto = controllerMapper.toReceiveDto(token, userId, roomId);
        then:
        receiveDto == null
    }

    def "SearchDto로 변경 확인"() {
        given:
        String token = "9Aa"
        String userId = "USER-ID"
        Integer roomId = 100
        when:
        SearchDto searchDto = controllerMapper.toSearchDto(token, userId, roomId)
        then:
        searchDto.token == token
        searchDto.userId == userId
        searchDto.roomId == roomId
    }

    def "SearchDto로 변경 NULL 확인"() {
        given:
        String token = null
        String userId = null
        Integer roomId = null
        when:
        SearchDto searchDto = controllerMapper.toSearchDto(token, userId, roomId)
        then:
        searchDto == null
    }

    def "ResultResponseV1로 변경 NULL 확인"() {
        given:
        SearchResultDto inDto = null
        when:
        ResultResponseV1 resultResponseV1 = controllerMapper.toResultResponseV1(inDto)
        then:
        resultResponseV1 == null
    }

    def "ResultResponseV1 목록으로 변경 확인"() {
        given:
        SearchResultDto.ReceiveInfoDto innerDto = new SearchResultDto.ReceiveInfoDto()
        innerDto.userId = "USER-ID"
        innerDto.amount = 10000
        SearchResultDto inDto = new SearchResultDto()
        inDto.spreadAt = LocalDateTime.now()
        inDto.completeAmount = 8000
        inDto.spreadAmount = 5000
        inDto.receives = Lists.newArrayList(innerDto)
        when:
        List<ResultResponseV1> resultResponseV1s = controllerMapper.toResultResponseV1s(Lists.newArrayList(inDto))
        then:
        resultResponseV1s.size() == 1
    }

    def "ResultResponseV1 목록으로 변경 NULL 확인"() {
        when:
        List<ResultResponseV1> resultResponseV1s = controllerMapper.toResultResponseV1s(null)
        then:
        resultResponseV1s == null
    }

    def "ReceiveInfoVo로 변경 확인"() {
        given:
        SearchResultDto.ReceiveInfoDto receiveInfoDto = new SearchResultDto.ReceiveInfoDto()
        receiveInfoDto.userId = "USER-ID"
        receiveInfoDto.amount = 10000
        when:
        ResultResponseV1.ReceiveInfoVo receiveInfoVo = controllerMapper.toReceiveInfoVo(receiveInfoDto)
        then:
        receiveInfoVo.userId == receiveInfoDto.userId
        receiveInfoVo.amount == receiveInfoDto.amount
    }

    def "ReceiveInfoVo로 변경 NULL 확인"() {
        given:
        SearchResultDto.ReceiveInfoDto receiveInfoDto = null
        when:
        ResultResponseV1.ReceiveInfoVo receiveInfoVo = controllerMapper.toReceiveInfoVo(receiveInfoDto)
        then:
        receiveInfoVo == null
    }

    def "ReceiveInfoVo 목록으로 변경 확인"() {
        given:
        SearchResultDto.ReceiveInfoDto receiveInfoDto = new SearchResultDto.ReceiveInfoDto()
        receiveInfoDto.userId = "USER-ID"
        receiveInfoDto.amount = 10000
        when:
        List<ResultResponseV1.ReceiveInfoVo> receiveInfoVos = controllerMapper.toReceiveInfoVos(Lists.newArrayList(receiveInfoDto))
        then:
        receiveInfoVos.size() == 1
    }

    def "ReceiveInfoVo 목록으로 변경 NULL 확인"() {
        when:
        List<ResultResponseV1.ReceiveInfoVo> receiveInfoVos = controllerMapper.toReceiveInfoVos(null)
        then:
        receiveInfoVos == null
    }
}
