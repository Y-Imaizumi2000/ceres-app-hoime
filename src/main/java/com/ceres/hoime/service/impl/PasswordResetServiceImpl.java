package com.ceres.hoime.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ceres.hoime.entity.PasswordResetToken;
import com.ceres.hoime.entity.User;
import com.ceres.hoime.repository.PasswordResetTokenRepository;
import com.ceres.hoime.service.EmailService;
import com.ceres.hoime.service.PasswordResetService;
import com.ceres.hoime.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * パスワードリセットフローを実装するサービスクラス。
 *
 * <p>メール送信時はユーザーの存在有無に関わらず同じレスポンスを返すことで、
 * メールアドレスの存在確認攻撃（User Enumeration）を防止する。</p>
 */
@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void requestPasswordReset(String email) {

        log.debug("【requestPasswordReset】start email={}", email);

        // ユーザーが存在しない場合は何もしない（User Enumeration 防止）
        User user = userService.findByEmail(email);
        if (user == null) {
            log.debug("【requestPasswordReset】user not found (silent) email={}", email);
            return;
        }

        // 既存の未使用トークンを全て無効化
        tokenRepository.invalidateAllByUser(user);
        log.debug("【requestPasswordReset】invalidated old tokens userId={}", user.getId());

        // 新規トークン生成・保存
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(user, rawToken);
        tokenRepository.save(prt);
        log.debug("【requestPasswordReset】token saved userId={} token={}", user.getId(), rawToken);

        // リセットメール送信
        emailService.sendPasswordResetEmail(email, rawToken);
        log.debug("【requestPasswordReset】email sent email={}", email);

        log.debug("【requestPasswordReset】end");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PasswordResetToken findByToken(String token) {
        log.debug("【findByToken】start token={}", token);
        PasswordResetToken prt = tokenRepository.findByToken(token).orElse(null);
        log.debug("【findByToken】end result={}", prt);
        return prt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        log.debug("【resetPassword】start token={}", token);

        PasswordResetToken prt = tokenRepository.findByToken(token).orElse(null);

        if (prt == null) {
            log.warn("【resetPassword】token not found token={}", token);
            throw new IllegalArgumentException("無効なトークンです。");
        }
        if (prt.isUsed()) {
            log.warn("【resetPassword】token already used token={}", token);
            throw new IllegalArgumentException("このリンクは既に使用済みです。");
        }
        if (prt.isExpired()) {
            log.warn("【resetPassword】token expired token={}", token);
            throw new IllegalArgumentException("リンクの有効期限が切れています。再度お申し込みください。");
        }

        // パスワード更新
        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
        log.debug("【resetPassword】password updated userId={}", user.getId());

        // トークンを使用済みにする
        prt.setUsed(true);
        tokenRepository.save(prt);
        log.debug("【resetPassword】token marked as used");

        log.debug("【resetPassword】end");
    }
}
