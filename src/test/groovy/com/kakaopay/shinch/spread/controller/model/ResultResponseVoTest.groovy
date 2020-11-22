package com.kakaopay.shinch.spread.controller.model

import be.joengenduvel.java.verifiers.ToStringVerifier
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class ResultResponseV1Test extends Specification {
    static class SubDto extends ResultResponseV1{
        @Override
        boolean canEqual(Object obj){
            return false
        }
    }

    Class testClass

    def setup() {
        testClass = ResultResponseV1.class
    }

    def "equalsAndHashCode 확인"() {
        expect:
        EqualsVerifier.forClass(testClass)
                .suppress(Warning.NONFINAL_FIELDS, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withRedefinedSubclass(SubDto.class)
                .withRedefinedSuperclass()
                .verify()
    }

    def "toString 확인"(){
        expect:
        ToStringVerifier.forClass(testClass).containsClassName(new ResultResponseV1())
    }
}
