package com.kakaopay.shinch.spread.service

import com.kakaopay.shinch.spread.dao.room_member.RoomMemberRepository
import com.kakaopay.shinch.spread.dao.spread.SpreadEntity
import com.kakaopay.shinch.spread.dao.spread.SpreadRepository
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailEntity
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailRepository
import com.kakaopay.shinch.spread.exception.NoContentException
import com.kakaopay.shinch.spread.exception.SpreadConditionException
import com.kakaopay.shinch.spread.exception.SpreadConflictException
import com.kakaopay.shinch.spread.mapper.ServiceMapper
import com.kakaopay.shinch.spread.service.model.ReceiveDto
import com.kakaopay.shinch.spread.service.model.SearchDto
import com.kakaopay.shinch.spread.service.model.SearchResultDto
import com.kakaopay.shinch.spread.service.model.SpreadDto
import com.kakaopay.shinch.spread.util.SplitUtil
import com.kakaopay.shinch.spread.util.TokenUtil
import com.kakaopay.shinch.spread.util.TokenVo
import org.assertj.core.util.Lists
import spock.lang.Specification

import java.time.LocalDateTime

class SpreadServiceTest extends Specification {
    SpreadService spreadService
    SplitUtil splitUtil
    TokenUtil tokenUtil
    ServiceMapper serviceMapper
    SpreadRepository spreadRepository
    SpreadDetailRepository spreadDetailRepository
    RoomMemberRepository roomMemberRepository

    def "setup"() {
        splitUtil = Mock(SplitUtil)
        tokenUtil = Mock(TokenUtil)
        serviceMapper = Mock(ServiceMapper)
        spreadRepository = Mock(SpreadRepository)
        spreadDetailRepository = Mock(SpreadDetailRepository)
        roomMemberRepository = Mock(RoomMemberRepository)
        spreadService = new SpreadService(splitUtil, tokenUtil, serviceMapper, spreadRepository, spreadDetailRepository, roomMemberRepository)
    }

    def "뿌리기 성공 확인"() {
        given:
        SpreadDto spreadDto = SpreadDto.builder()
                .personnel(10)
                .amount(100000)
                .userId("USER-ID")
                .roomId(100).build()
        TokenVo tokenVo = TokenVo.builder()
                .userId(spreadDto.getUserId())
                .roomId(spreadDto.getRoomId())
                .createAt(LocalDateTime.now()).build()
        String token = "ABC"
        List<Integer> amounts = Lists.newArrayList(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000)
        when:
        spreadService.spread(spreadDto)
        then:
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(spreadDto.getUserId(), spreadDto.getRoomId()) >> 1
        1 * serviceMapper.toTokenVo(spreadDto, _ as LocalDateTime) >> tokenVo
        1 * tokenUtil.getToken(tokenVo) >> token
        1 * spreadRepository.save(_ as SpreadEntity)
        1 * splitUtil.randomSplit(spreadDto.getAmount(), spreadDto.getPersonnel(), 70) >> amounts
        amounts.size() * spreadDetailRepository.save(_ as SpreadDetailEntity)
    }

    def "방 구성원이 아닐때 뿌리기 실패 확인"() {
        given:
        SpreadDto spreadDto = SpreadDto.builder()
                .personnel(10)
                .amount(100000)
                .userId("USER-ID")
                .roomId(100).build()
        when:
        spreadService.spread(spreadDto)
        then:
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(spreadDto.getUserId(), spreadDto.getRoomId()) >> 0
        thrown(SpreadConditionException.class)
    }

    def "받기 성공 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity spreadEntity = Mock(SpreadEntity)
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(spreadEntity)
        int memberCnt = 1
        int spreadSeq = 10
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        1 * spreadEntity.getMemberId() >> "MEMBER-ID"
        1 * spreadEntity.getExpiryAt() >> LocalDateTime.now().plusDays(1L)
        3 * spreadEntity.getSpreadSeq() >> spreadSeq
        1 * spreadDetailRepository.countBySpreadSeqAndMemberId(spreadSeq, receiveDto.getUserId()) >> 0
        1 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(spreadSeq, receiveDto.getUserId())
        1 * spreadDetailRepository.countBySpreadSeqAndMemberId(spreadSeq, receiveDto.getUserId()) >> 1
    }

    def "뿌린 항목이 모두 소진되어 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity spreadEntity = Mock(SpreadEntity)
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(spreadEntity)
        int memberCnt = 1
        int spreadSeq = 10
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        1 * spreadEntity.getMemberId() >> "MEMBER-ID"
        1 * spreadEntity.getExpiryAt() >> LocalDateTime.now().plusDays(1L)
        3 * spreadEntity.getSpreadSeq() >> spreadSeq
        2 * spreadDetailRepository.countBySpreadSeqAndMemberId(spreadSeq, receiveDto.getUserId()) >> 0
        1 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(spreadSeq, receiveDto.getUserId())
        SpreadConflictException ex = thrown()
        ex.message == "더이상 받을 수 있는 받기가 없습니다."
    }

    def "뿌리기 정보가 없어 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        Optional<SpreadEntity> optionalSpreadEntity = Optional.empty()
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        0 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(_ as int, _ as String)
        SpreadConditionException ex = thrown()
        ex.message == "뿌리기한 내용이 없습니다."
    }

    def "뿌리기한 방의 구성원이 아니여서 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(new SpreadEntity())
        int memberCnt = 0
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        0 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(_ as int, _ as String)
        SpreadConditionException ex = thrown()
        ex.message == "방에 속한 구성원이 아닙니다."
    }

    def "자신이 뿌린 뿌리기 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity spreadEntity = Mock(SpreadEntity)
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(spreadEntity)
        int memberCnt = 1
        int spreadSeq = 10
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        1 * spreadEntity.getMemberId() >> receiveDto.getUserId()
        0 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(_ as int, _ as String)
        SpreadConditionException ex = thrown()
        ex.message == "자신이 뿌리기 한 것은 받을 수 없습니다."
    }

    def "만료된 뿌리기 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity spreadEntity = Mock(SpreadEntity)
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(spreadEntity)
        int memberCnt = 1
        int spreadSeq = 10
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        1 * spreadEntity.getMemberId() >> "MEMBER-ID"
        1 * spreadEntity.getExpiryAt() >> LocalDateTime.now().minusDays(1L)
        0 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(_ as int, _ as String)
        SpreadConflictException ex = thrown()
        ex.message == "받기가 만료된 뿌리기 입니다."
    }

    def "이미 받은 뿌리기 받기 실패 확인"() {
        given:
        ReceiveDto receiveDto = ReceiveDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity spreadEntity = Mock(SpreadEntity)
        Optional<SpreadEntity> optionalSpreadEntity = Optional.of(spreadEntity)
        int memberCnt = 1
        int spreadSeq = 10
        when:
        spreadService.receive(receiveDto)
        then:
        1 * spreadRepository.findByRoomSeqAndToken(receiveDto.getRoomId(), receiveDto.getToken()) >> optionalSpreadEntity
        1 * roomMemberRepository.countByMemberIdAndRoomSeq(receiveDto.getUserId(), receiveDto.getRoomId()) >> memberCnt
        1 * spreadEntity.getMemberId() >> "MEMBER-ID"
        1 * spreadEntity.getExpiryAt() >> LocalDateTime.now().plusDays(1L)
        1 * spreadEntity.getSpreadSeq() >> spreadSeq
        1 * spreadDetailRepository.countBySpreadSeqAndMemberId(spreadSeq, receiveDto.getUserId()) >> 1
        0 * spreadDetailRepository.updateBySpreadSeqAndEmptyUser(_ as int, _ as String)
        SpreadConditionException ex = thrown()
        ex.message == "이미 받은 뿌리기 입니다."
    }


    def "조회 성공 확인"() {
        given:
        SearchDto searchDto = SearchDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity firstSpread = Mock(SpreadEntity)
        SpreadEntity secondSpread = Mock(SpreadEntity)
        SpreadEntity thirdSpread = Mock(SpreadEntity)
        List<SpreadEntity> spreadEntities = Lists.newArrayList(firstSpread, secondSpread, thirdSpread)
        SpreadDetailEntity spreadDetailEntity = Mock(SpreadDetailEntity)
        List<SpreadDetailEntity> spreadDetails = Lists.newArrayList(spreadDetailEntity)
        when:
        List<SearchResultDto> results = spreadService.search(searchDto)
        then:
        1 * spreadRepository.findByMemberIdAndToken(searchDto.getUserId(), searchDto.getToken()) >> spreadEntities
        1 * firstSpread.getCreateAt() >> LocalDateTime.now().minusDays(8L)
        1 * secondSpread.getCreateAt() >> LocalDateTime.now().minusDays(5L)
        1 * thirdSpread.getCreateAt() >> LocalDateTime.now().minusDays(3L)
        1 * secondSpread.getSpreadSeq() >> 100
        1 * thirdSpread.getSpreadSeq() >> 100
        2 * spreadDetailRepository.findBySpreadSeqAndMemberIdIsNotNullOrderByOrderNo(100) >> spreadDetails
        2 * spreadDetailEntity.getAmount() >> 30
        2 * serviceMapper.toSearchResultDto(_ as SpreadEntity, _ as List, 30) >> new SearchResultDto()
        results.size() == 2
    }

    def "뿌리기 된 내역이 없어 조회 실패 확인"() {
        given:
        SearchDto searchDto = SearchDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        List<SpreadEntity> spreadEntities = Lists.emptyList()
        when:
        spreadService.search(searchDto)
        then:
        1 * spreadRepository.findByMemberIdAndToken(searchDto.getUserId(), searchDto.getToken()) >> spreadEntities
        0 * serviceMapper.toSearchResultDto(_ as SpreadEntity, _ as List, _ as int)
        SpreadConditionException ex = thrown()
        ex.message == "ID[USER-ID] 와 TOKEN[ABC]으로 뿌리기된 항목이 없습니다."
    }

    def "7일 이내 뿌리기 된 내역이 없어 조회 결과 없음 확인"() {
        given:
        SearchDto searchDto = SearchDto.builder()
                .token("ABC")
                .userId("USER-ID")
                .roomId(1).build()
        SpreadEntity firstSpread = Mock(SpreadEntity)
        SpreadEntity secondSpread = Mock(SpreadEntity)
        SpreadEntity thirdSpread = Mock(SpreadEntity)
        List<SpreadEntity> spreadEntities = Lists.newArrayList(firstSpread, secondSpread, thirdSpread)
        when:
        spreadService.search(searchDto)
        then:
        1 * spreadRepository.findByMemberIdAndToken(searchDto.getUserId(), searchDto.getToken()) >> spreadEntities
        1 * firstSpread.getCreateAt() >> LocalDateTime.now().minusDays(8L)
        1 * secondSpread.getCreateAt() >> LocalDateTime.now().minusDays(9L)
        1 * thirdSpread.getCreateAt() >> LocalDateTime.now().minusDays(10L)
        0 * serviceMapper.toSearchResultDto(_ as SpreadEntity, _ as List, _ as int)
        NoContentException ex = thrown()
        ex.message == "ID[USER-ID] 와 TOKEN[ABC]으로 7일 이내 뿌리기된 항목이 없습니다."
    }

}
