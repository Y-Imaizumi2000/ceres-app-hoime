package com.ceres.hoime.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * ユーザーのメールアドレス認証に使用するトークンを管理するエンティティ。
 *
 * <p>ユーザー登録後に発行される認証トークンを保持し、
 * /auth/verify の認証処理で使用される。</p>
 *
 * <p>1ユーザーにつき1トークンを保持する想定のため、
 * User エンティティとは OneToOne で紐づく。</p>
 */
@Getter
@Setter
@Slf4j
@Entity
public class VerificationToken {

    /** トークンID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 認証用トークン文字列（UUID） */
    private String token;

    /** トークンを発行した対象ユーザー */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** トークンの有効期限（例：24時間） */
    private LocalDateTime expiryDate;
    
    /**
     * JPA が使用するためのデフォルトコンストラクタ。
     *
     * <p>VerificationTokenServiceImpl など、
     * 「User を紐づけずにトークンだけ生成する」ケースでも利用される。</p>
     */
    public VerificationToken() {
        // 必須：JPA がリフレクションでインスタンス化するため
        log.debug("【VerificationToken.defaultConstructor】called");
    }


    /**
     * ユーザーを受け取って新しい認証トークンを作成するコンストラクタ。
     *
     * @param user トークンを紐づけるユーザー
     */
    public VerificationToken(User user) {

        log.debug("【VerificationToken.constructor】start userId={}", 
                user != null ? user.getId() : null);

        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusHours(24);

        log.debug("【VerificationToken.constructor】end token={} expiryDate={}", 
                this.token, this.expiryDate);
    }

    /**
     * トークンが有効期限切れかどうかを判定する。
     *
     * @return true:期限切れ / false:有効
     */
    public boolean isExpired() {

        log.debug("【isExpired】start tokenId={} expiryDate={}", id, expiryDate);

        boolean expired = expiryDate.isBefore(LocalDateTime.now());

        log.debug("【isExpired】end tokenId={} expired={}", id, expired);
        return expired;
    }
}
