package com.kakaopay.shinch.spread.service.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchResultDto {
    private LocalDateTime spreadAt;
    private Integer completeAmount;
    private Integer spreadAmount;
    private List<ReceiveInfoDto> receives;

    @Data
    public static class ReceiveInfoDto {
        private String userId;
        private Integer amount;
    }
}
