package com.kakaopay.shinch.spread.mapper;

import com.kakaopay.shinch.spread.dao.spread.SpreadEntity;
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailEntity;
import com.kakaopay.shinch.spread.service.model.SearchResultDto;
import com.kakaopay.shinch.spread.service.model.SpreadDto;
import com.kakaopay.shinch.spread.util.TokenVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    @Mappings({
            @Mapping(target = "userId",   source = "inDto.userId"),
            @Mapping(target = "createAt", source = "createAt"),
            @Mapping(target = "roomId",   source = "inDto.roomId")
    })
    TokenVo toTokenVo(SpreadDto inDto, LocalDateTime createAt);

    @Mappings({
            @Mapping(target = "spreadAt",   source = "spread.createAt"),
            @Mapping(target = "completeAmount", source = "completeAmount"),
            @Mapping(target = "spreadAmount",   source = "spread.amount"),
            @Mapping(target = "receives",       expression = "java( toReceiveInfoDtos(spreadDetails) )")
    })
    SearchResultDto toSearchResultDto(SpreadEntity spread, List<SpreadDetailEntity> spreadDetails, int completeAmount);

    @Mappings({
            @Mapping(target = "userId",   source = "memberId"),
            @Mapping(target = "amount", source = "amount")
    })
    SearchResultDto.ReceiveInfoDto toReceiveInfoDto(SpreadDetailEntity spreadDetail);
    List<SearchResultDto.ReceiveInfoDto> toReceiveInfoDtos(List<SpreadDetailEntity> spreadDetails);

}
