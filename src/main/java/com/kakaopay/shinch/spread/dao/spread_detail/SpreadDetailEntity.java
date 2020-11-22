package com.kakaopay.shinch.spread.dao.spread_detail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "SPREAD_DETAIL")
@Getter
@ToString
@NoArgsConstructor
public class SpreadDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SD_SEQ", nullable = false)
    private Integer sdSeq;

    @Column(name = "S_SEQ", nullable = false)
    private Integer spreadSeq;

    @Column(name = "ORDER_NO", nullable = false)
    private Integer orderNo;

    @Column(name = "M_ID", length=50)
    private String memberId;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    public SpreadDetailEntity( Integer spreadSeq, Integer orderNo, Integer amount ) {
        this.spreadSeq = spreadSeq;
        this.orderNo = orderNo;
        this.amount = amount;
    }
}
