package com.ceres.hoime.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * パスワードリセット用のトークンを管理するエンティティ。
 *
 * <p>/auth/password/reset のパスワード再設定フローで使用される。</p>
 * <p>1ユーザーにつき複数回の再設定要求が発生しうるため ManyToOne で紐づける。</p>
 */
@Getter
@Setter
@Slf4j
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    /** トークンID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** リセット用トークン文字列（UUID） */
    private String token;

    /** トークンを発行した対象ユーザー */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** トークンの有効期限（1時間） */
    private LocalDateTime expiryDate;

    /** 使用済みフラグ */
    private boolean used;

    public PasswordResetToken() {
        log.debug("【PasswordResetToken.defaultConstructor】called");
    }

    public PasswordResetToken(User user, String token) {
        log.debug("【PasswordResetToken.constructor】start userId={}", user != null ? user.getId() : null);
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(1);
        this.used = false;
        log.debug("【PasswordResetToken.constructor】end token={} expiryDate={}", this.token, this.expiryDate);
    }

    /**
     * トークンが有効期限切れかどうかを判定する。
     *
     * @return true: 有効期限切れ
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
