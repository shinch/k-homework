package com.kakaopay.shinch.spread.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenUtil {
    private int NUMBER_MARGIN;
    private int UPPER_MARGIN;
    private int LOWER_MARGIN;
    private int NUMBER_MAX;
    private int UPPER_MAX;
    private int LOWER_MAX;
    private int DEFAULT_ASCII;
    private int CHAR_CNT;
    private int LOOP_MASK;
    private int LAST_MASK;
    private int LOOP_BITS_CNT;
    private int HASH_BITS_CNT;

    public TokenUtil() {
        this.NUMBER_MAX = '9' - '0';
        this.UPPER_MAX = NUMBER_MAX + 1 + 'Z' - 'A';
        this.LOWER_MAX = UPPER_MAX + 1 + 'z' - 'a';

        this.NUMBER_MARGIN = '9' - this.NUMBER_MAX;
        this.UPPER_MARGIN = 'Z' - this.UPPER_MAX;
        this.LOWER_MARGIN = 'z' - this.LOWER_MAX;
        this.DEFAULT_ASCII = '-';
        this.CHAR_CNT = LOWER_MAX + 1;
        this.LOOP_MASK = 0b00000000000000000000011111111111;
        this.LAST_MASK = 0b00000000000000000000001111111111;
        this.HASH_BITS_CNT = Integer.BYTES * 8;
        this.LOOP_BITS_CNT = (int)Math.ceil(HASH_BITS_CNT / 3.0);
    }

    public String getToken(TokenVo inVo) {
        int hash = inVo.hashCode();
        String asciiToken = getTokenChar((hash >> HASH_BITS_CNT - ( LOOP_BITS_CNT * 1 )) & LOOP_MASK)
                + getTokenChar((hash >> HASH_BITS_CNT - ( LOOP_BITS_CNT * 2 )) & LOOP_MASK)
                + getTokenChar(hash & LAST_MASK);
        return asciiToken;
    }
    public String getTokenChar(int useNumber) {
        int idx = useNumber % CHAR_CNT;
        int ascii = 0;
        if ( idx <= NUMBER_MAX ) {
            ascii = idx + NUMBER_MARGIN;
        } else if ( idx <= UPPER_MAX ) {
            ascii = idx + UPPER_MARGIN;
        } else if ( idx <= LOWER_MAX ) {
            ascii = idx + LOWER_MARGIN;
        } else {
            ascii = DEFAULT_ASCII;
            log.warn("적절 하지 않은 순번[{}] 입니다.", idx);
        }
        return Character.toString((char) ascii);
    }
}
