package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ceres.hoime.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 認証メール再送処理を担当するコントローラークラス。
 * <p>
 * ユーザーがメール認証リンクを紛失した場合や、
 * 有効期限切れとなった場合に再送を行う。
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthResendController {

    @Autowired
    private UserService userService;

    /**
     * 認証メール再送処理。
     *
     * @param email 再送対象のユーザーのメールアドレス
     * @param model 画面表示用モデル
     * @return 再送成功画面 or エラー画面
     */
    @PostMapping("/resend")
    public String resendVerificationEmail(
            @RequestParam("email") String email,
            Model model) {

        log.debug("【AuthResendController.resendVerificationEmail】start email={}", email);

        try {
            // 認証メール再送処理
            userService.resendVerificationEmail(email);

            // 画面にメールアドレスを渡す
            model.addAttribute("email", email);

            log.debug("【AuthResendController.resendVerificationEmail】end success email={}", email);
            return "HM0003"; // 再送完了画面

        } catch (Exception e) {
            log.error("【AuthResendController.resendVerificationEmail】error email={} message={}",
                    email, e.getMessage(), e);

            model.addAttribute("error", "認証メールの再送に失敗しました。");

            return "HM0003-ERR"; // エラー画面
        }
    }
}

