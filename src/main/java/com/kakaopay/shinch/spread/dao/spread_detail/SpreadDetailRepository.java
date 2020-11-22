package com.kakaopay.shinch.spread.dao.spread_detail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SpreadDetailRepository extends CrudRepository<SpreadDetailEntity, Integer> {
    @Transactional
    @Modifying
    @Query(value="UPDATE SPREAD_DETAIL SET M_ID = :userId WHERE S_SEQ = :spreadSeq AND M_ID IS NULL ORDER BY ORDER_NO LIMIT 1", nativeQuery=true)
    void updateBySpreadSeqAndEmptyUser(@Param("spreadSeq") Integer spreadSeq, @Param("userId") String userId);
    int countBySpreadSeqAndMemberId(Integer spreadSeq, String memberId);
    List<SpreadDetailEntity> findBySpreadSeqAndMemberIdIsNotNullOrderByOrderNo(Integer spreadSeq);
}
