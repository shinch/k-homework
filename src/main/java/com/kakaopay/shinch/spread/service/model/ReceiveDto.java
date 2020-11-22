package com.kakaopay.shinch.spread.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveDto {
    private String token;
    private String userId;
    private int roomId;
}
