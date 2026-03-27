package com.ceres.hoime.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * Hoime のユーザー情報を管理するエンティティ。
 *
 * <p>メールアドレス・パスワード・ニックネームなどの基本情報に加え、
 * 会員ステータス、ロール、更新カウンタ、登録日時など
 * アプリ運用に必要な情報を保持する。</p>
 *
 * <p>新規登録時には @PrePersist により
 * regTimestamp / updTimestamp / updCntr が自動セットされる。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "m_user")
public class User {

    /** ユーザーID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** メールアドレス（ログインID） */
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    /** ハッシュ化済みパスワード */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** ニックネーム */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 入会日（YYYYMMDD） */
    @Column(name = "join_ymd", nullable = false, length = 8)
    private String joinYmd;

    /** ロール（FREE / PREMIUM / ADMIN など） */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    /** ステータス（ACTIVE / WITHDRAWN / SUSPENDED） */
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    /** 認証済みフラグ（true:認証済み / false:未認証） */
    @Column(name = "verified", nullable = false)
    private boolean verified;


    /** 更新カウンタ（楽観ロック用） */
    @Version
    @Column(name = "upd_cntr", nullable = false)
    private Integer updCntr;

    /** 最終更新日時 */
    @Column(name = "upd_timestamp", nullable = false)
    private LocalDateTime updTimestamp;

    /** 登録日時 */
    @Column(name = "reg_timestamp", nullable = false)
    private LocalDateTime regTimestamp;

    /** 退会日（YYYYMMDD） */
    @Column(name = "leave_ymd", length = 8)
    private String leaveYmd;

    /**
     * 新規登録時に自動セットされる値。
     * regTimestamp / updTimestamp / updCntr を初期化する。
     */
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.regTimestamp = now;
        this.updTimestamp = now;
        this.updCntr = 0;
    }
}