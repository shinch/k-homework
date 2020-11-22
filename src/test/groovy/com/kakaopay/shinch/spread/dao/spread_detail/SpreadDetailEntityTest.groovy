package com.kakaopay.shinch.spread.dao.spread_detail

import be.joengenduvel.java.verifiers.ToStringVerifier
import spock.lang.Specification

class SpreadDetailEntityTest extends Specification {
    Class testClass

    def setup() {
        testClass = SpreadDetailEntity.class
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new SpreadDetailEntity())
    }
}
