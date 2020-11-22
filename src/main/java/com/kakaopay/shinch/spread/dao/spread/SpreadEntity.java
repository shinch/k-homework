package com.kakaopay.shinch.spread.dao.spread;

import com.kakaopay.shinch.spread.service.model.SpreadDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SPREAD")
@Getter
@ToString
@NoArgsConstructor
public class SpreadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "S_SEQ", nullable = false)
    private Integer spreadSeq;

    @Column(name = "M_ID", length=50, nullable = false)
    private String memberId;

    @Column(name = "R_SEQ", nullable = false)
    private Integer roomSeq;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @Column(name = "PERSONNEL", nullable = false)
    private Integer personnel;

    @Column(name = "TOKEN", length=50, nullable = false)
    private String token;

    @Column(name = "CREATE_AT", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "EXPIRY_AT", nullable = false)
    private LocalDateTime expiryAt;

    public SpreadEntity(SpreadDto spreadDto, String token, LocalDateTime createAt) {
        this.memberId = spreadDto.getUserId();
        this.roomSeq = spreadDto.getRoomId();
        this.amount = spreadDto.getAmount();
        this.personnel = spreadDto.getPersonnel();
        this.token = token;
        this.createAt = createAt;
        this.expiryAt = createAt.plusMinutes(10L);
    }
}
