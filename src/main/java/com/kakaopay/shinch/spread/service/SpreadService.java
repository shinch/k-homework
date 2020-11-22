package com.kakaopay.shinch.spread.service;

import com.google.common.collect.Lists;
import com.kakaopay.shinch.spread.dao.room_member.RoomMemberRepository;
import com.kakaopay.shinch.spread.dao.spread.SpreadEntity;
import com.kakaopay.shinch.spread.dao.spread.SpreadRepository;
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailEntity;
import com.kakaopay.shinch.spread.dao.spread_detail.SpreadDetailRepository;
import com.kakaopay.shinch.spread.exception.NoContentException;
import com.kakaopay.shinch.spread.exception.SpreadConditionException;
import com.kakaopay.shinch.spread.exception.SpreadConflictException;
import com.kakaopay.shinch.spread.mapper.ServiceMapper;
import com.kakaopay.shinch.spread.service.model.ReceiveDto;
import com.kakaopay.shinch.spread.service.model.SearchDto;
import com.kakaopay.shinch.spread.service.model.SearchResultDto;
import com.kakaopay.shinch.spread.service.model.SpreadDto;
import com.kakaopay.shinch.spread.util.SplitUtil;
import com.kakaopay.shinch.spread.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpreadService {
    private final SplitUtil splitUtil;
    private final TokenUtil tokenUtil;
    private final ServiceMapper serviceMapper;
    private final SpreadRepository spreadRepository;
    private final SpreadDetailRepository spreadDetailRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Autowired
    public SpreadService(SplitUtil splitUtil, TokenUtil tokenUtil, ServiceMapper serviceMapper, SpreadRepository spreadRepository, SpreadDetailRepository spreadDetailRepository, RoomMemberRepository roomMemberRepository) {
        this.splitUtil = splitUtil;
        this.tokenUtil = tokenUtil;
        this.serviceMapper = serviceMapper;
        this.spreadRepository = spreadRepository;
        this.spreadDetailRepository = spreadDetailRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Transactional
    public String spread(SpreadDto inDto) {
        int memberCnt = roomMemberRepository.countByMemberIdAndRoomSeq(inDto.getUserId(), inDto.getRoomId());
        if ( memberCnt == 0 ) {
            throw new SpreadConditionException("방에 속한 구성원이 아닙니다.");
        }
        LocalDateTime spreadAt = LocalDateTime.now();
        String token = tokenUtil.getToken(serviceMapper.toTokenVo(inDto, spreadAt));
        SpreadEntity spreadEntity = new SpreadEntity(inDto, token, spreadAt);
        spreadRepository.save(spreadEntity);
        List<Integer> splitAmounts = splitUtil.randomSplit(inDto.getAmount(), inDto.getPersonnel(), 70);
        for ( int i = 0 ; i < splitAmounts.size() ; i ++ ) {
            SpreadDetailEntity spreadDetailEntity = new SpreadDetailEntity( spreadEntity.getSpreadSeq(), i, splitAmounts.get(i) );
            spreadDetailRepository.save(spreadDetailEntity);
        }
        return token;
    }

    public void receive(ReceiveDto inDto) {
        Optional<SpreadEntity> optionalSpreadEntity = spreadRepository.findByRoomSeqAndToken(inDto.getRoomId(), inDto.getToken());
        if ( optionalSpreadEntity.isPresent() ) {
            SpreadEntity spreadEntity = optionalSpreadEntity.get();
            int memberCnt = roomMemberRepository.countByMemberIdAndRoomSeq(inDto.getUserId(), inDto.getRoomId());
            if ( memberCnt == 0 ) {
                throw new SpreadConditionException("방에 속한 구성원이 아닙니다.");
            }
            if ( spreadEntity.getMemberId().equals(inDto.getUserId()) ) {
                throw new SpreadConditionException("자신이 뿌리기 한 것은 받을 수 없습니다.");
            }
            if ( LocalDateTime.now().isAfter(spreadEntity.getExpiryAt()) ) {
                throw new SpreadConflictException("받기가 만료된 뿌리기 입니다.");
            }
            int receiveCnt = spreadDetailRepository.countBySpreadSeqAndMemberId(spreadEntity.getSpreadSeq(), inDto.getUserId());
            if ( receiveCnt == 0 ) {
                spreadDetailRepository.updateBySpreadSeqAndEmptyUser(spreadEntity.getSpreadSeq(), inDto.getUserId());
                int updateCnt = spreadDetailRepository.countBySpreadSeqAndMemberId(spreadEntity.getSpreadSeq(), inDto.getUserId());
                if ( updateCnt == 0 ) {
                    throw new SpreadConflictException("더이상 받을 수 있는 받기가 없습니다.");
                }
            } else {
                throw new SpreadConditionException("이미 받은 뿌리기 입니다.");
            }
        } else {
            throw new SpreadConditionException("뿌리기한 내용이 없습니다.");
        }
    }

    public List<SearchResultDto> search(SearchDto inDto) {
        List<SearchResultDto> searchResults = Lists.newArrayList();
        LocalDateTime minAt = LocalDateTime.of(LocalDate.now().minusDays(7L), LocalTime.MIN);
        List<SpreadEntity> spreadEntities = spreadRepository.findByMemberIdAndToken(inDto.getUserId(), inDto.getToken());
        List<SpreadEntity> resultSpreads = Lists.newArrayList();
        if ( spreadEntities.size() == 0 ) {
            throw new SpreadConditionException(String.format("ID[%s] 와 TOKEN[%s]으로 뿌리기된 항목이 없습니다.", inDto.getUserId(), inDto.getToken()));
        } else {
            for ( SpreadEntity spreadEntity : spreadEntities ) {
                if ( spreadEntity.getCreateAt().isAfter(minAt) ) {
                    resultSpreads.add(spreadEntity);
                }
            }
        }
        if ( resultSpreads.size() == 0 ) {
            throw new NoContentException(String.format("ID[%s] 와 TOKEN[%s]으로 7일 이내 뿌리기된 항목이 없습니다.", inDto.getUserId(), inDto.getToken()));
        } else {
            for ( SpreadEntity spreadEntity : resultSpreads ) {
                List<SpreadDetailEntity> spreadDetails = spreadDetailRepository.findBySpreadSeqAndMemberIdIsNotNullOrderByOrderNo(spreadEntity.getSpreadSeq());
                int sumAmount = 0;
                for ( SpreadDetailEntity spreadDetail : spreadDetails ) {
                    sumAmount = sumAmount + spreadDetail.getAmount();
                }
                SearchResultDto searchResultDto = serviceMapper.toSearchResultDto(spreadEntity, spreadDetails, sumAmount);
                searchResults.add(searchResultDto);
            }
        }
        return searchResults;
    }
}
