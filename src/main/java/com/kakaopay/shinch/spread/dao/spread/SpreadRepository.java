package com.kakaopay.shinch.spread.dao.spread;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SpreadRepository extends CrudRepository<SpreadEntity, Integer> {
    Optional<SpreadEntity> findByRoomSeqAndToken(Integer roomSeq, String token);
    List<SpreadEntity> findByMemberIdAndToken(String memberId, String token);
}
