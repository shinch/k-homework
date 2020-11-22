package com.kakaopay.shinch.spread.dao.room

import be.joengenduvel.java.verifiers.ToStringVerifier
import spock.lang.Specification

class RoomEntityTest extends Specification {
    Class testClass

    def setup() {
        testClass = RoomEntity.class
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new RoomEntity())
    }
}
