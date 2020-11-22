package com.kakaopay.shinch.spread.dao.room_member

import be.joengenduvel.java.verifiers.ToStringVerifier
import spock.lang.Specification

class RoomMemberEntityTest extends Specification {
    Class testClass

    def setup() {
        testClass = RoomMemberEntity.class
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new RoomMemberEntity())
    }

    def "builder 확인"() {
        expect:
        RoomMemberEntity.builder().build() != null
    }
}
