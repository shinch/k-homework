package com.kakaopay.shinch.spread.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpreadDto {
    private int personnel;
    private int amount;
    private String userId;
    private int roomId;
}
