package com.kakaopay.shinch.spread.dao.room_member;

import org.springframework.data.repository.CrudRepository;

public interface RoomMemberRepository extends CrudRepository<RoomMemberEntity, Integer> {
    int countByMemberIdAndRoomSeq(String memberId, Integer roomSeq);
}
