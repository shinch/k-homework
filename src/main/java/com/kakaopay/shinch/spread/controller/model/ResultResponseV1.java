package com.kakaopay.shinch.spread.controller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "뿌리기 및 해당 받기 상태 정보를 반환 한다.")
public class ResultResponseV1 {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "뿌리기 등록 일시", example = "2020-11-20T10:20:30")
    private LocalDateTime spreadAt;
    @Schema(description = "받기 완료 된 금액의 총합", example = "5000")
    private Integer completeAmount;
    @Schema(description = "뿌리기 요청 금액", example = "10000")
    private Integer spreadAmount;
    @Schema(description = "받기 완료된 목록")
    private List<ReceiveInfoVo> receives;

    @Data
    public static class ReceiveInfoVo {
        @Schema(description = "받기 사람 ID", example = "RECEIVER-01")
        private String userId;
        @Schema(description = "받은 금액", example = "5000")
        private Integer amount;
    }
}
