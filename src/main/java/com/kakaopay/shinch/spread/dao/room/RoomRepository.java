package com.kakaopay.shinch.spread.dao.room;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<RoomEntity, Integer> {
    Optional<RoomEntity> findByRoomSeq(Integer roomSeq);
    Optional<RoomEntity> findByRoomId(Integer roomId);
}
