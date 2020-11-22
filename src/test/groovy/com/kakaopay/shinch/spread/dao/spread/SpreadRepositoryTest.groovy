package com.kakaopay.shinch.spread.dao.spread

import com.kakaopay.shinch.spread.service.model.SpreadDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest
class SpreadRepositoryTest extends Specification {
    @Autowired
    SpreadRepository spreadRepository
    SpreadEntity spreadEntity
    SpreadDto spreadDto
    String token

    def "setup"() {
        spreadDto = SpreadDto.builder()
                .personnel(10)
                .amount(20000)
                .userId("USER-ID")
                .roomId(100)
                .build()

        token = "ABC"
        LocalDateTime createAt = LocalDateTime.now()
        spreadEntity = new SpreadEntity(spreadDto, token, createAt)
        spreadRepository.save(spreadEntity)
    }

    def "cleanup"() {
        spreadRepository.delete(spreadEntity)
    }

    def "뿌리기 생성 결과 뿌리기 생성자 기준 조회 확인"() {
        when:
        List<SpreadEntity> spreadEntities = spreadRepository.findByMemberIdAndToken(spreadDto.getUserId(), token)
        then:
        spreadEntities.size() == 1
    }

    def "뿌리기 생성 결과 방번호 기준 조회 확인"() {
        when:
        Optional<SpreadEntity> spreadEntities = spreadRepository.findByRoomSeqAndToken(spreadDto.getRoomId(), token)
        then:
        spreadEntities.isPresent()
    }
}
