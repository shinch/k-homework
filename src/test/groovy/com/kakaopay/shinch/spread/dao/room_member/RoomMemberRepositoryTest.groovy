package com.kakaopay.shinch.spread.dao.room_member

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class RoomMemberRepositoryTest extends Specification {

    @Autowired
    RoomMemberRepository roomMemberRepository
    String memberId
    int roomSeq

    def "setup"() {
        memberId = "MEMBER-ID"
        roomSeq = 10
        roomMemberRepository.save(RoomMemberEntity.builder().memberId(memberId).roomSeq(roomSeq).build())
    }

    def "룸번호에 특정 ID가 속해 있는지 확인"() {
        when:
        int memberCnt = roomMemberRepository.countByMemberIdAndRoomSeq(memberId, roomSeq)
        then:
        memberCnt == 1
    }
}
