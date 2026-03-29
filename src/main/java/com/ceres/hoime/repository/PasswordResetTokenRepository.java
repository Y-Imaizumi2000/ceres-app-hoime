package com.ceres.hoime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ceres.hoime.entity.PasswordResetToken;
import com.ceres.hoime.entity.User;

/**
 * パスワードリセット用トークンを管理する Repository。
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * トークン文字列から PasswordResetToken を検索する。
     *
     * @param token リセットメールに記載されたトークン文字列
     * @return 該当トークン
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * 指定ユーザーの未使用・未期限切れトークンを全て無効化（使用済みに）する。
     *
     * @param user 対象ユーザー
     */
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user AND t.used = false")
    void invalidateAllByUser(User user);
}
