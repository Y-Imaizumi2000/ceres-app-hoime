package com.ceres.hoime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.entity.VerificationToken;

/**
 * メールアドレス認証用トークンを管理するための Repository。
 *
 * <p>VerificationToken エンティティに対する CRUD 操作を提供し、
 * トークン文字列から該当レコードを検索するためのメソッドを定義する。</p>
 *
 * <p>/auth/verify の認証処理や、認証メール再送処理で使用される。</p>
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * トークン文字列から VerificationToken を検索する。
     *
     * @param token 認証メールに記載されたトークン文字列
     * @return 該当する VerificationToken（存在しない場合は Optional.empty()）
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * 指定したユーザーに紐づく認証トークンを削除する。
     *
     * <p>Hoime では「1ユーザーにつき1トークン」の設計のため、
     * 認証メール再送時に古いトークンを削除する用途で使用される。</p>
     *
     * @param user トークンを削除したい対象ユーザー
     */
    void deleteByUser(User user);
}