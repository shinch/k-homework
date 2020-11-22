package com.kakaopay.shinch.spread.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SplitUtil {
    public List<Integer> randomSplit(int totalAmount, int divisionCnt, int maxRatio) {
        List<Integer> numbers = Lists.newArrayList();
        int useAmount = totalAmount;
        for ( int i = 0 ; i < divisionCnt ; i ++ ) {
            int number =  (int)((Math.random() * 100) % maxRatio) * useAmount / 100;
            useAmount = useAmount - number;
            numbers.add(Integer.valueOf(number));
        }
        int minIdx = 0;
        int minAmount = totalAmount;
        int tailAmount = totalAmount;
        for ( int i = 0 ; i < numbers.size() ; i ++ ) {
            if ( minAmount > numbers.get(i) ) {
                minIdx = i;
                minAmount = numbers.get(i);
            }
            tailAmount = tailAmount - numbers.get(i);
        }
        numbers.set(minIdx, numbers.get(minIdx) + tailAmount);
        Collections.shuffle(numbers);
        return numbers;
    }
}
