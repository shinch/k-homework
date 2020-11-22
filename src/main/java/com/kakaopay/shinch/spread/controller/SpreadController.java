package com.kakaopay.shinch.spread.controller;

import com.kakaopay.shinch.spread.controller.model.ResultResponseV1;
import com.kakaopay.shinch.spread.mapper.ControllerMapper;
import com.kakaopay.shinch.spread.service.SpreadService;
import com.kakaopay.shinch.spread.service.model.ReceiveDto;
import com.kakaopay.shinch.spread.service.model.SearchDto;
import com.kakaopay.shinch.spread.service.model.SearchResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "뿌리기 APIs", description = "/api")
public class SpreadController {
    private final SpreadService spreadService;
    private final ControllerMapper controllerMapper;

    @Autowired
    public SpreadController(SpreadService spreadService, ControllerMapper controllerMapper) {
        this.spreadService = spreadService;
        this.controllerMapper = controllerMapper;
    }

    @Operation(summary = "뿌리기", description = "뿌리기를 실행 한다", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/vnd.kakaopay.api.spread-V1+json", schema = @Schema(implementation = Map.Entry.class), examples = {@ExampleObject(value = "{\"token\": \"3dB\"}")}) ),
            @ApiResponse(responseCode = "204", description = "조회값 없음", content = @Content),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 확인 실패", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "409", description = "이미 뿌리기 완료됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(value = "{\"상세내용\"}")}) ),
            @ApiResponse(responseCode = "500", description = "알수 없는 오류(확인 필요)", content = @Content) })
    @PostMapping(value = "/spread/{personnel}/{amount}", headers = "accept=application/vnd.kakaopay.api.spread-V1+json")
    public Map.Entry<String, String> postSpreadV1(
            @NotNull(message = "해당 인원은 필수 입니다.")
            @Min(value = 1, message = "해당 인원은 최소 1명 이상 입니다.")
            @Parameter(required = true, description = "뿌리기를 받을 최대 인원을 지정 한다.", example = "3")
            @PathVariable("personnel") Integer personnel,
            @NotNull(message = "뿌릴 금액은 필수 입니다.")
            @Min(value = 1, message = "뿌릴 금액은 최소 1원 이상 입니다.")
            @Parameter(required = true, description = "뿌릴기 가능한 최대 금액을 지정 한다.", example = "1000")
            @PathVariable("amount") Integer amount,
            @NotBlank(message = "요청는 필수 입니다.")
            @Parameter(required = true, description = "요청자 ID를 지정 한다.", example = "sender_01")
            @RequestHeader("X-USER-ID") String userId,
            @NotNull(message = "방번호는 필수 입니다.")
            @Min(value = 1, message = "방번호는은 최소 1번 이상 입니다.")
            @Parameter(required = true, description = "대상 방번호를 지정 한다.", example = "10")
            @RequestHeader("X-ROOM-ID") Integer roomId ) {
        String token = spreadService.spread(controllerMapper.toSpreadDto(personnel, amount, userId, roomId));
        return new AbstractMap.SimpleEntry<>("token", token);
    }

    @Operation(summary = "받기", description = "뿌리기한 금액을 받는다.", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content ),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 확인 실패", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "409", description = "받기 할 수 없는 뿌리기 상태", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(value = "{\"상세내용\"}")})),
            @ApiResponse(responseCode = "500", description = "알수 없는 오류(확인 필요)", content = @Content) })
    @PatchMapping(value = "/receive/{token}", headers = "accept=application/vnd.kakaopay.api.spread-V1+json")
    public void patchReceiveV1(
            @NotBlank(message = "Token값은 필수 입니다.")
            @Size(min = 3 , max = 3, message="토큰 길이는 3자입니다.")
            @Parameter(required = true, description = "받기 Token을 지정 한다.", example = "8wG")
            @PathVariable("token") String token,
            @NotBlank(message = "요청자는 필수 입니다.")
            @Parameter(required = true, description = "요청자 ID를 지정 한다.", example = "receiver_01")
            @RequestHeader("X-USER-ID") String userId,
            @NotNull(message = "방번호는 필수 입니다.")
            @Min(value = 1, message = "방번호는은 최소 1번 이상 입니다.")
            @Parameter(required = true, description = "대상 방번호를 지정 한다.", example = "1")
            @RequestHeader("X-ROOM-ID") Integer roomId ) {
        ReceiveDto receiveDto = controllerMapper.toReceiveDto(token, userId, roomId);
        spreadService.receive(receiveDto);
    }

    @Operation(summary = "뿌리기 결과 확인", description = "뿌리기 결과를 확인 한다", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/vnd.kakaopay.api.spread-V1+json", array = @ArraySchema(schema = @Schema(implementation = ResultResponseV1.class))) ),
            @ApiResponse(responseCode = "204", description = "조회값 없음", content = @Content),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 확인 실패", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "500", description = "알수 없는 오류(확인 필요)", content = @Content) })
    @GetMapping(value = "/result/{token}", headers = "accept=application/vnd.kakaopay.api.spread-V1+json")
    public List<ResultResponseV1> getResultV1(
            @NotBlank(message = "Token값은 필수 입니다.")
            @Size(min = 3 , max = 3, message="토큰 길이는 3자입니다.")
            @Parameter(required = true, description = "받기 Token을 지정 한다.", example = "8wG")
            @PathVariable("token") String token,
            @NotBlank(message = "요청자는 필수 입니다.")
            @Parameter(required = true, description = "요청자 ID를 지정 한다.", example = "receiver_01")
            @RequestHeader("X-USER-ID") String userId,
            @NotNull(message = "방번호는 필수 입니다.")
            @Min(value = 1, message = "방번호는은 최소 1번 이상 입니다.")
            @Parameter(required = true, description = "대상 방번호를 지정 한다.", example = "1")
            @RequestHeader("X-ROOM-ID") Integer roomId ) {
        List<SearchResultDto> results = spreadService.search(controllerMapper.toSearchDto(token, userId, roomId));
        return controllerMapper.toResultResponseV1s(results);
    }
}
