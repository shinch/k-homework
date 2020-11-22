package com.kakaopay.shinch.spread.dao.member;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER")
@Getter
@ToString
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "M_SEQ", nullable = false)
    private Integer memberSeq;

    @Column(name = "M_ID", length=50, nullable = false)
    private String memberId;

    @Column(name = "M_NAME", length=50, nullable = false)
    private String memberName;
}
