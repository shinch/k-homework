package com.kakaopay.shinch.spread.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo {
    private String userId;
    private LocalDateTime createAt;
    private int roomId;
}
