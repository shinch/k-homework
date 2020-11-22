package com.kakaopay.shinch.spread.util

import spock.lang.Specification

class TokenUtilTest extends Specification {
    TokenUtil tokenUtil

    def "setup"() {
        tokenUtil = new TokenUtil()
    }

    def "token 반환"() {
        given:
        TokenVo tokenVo = Mock(TokenVo)
        // 9 34 59 => 00000001001 00000100010 0000111011 => 9Yx
        int hashValue = 0b00000001001000001000100000111011
        tokenVo.hashCode() >> hashValue
        when:
        String result = tokenUtil.getToken(tokenVo)
        then:
        result.equals("9Yx")
    }

    def "token에 사용되는 문자 반환"() {
        expect:
        result == tokenUtil.getTokenChar(inNum)
        where:
        inNum||result
        0 ||"0"
        9 ||"9"
        10||"A"
        35||"Z"
        36||"a"
        61||"z"
    }
}
