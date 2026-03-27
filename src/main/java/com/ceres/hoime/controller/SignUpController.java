package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.service.EmailService;
import com.ceres.hoime.service.UserService;
import com.ceres.hoime.service.VerificationTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * 新規ユーザー登録処理を担当するコントローラー。
 *
 * <p>入力チェック → DB登録 → 認証トークン生成 → メール送信 → 完了画面表示
 * という一連のフローを制御する。</p>
 *
 * <p>ビジネスロジックは Service 層に委譲し、
 * Controller は画面遷移と例外処理のみを担当する。</p>
 */
@Slf4j
@Controller
public class SignUpController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailService emailService;

    /**
     * 新規登録ボタン押下時の処理。
     *
     * @param email            入力メールアドレス
     * @param password         入力パスワード
     * @param confirmPassword  パスワード（確認用）
     * @param nickname         ニックネーム
     * @param model            エラーメッセージ表示用
     * @return 完了画面 or 入力画面
     */
    @PostMapping("/signup")
    public String signupPost(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirm-password") String confirmPassword,
            @RequestParam("nickname") String nickname,
            Model model) {

        // --- method start ---
        log.debug("【signupPost】start email={} nickname={}", email, nickname);

        // --- 1. パスワード一致チェック ---
        if (!password.equals(confirmPassword)) {
            log.debug("【signupPost】password mismatch email={}", email);
            model.addAttribute("error", "パスワードが一致しません。");
            return "HM0002";
        }

        // --- 2. メールアドレスの重複チェック ---
        if (userService.existsByEmail(email)) {
            log.debug("【signupPost】email already exists email={}", email);
            model.addAttribute("error", "このメールアドレスは既に登録されています。");
            return "HM0002";
        }

        try {
            // --- 3. ユーザー登録 ---
            User user = userService.registerUser(email, password, nickname);
            log.debug("【signupPost】user registered userId={}", user.getId());

            // --- 4. 認証トークン生成 ---
            String token = verificationTokenService.createToken(user);
            log.debug("【signupPost】token created userId={} token={}", user.getId(), token);

            // --- 5. 認証メール送信 ---
            emailService.sendVerificationEmail(user.getEmail(), token);
            log.debug("【signupPost】verification email sent email={}", user.getEmail());

            // --- 6. 完了画面へ ---
            model.addAttribute("registeredEmail", user.getEmail());
            model.addAttribute("registeredNickname", user.getNickname());
            log.debug("【signupPost】end (success)");
            return "HM0003";

        } catch (Exception e) {
            log.error("【signupPost】error email={} message={}", email, e.getMessage(), e);
            model.addAttribute("error", "登録処理中にエラーが発生しました。");
            return "HM0002";
        }
    }
}