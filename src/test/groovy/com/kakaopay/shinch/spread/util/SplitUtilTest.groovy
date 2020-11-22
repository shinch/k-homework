package com.kakaopay.shinch.spread.util

import spock.lang.Specification

class SplitUtilTest extends Specification {
    SplitUtil splitUtil

    def "setup"() {
        splitUtil = new SplitUtil();
    }

    def "숫자 분할 확인"() {
        given:
        int totalAmount = 100
        int divisionCnt = 5
        int maxRatio = 70
        when:
        List<Integer> results = splitUtil.randomSplit(totalAmount, divisionCnt, maxRatio)
        int sum = results.sum()
        then:
        results.size() == divisionCnt
        sum == totalAmount
    }
}
