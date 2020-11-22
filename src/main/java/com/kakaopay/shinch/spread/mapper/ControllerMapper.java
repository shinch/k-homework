package com.kakaopay.shinch.spread.mapper;

import com.kakaopay.shinch.spread.controller.model.ResultResponseV1;
import com.kakaopay.shinch.spread.service.model.ReceiveDto;
import com.kakaopay.shinch.spread.service.model.SearchDto;
import com.kakaopay.shinch.spread.service.model.SearchResultDto;
import com.kakaopay.shinch.spread.service.model.SpreadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ControllerMapper {

    @Mappings({
            @Mapping(target = "personnel", source = "personnel"),
            @Mapping(target = "amount",    source = "amount"),
            @Mapping(target = "userId",    source = "userId"),
            @Mapping(target = "roomId",    source = "roomId")
    })
    SpreadDto toSpreadDto(Integer personnel, Integer amount, String userId, Integer roomId);

    @Mappings({
            @Mapping(target = "token",     source = "token"),
            @Mapping(target = "userId",    source = "userId"),
            @Mapping(target = "roomId",    source = "roomId")
    })
    ReceiveDto toReceiveDto(String token, String userId, Integer roomId);

    @Mappings({
            @Mapping(target = "token",     source = "token"),
            @Mapping(target = "userId",    source = "userId"),
            @Mapping(target = "roomId",    source = "roomId")
    })
    SearchDto toSearchDto(String token, String userId, Integer roomId);

    @Mappings({
            @Mapping(target = "spreadAt",       source = "spreadAt"),
            @Mapping(target = "completeAmount", source = "completeAmount"),
            @Mapping(target = "spreadAmount",   source = "spreadAmount"),
            @Mapping(target = "receives",       expression = "java( toReceiveInfoVos(inDto.getReceives()) )")
    })
    ResultResponseV1 toResultResponseV1(SearchResultDto inDto);
    List<ResultResponseV1> toResultResponseV1s(List<SearchResultDto> inDto);

    @Mappings({
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "amount", source = "amount")
    })
    ResultResponseV1.ReceiveInfoVo toReceiveInfoVo(SearchResultDto.ReceiveInfoDto inDto);
    List<ResultResponseV1.ReceiveInfoVo> toReceiveInfoVos(List<SearchResultDto.ReceiveInfoDto> inDto);
}
