package com.ceres.hoime.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.entity.VerificationToken;
import com.ceres.hoime.repository.UserRepository;
import com.ceres.hoime.repository.VerificationTokenRepository;
import com.ceres.hoime.service.EmailService;
import com.ceres.hoime.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * ユーザー情報を扱うサービス実装クラス。
 * 
 * - 新規登録
 * - メールアドレス重複チェック
 * - ログイン用検索
 * - 権限変更
 * - ステータス変更
 * - 退会処理
 * 
 * DB とのやり取りは UserRepository を通じて行う。
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;


    /**
     * 新規ユーザー登録処理。
     *
         * @param email    メールアドレス
     * @param password パスワード（平文）
     * @param nickname ニックネーム
     */
    @Override
    public User registerUser(String email, String password, String nickname) {

        log.debug("【registerUser】email={}, nickname={}", email, nickname);

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);

        // 入会年月日（YYYYMMDD）
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        user.setJoinYmd(today);

        // 初期値
        user.setRole("FREE");
        user.setStatus("ACTIVE");

        log.debug("【registerUser】User 保存前: {}", user);

        User response = new User();
        response = userRepository.save(user);

        log.debug("【registerUser】User 保存完了: id={}", user.getId());
        
        return response;
    }

    /**
     * メールアドレスの存在チェック。
     *
     * @param email メールアドレス
     * @return true: 存在する / false: 存在しない
     */
    @Override
    public boolean existsByEmail(String email) {
        log.debug("【existsByEmail】email={}", email);
        boolean exists = userRepository.findByEmail(email).isPresent();
        log.debug("【existsByEmail】結果={}", exists);
        return exists;
    }

    /**
     * メールアドレスでユーザーを検索。
     *
     * @param email メールアドレス
     * @return User or null
     */
    @Override
    public User findByEmail(String email) {
        log.debug("【findByEmail】email={}", email);
        return userRepository.findByEmail(email).orElseThrow();
    }

    /**
     * ユーザーの権限を更新する。
     *
     * @param userId ユーザーID
     * @param role   新しい権限
     */
    @Override
    public void updateRole(int userId, String role) {
        log.debug("【updateRole】userId={}, role={}", userId, role);

        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(role);

        userRepository.save(user);

        log.debug("【updateRole】更新完了");
    }

    /**
     * ユーザーのステータスを更新する。
     *
     * @param userId ユーザーID
     * @param status 新しいステータス 	
     */
    @Override
    public void updateStatus(int userId, String status) {
        log.debug("【updateStatus】userId={}, status={}", userId, status);

        User user = userRepository.findById(userId).orElseThrow();
        user.setStatus(status);

        userRepository.save(user);

        log.debug("【updateStatus】更新完了");
    }

    /**
     * ユーザーを退会状態にする。
     *
     * @param userId ユーザーID
     */
    @Override
    public void withdrawUser(int userId) {
        log.debug("【withdrawUser】userId={}", userId);

        User user = userRepository.findById(userId).orElseThrow();

        // 退会日セット（YYYYMMDD）
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        user.setLeaveYmd(today);

        // ステータス変更
        user.setStatus("WITHDRAWN");

        userRepository.save(user);

        log.debug("【withdrawUser】退会処理完了");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyUser(User user) {

        // --- method start ---
        log.debug("【verifyUser】start user={}", user.getEmail());

        // 認証済みに更新
        user.setVerified(true);
        user.setStatus("ACTIVE");

        // DB 保存
        userRepository.save(user);

        // --- method end ---
        log.debug("【verifyUser】end user={}", user.getEmail());
    }
    
    /**
     * 認証メール再送処理。
     *
     * @param email 認証メールを再送信するユーザーのメールアドレス
     */
    @Override
    public void resendVerificationEmail(String email) {

        log.debug("【UserServiceImpl.resendVerificationEmail】start email={}", email);

        // ユーザー取得（存在しなければ例外）
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("【UserServiceImpl.resendVerificationEmail】user not found email={}", email);
                    return new RuntimeException("ユーザーが存在しません");
                });

        // 古いトークン削除（1ユーザー1トークンのため）
        log.debug("【UserServiceImpl.resendVerificationEmail】delete old token userId={}", user.getId());
        tokenRepository.deleteByUser(user);

        // 新しいトークン作成
        VerificationToken newToken = createVerificationToken(user);
        log.debug("【UserServiceImpl.resendVerificationEmail】token created token={}", newToken.getToken());

        // 認証メール送信（EmailService に委譲）
        try {
            emailService.sendVerificationEmail(user.getEmail(), newToken.getToken());
            log.debug("【UserServiceImpl.resendVerificationEmail】email sent email={}", user.getEmail());
        } catch (Exception e) {
            log.error("【UserServiceImpl.resendVerificationEmail】send error email={} message={}",
                    user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("認証メールの送信に失敗しました");
        }

        log.debug("【UserServiceImpl.resendVerificationEmail】end email={}", email);
    }

    /**
     * 認証トークンを新規作成する内部メソッド。
     *
     * @param user トークンを作成するユーザー
     * @return 新規作成された認証トークン
     */
    private VerificationToken createVerificationToken(User user) {
        VerificationToken token = new VerificationToken(user);
        return tokenRepository.save(token);
    }



}
