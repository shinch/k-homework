package com.kakaopay.shinch.spread.dao.room_member;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ROOM_MEMBER")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RM_SEQ", nullable = false)
    private Integer rmSeq;

    @Column(name = "M_ID", length=50, nullable = false)
    private String memberId;

    @Column(name = "R_SEQ", nullable = false)
    private Integer roomSeq;
}
