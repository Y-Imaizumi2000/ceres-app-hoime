package com.ceres.hoime.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Spring Security 用のユーザー詳細読み出しサービスのインターフェース。
 *
 * <p>ビジネス層命名規則: Service インターフェースは末尾に Service。</p>
 */
public interface CustomUserDetailsService extends UserDetailsService {
}
