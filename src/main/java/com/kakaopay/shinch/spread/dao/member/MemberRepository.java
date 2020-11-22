package com.kakaopay.shinch.spread.dao.member;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<MemberEntity, Integer> {
    Optional<MemberEntity> findByMemberSeq(Integer memberSeq);
    Optional<MemberEntity> findByMemberId(String memberId);
}
