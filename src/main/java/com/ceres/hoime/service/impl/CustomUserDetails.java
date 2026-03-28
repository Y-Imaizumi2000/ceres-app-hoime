package com.ceres.hoime.service.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security における認証済みユーザー情報を保持するクラス。
 *
 * <p>本クラスは {@link UserDetails} を実装し、アプリケーション独自の
 * ユーザー情報（id / email / password / nickname / 権限）を
 * Spring Security が扱える形式に変換する役割を持つ。</p>
 *
 * <p>認証後、SecurityContext に格納され、コントローラやサービス層で
 * 「ログイン中のユーザー情報」を取得する際に利用される。</p>
 *
 * <p>※本クラスは不変（immutable）として設計しており、
 * フィールドはすべて final で定義している。</p>
 */
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** ユーザーID（DB の主キー） */
    private final Integer id;

    /** メールアドレス（ログインIDとして使用） */
    private final String email;

    /** ハッシュ化済みパスワード */
    private final String password;

    /** ニックネーム（画面表示用） */
    private final String nickname;

    /** 権限リスト（ROLE_FREE / ROLE_PREMIUM / ROLE_ADMIN など） */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * コンストラクタ。
     *
     * @param id ユーザーID
     * @param email メールアドレス（ログインID）
     * @param password ハッシュ化済みパスワード
     * @param nickname ニックネーム
     * @param authorities 権限リスト
     */
    public CustomUserDetails(Integer id, String email, String password, String nickname,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    /** @return ユーザーID */
    public Integer getId() {
        return id;
    }

    /** @return ニックネーム */
    public String getNickname() {
        return nickname;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Spring Security が「ユーザー名」として扱う値。
     * <p>本アプリでは email をログインIDとして使用するため email を返す。</p>
     */
    @Override
    public String getUsername() {
        return email;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonExpired() {
        return true; // アカウント有効期限を管理しないため常に true
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonLocked() {
        return true; // ロック機能を使用しないため常に true
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // パスワード有効期限を管理しないため常に true
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return true; // ステータス管理は User エンティティ側で行うため常に true
    }
}

