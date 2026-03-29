package com.ceres.hoime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ceres.hoime.service.EmailService;

import lombok.extern.slf4j.Slf4j;

/**
 * メール送信処理を実装するサービスクラス。
 *
 * <p>ユーザー登録時のメールアドレス認証メールを送信する。</p>
 *
 * <p>JavaMailSender を利用して SMTP 経由でメールを送信する。</p>
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.domain}")
    private String domain;


    /**
     * 認証メールを送信する。
     *
     * @param to    送信先メールアドレス
     * @param token 認証トークン
     */
    @Override
    public void sendVerificationEmail(String to, String token) {

        // --- method start ---
        log.debug("【sendVerificationEmail】start to={} token={}", to, token);

        try {
            String subject = "【Hoime】メールアドレス認証のお願い";
            String url = domain + "/auth/verify?token=" + token;

            String text = """
                    Hoimeをご利用いただきありがとうございます。

                    以下のリンクをクリックして、メールアドレスを認証してください。

                    認証リンク：
                    %s

                    ※このメールに心当たりがない場合は破棄してください。
                    """.formatted(url);

            // メール作成
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            // メール送信
            mailSender.send(message);

            log.debug("【sendVerificationEmail】mail sent to={}", to);

        } catch (Exception e) {
            log.error("【sendVerificationEmail】error to={} message={}", to, e.getMessage(), e);
            throw e; // Controller 側でキャッチされる
        }

        // --- method end ---
        log.debug("【sendVerificationEmail】end");
    }

    /**
     * パスワードリセットメールを送信する。
     *
     * @param to    送信先メールアドレス
     * @param token リセットトークン
     */
    @Override
    public void sendPasswordResetEmail(String to, String token) {

        log.debug("【sendPasswordResetEmail】start to={} token={}", to, token);

        try {
            String subject = "【Hoime】パスワード再設定のご案内";
            String url = domain + "/auth/password/reset/confirm?token=" + token;

            String text = """
                    Hoimeをご利用いただきありがとうございます。

                    以下のリンクからパスワードを再設定してください。
                    リンクの有効期限は1時間です。

                    パスワード再設定リンク：
                    %s

                    ※このメールに心当たりのない場合は破棄してください。
                    """.formatted(url);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.debug("【sendPasswordResetEmail】mail sent to={}", to);

        } catch (Exception e) {
            log.error("【sendPasswordResetEmail】error to={} message={}", to, e.getMessage(), e);
            throw e;
        }

        log.debug("【sendPasswordResetEmail】end");
    }
}