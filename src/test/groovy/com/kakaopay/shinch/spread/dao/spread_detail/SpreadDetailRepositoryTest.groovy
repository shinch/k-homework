package com.kakaopay.shinch.spread.dao.spread_detail

import org.assertj.core.util.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringBootTest
class SpreadDetailRepositoryTest extends Specification {
    @Autowired
    SpreadDetailRepository spreadDetailRepository
    List<SpreadDetailEntity> spreadDetailEntities
    int spreadSeq

    def "setup"() {
        spreadSeq = 100
        spreadDetailEntities = Lists.newArrayList(
                new SpreadDetailEntity( spreadSeq, 1, 1000 ),
                new SpreadDetailEntity( spreadSeq, 2, 2000 ),
                new SpreadDetailEntity( spreadSeq, 3, 3000 ),
                new SpreadDetailEntity( spreadSeq, 4, 4000 ),
                new SpreadDetailEntity( spreadSeq, 5, 5000 ),
                new SpreadDetailEntity( spreadSeq, 6, 6000 ),
                new SpreadDetailEntity( spreadSeq, 7, 7000 )
        )
        spreadDetailRepository.saveAll(spreadDetailEntities)
    }

    def "cleanup"() {
        spreadDetailRepository.deleteAll(spreadDetailEntities)
    }

    @Transactional
    def "할당된 뿌리기 목록 확인"() {
        given:
        String userId = "USER-ID"
        when:
        spreadDetailRepository.updateBySpreadSeqAndEmptyUser(spreadSeq, userId)
        List<SpreadDetailEntity> resultEntities = spreadDetailRepository.findBySpreadSeqAndMemberIdIsNotNullOrderByOrderNo(spreadSeq)
        then:
        resultEntities.size() == 1
    }

    @Transactional
    def "뿌리기 할당 후 결과 확인"() {
        given:
        String userId = "USER-ID"
        when:
        spreadDetailRepository.updateBySpreadSeqAndEmptyUser(spreadSeq, userId)
        int result = spreadDetailRepository.countBySpreadSeqAndMemberId(spreadSeq, userId)
        then:
        result == 1
    }
}
