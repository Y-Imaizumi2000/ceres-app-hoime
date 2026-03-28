package com.ceres.hoime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * アプリ全体の Spring Security 設定クラス。
 * 
 * - 認可設定（どのURLを許可するか）
 * - ログインページの指定
 * - ログアウト設定
 * - CSRF の有効/無効
 * - PasswordEncoder の Bean 定義
 * 
 * Hoime の認証・認可の中心となる設定。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP セキュリティ設定を構築する。
     *
     * @param http HttpSecurity オブジェクト
     * @return SecurityFilterChain セキュリティフィルタチェーン
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 認可設定（どのURLをログインなしでアクセス可能にするか）
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",          // ログイン画面
                    "/signup",         // 新規登録画面
                    "/auth/verify",    // メール認証リンク
                    "/auth/resend",    // 認証メール再送
                    "/reset-password", // パスワードリセット画面
                    "/terms",          // 利用規約
                    "/privacy",        // プライバシーポリシー
                    "/law",            // 特商法
                    "/css/**",         // 静的リソース
                    "/js/**",
                    "/img/**"
                ).permitAll()
                .anyRequest().authenticated() // 上記以外はログイン必須
        )

        // ログイン設定
        .formLogin(form -> form
                .loginPage("/login")          // カスタムログインページ
                .defaultSuccessUrl("/home", true) // ログイン成功後の遷移先
                .permitAll()
        )

        // ログアウト設定
        .logout(logout -> logout.permitAll())

        // CSRF 無効化（API や簡易開発時に使用）
        .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * パスワードをハッシュ化するエンコーダーを提供する。
     * 
     * Spring Security がユーザーのパスワードを保存・照合する際に使用。
     *
     * @return BCryptPasswordEncoder のインスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

