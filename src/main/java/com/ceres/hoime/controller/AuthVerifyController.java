package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.entity.VerificationToken;
import com.ceres.hoime.service.UserService;
import com.ceres.hoime.service.VerificationTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * メール認証リンクからアクセスされるコントローラー。
 *
 * <p>ユーザー登録時に送信された認証メールのリンクを処理し、
 * トークンの検証・ユーザーの有効化を行う。</p>
 */
@Slf4j
@Controller
public class AuthVerifyController {

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private UserService userService;

    /**
     * メール認証リンク（/auth/verify?token=xxxx）を処理する。
     *
     * @param token 認証トークン
     * @param model 画面表示用モデル
     * @return 認証成功画面（HM0004）またはエラー画面（HM0004-ERR）
     */
    @GetMapping("/auth/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {

        // --- method start ---
        log.debug("【verifyEmail】start token={}", token);

        // トークン取得
        VerificationToken verificationToken = tokenService.findByToken(token);

        // トークンが存在しない場合
        if (verificationToken == null) {
            log.warn("【verifyEmail】invalid token token={}", token);
            model.addAttribute("message", "認証リンクが無効です。");
            return "HM0004-ERR";
        }

        // 有効期限チェック
        if (verificationToken.isExpired()) {
            log.warn("【verifyEmail】token expired token={}", token);
            model.addAttribute("message", "認証リンクの有効期限が切れています。");
            return "HM0004-ERR";
        }

        // トークンに紐づくユーザー取得
        User user = verificationToken.getUser();

        // すでに認証済みの場合
        if (user.isVerified()) {
            log.info("【verifyEmail】already verified user={}", user.getEmail());
            return "HM0004"; // 認証済みでも成功画面へ
        }

        // ユーザーを認証済みに更新
        log.debug("【verifyEmail】verifying user={}", user.getEmail());
        userService.verifyUser(user);

        // トークン削除（再利用防止）
        log.debug("【verifyEmail】deleting token token={}", token);
        tokenService.delete(verificationToken);

        log.debug("【verifyEmail】success user={}", user.getEmail());

        // --- method end ---
        log.debug("【verifyEmail】end");

        return "HM0004"; // 認証成功画面
    }
}
