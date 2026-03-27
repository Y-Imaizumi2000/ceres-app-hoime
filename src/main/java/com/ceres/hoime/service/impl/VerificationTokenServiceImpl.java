 package com.ceres.hoime.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.entity.VerificationToken;
import com.ceres.hoime.repository.VerificationTokenRepository;
import com.ceres.hoime.service.VerificationTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * メールアドレス認証用トークンを生成・保存するサービス実装クラス。
 *
 * <p>ユーザー登録後に呼び出され、UUID を元に認証トークンを生成し、
 * DB に保存した上でトークン文字列を返却する。</p>
 *
 * <p>トークンには有効期限（24時間）を設定し、
 * /auth/verify の認証処理で使用される。</p>
 */
@Slf4j
@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    /**
     * 指定されたユーザーに紐づく認証トークンを生成し、DB に保存する。
     *
     * @param user 認証トークンを発行する対象ユーザー
     * @return 生成されたトークン文字列（UUID）
     */
    @Override
    public String createToken(User user) {

        // --- method start ---
        log.debug("【createToken】start userId={}", user.getId());

        // ランダムな UUID をトークンとして生成
        String token = UUID.randomUUID().toString();
        log.debug("【createToken】generatedToken={}", token);

        // トークンエンティティを作成
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(user);

        // トークンの有効期限（24時間後）
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        vt.setExpiryDate(expiry);

        log.debug("【createToken】expiryDate={}", expiry);

        // DB に保存
        tokenRepository.save(vt);
        log.debug("【createToken】token saved userId={} token={}", user.getId(), token);

        // --- method end ---
        log.debug("【createToken】end");

        return token;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VerificationToken findByToken(String token) {

        // --- method start ---
        log.debug("【findByToken】start token={}", token);

        // Optional で返ってくるため、orElse(null) で中身を取り出す
        VerificationToken vt = tokenRepository.findByToken(token).orElse(null);

        log.debug("【findByToken】end result={}", vt);
        return vt;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(VerificationToken token) {

        // --- method start ---
        log.debug("【delete】start tokenId={}", token.getId());

        tokenRepository.delete(token);

        // --- method end ---
        log.debug("【delete】end");
    }

}