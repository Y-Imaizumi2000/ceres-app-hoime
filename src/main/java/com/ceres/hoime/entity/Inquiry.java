package com.ceres.hoime.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * 問い合わせ履歴
 */
@Getter
@Setter
@Entity
@Table(name = "t_inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "inquiry_type", nullable = false, length = 20)
    private String inquiryType;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Version
    @Column(name = "upd_cntr", nullable = false)
    private Integer updCntr;

    @Column(name = "upd_timestamp", nullable = false)
    private LocalDateTime updTimestamp;

    @Column(name = "reg_timestamp", nullable = false)
    private LocalDateTime regTimestamp;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.regTimestamp = now;
        this.updTimestamp = now;
        this.updCntr = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updTimestamp = LocalDateTime.now();
    }
}
