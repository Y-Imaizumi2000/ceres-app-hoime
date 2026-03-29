package com.ceres.hoime.service;

/**
 * メール送信処理を担当するサービスのインターフェース。
 *
 * <p>主にユーザー登録時の認証メール送信を行う。</p>
 */
public interface EmailService {

    /**
     * メールアドレス認証用のメールを送信する。
     *
     * @param to    送信先メールアドレス
     * @param token 認証トークン
     */
    void sendVerificationEmail(String to, String token);

    /**
     * パスワードリセット用のメールを送信する。
     *
     * @param to    送信先メールアドレス
     * @param token リセットトークン
     */
    void sendPasswordResetEmail(String to, String token);
}
