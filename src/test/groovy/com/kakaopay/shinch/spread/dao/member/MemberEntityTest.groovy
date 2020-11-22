package com.kakaopay.shinch.spread.dao.member

import be.joengenduvel.java.verifiers.ToStringVerifier
import spock.lang.Specification

class MemberEntityTest extends Specification {
    Class testClass

    def setup() {
        testClass = MemberEntity.class
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new MemberEntity())
    }

}
