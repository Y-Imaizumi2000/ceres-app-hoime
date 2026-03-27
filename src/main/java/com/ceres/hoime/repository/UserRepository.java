package com.ceres.hoime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceres.hoime.entity.User;

/**
 * ユーザー情報を管理するための Repository。
 *
 * <p>User エンティティに対する CRUD 操作を提供し、
 * メールアドレスをキーとした検索メソッドを定義する。</p>
 *
 * <p>ログイン処理やメールアドレス重複チェックなど、
 * ユーザー認証に関わる主要な処理で使用される。</p>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * メールアドレスを元にユーザーを検索する。
     *
     * @param email 検索対象メールアドレス
     * @return 該当ユーザー（存在しない場合は null）
     */
	Optional<User> findByEmail(String email);
} 