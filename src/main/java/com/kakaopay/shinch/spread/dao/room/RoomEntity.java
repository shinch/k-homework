package com.kakaopay.shinch.spread.dao.room;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "ROOM")
@Getter
@ToString
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "R_SEQ", nullable = false)
    private Integer roomSeq;

    @Column(name = "R_ID", length=50, nullable = false)
    private String roomId;
}
