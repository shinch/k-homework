package com.kakaopay.shinch.spread.dao.spread

import be.joengenduvel.java.verifiers.ToStringVerifier
import spock.lang.Specification

class SpreadEntityTest extends Specification {
    Class testClass

    def setup() {
        testClass = SpreadEntity.class
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new SpreadEntity())
    }
}
