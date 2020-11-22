package com.kakaopay.shinch.spread.mapper

import com.kakaopay.shinch.spread.dao.spread.SpreadEntity
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailEntity
import com.kakaopay.shinch.spread.service.model.SearchResultDto
import com.kakaopay.shinch.spread.service.model.SpreadDto
import com.kakaopay.shinch.spread.util.TokenVo
import org.assertj.core.util.Lists
import org.mapstruct.factory.Mappers
import spock.lang.Specification

import java.time.LocalDateTime

class ServiceMapperTest extends Specification {
    ServiceMapper serviceMapper

    def "setup"() {
        serviceMapper = Mappers.getMapper(ServiceMapper.class)
    }

    def "TokenVo로 변경 확인"() {
        given:
        SpreadDto inDto = SpreadDto.builder()
                .userId("USER-ID")
                .roomId(3).build()
        LocalDateTime createAt = LocalDateTime.now()
        when:
        TokenVo tokenVo = serviceMapper.toTokenVo(inDto, createAt)
        then:
        tokenVo.userId == inDto.userId
        tokenVo.roomId == inDto.roomId
        tokenVo.createAt == createAt
    }

    def "TokenVo로 변경 NULL 확인"() {
        when:
        TokenVo tokenVo = serviceMapper.toTokenVo(null, null)
        then:
        tokenVo == null
    }

    def "SearchResultDto로 변경 확인"() {
        given:
        SpreadEntity spread = new SpreadEntity(SpreadDto.builder().roomId(3).userId("USER-ID").build(), "9Aa", LocalDateTime.now())
        SpreadDetailEntity spreadDetail = new SpreadDetailEntity(2, 1, 1000)
        int completeAmount = 10000
        when:
        SearchResultDto searchResultDto = serviceMapper.toSearchResultDto(spread, Lists.newArrayList(spreadDetail), completeAmount)
        then:
        searchResultDto.completeAmount == completeAmount
        searchResultDto.spreadAt == spread.createAt
        searchResultDto.spreadAmount == spread.amount
        searchResultDto.receives.size() == 1

    }

    def "SearchResultDto로 변경 NULL 확인"() {
        when:
        SearchResultDto searchResultDto = serviceMapper.toSearchResultDto(null, null, 0)
        then:
        searchResultDto == null
    }

    def "ReceiveInfoDto로 변경 확인"() {
        given:
        SpreadDetailEntity spreadDetail = new SpreadDetailEntity(2, 1, 1000)
        when:
        SearchResultDto.ReceiveInfoDto receiveInfoDto = serviceMapper.toReceiveInfoDto(spreadDetail)
        then:
        receiveInfoDto.userId == spreadDetail.memberId
        receiveInfoDto.amount == spreadDetail.amount
    }

    def "ReceiveInfoDto로 변경 NULL 확인"() {
        when:
        SearchResultDto.ReceiveInfoDto receiveInfoDto = serviceMapper.toReceiveInfoDto(null)
        then:
        receiveInfoDto == null
    }

    def "ReceiveInfoDto 목록으로 변경 확인"() {
        given:
        SpreadDetailEntity spreadDetail = new SpreadDetailEntity(2, 1, 1000)
        when:
        List<SearchResultDto.ReceiveInfoDto> receiveInfoDtos = serviceMapper.toReceiveInfoDtos(Lists.newArrayList(spreadDetail))
        then:
        receiveInfoDtos.size() == 1
    }

    def "ReceiveInfoDto 목록으로 변경 NULL 확인"() {
        when:
        List<SearchResultDto.ReceiveInfoDto> receiveInfoDtos = serviceMapper.toReceiveInfoDtos(null)
        then:
        receiveInfoDtos == null
    }
}
