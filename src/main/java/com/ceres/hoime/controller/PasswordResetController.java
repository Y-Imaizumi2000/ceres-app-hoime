package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ceres.hoime.entity.PasswordResetToken;
import com.ceres.hoime.service.PasswordResetService;

import lombok.extern.slf4j.Slf4j;

/**
 * パスワードリセット（HM0005）を担当するコントローラー。
 *
 * <p>フロー：
 * <ol>
 *   <li>GET  /auth/password/reset           → メールアドレス入力画面表示</li>
 *   <li>POST /auth/password/reset           → リセットメール送信</li>
 *   <li>GET  /auth/password/reset/confirm   → 新パスワード入力画面表示</li>
 *   <li>POST /auth/password/reset/confirm   → パスワード更新 → ログイン画面へ</li>
 * </ol>
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/auth/password/reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // ------------------------------------------------------------------
    // Step 1: メールアドレス入力画面
    // ------------------------------------------------------------------

    /**
     * パスワードリセット申請画面を表示する。
     *
     * @param model Model
     * @return HM0005
     */
    @GetMapping
    public String resetRequestForm(Model model) {
        log.debug("【resetRequestForm】GET /auth/password/reset");
        model.addAttribute("step", "request");
        return "HM0005";
    }

    /**
     * パスワードリセットメールを送信する。
     *
     * <p>メールアドレスが存在しない場合も同じ完了メッセージを表示し、
     * ユーザーの存在確認攻撃（User Enumeration）を防ぐ。</p>
     *
     * @param email 入力メールアドレス
     * @param model Model
     * @return HM0005（完了メッセージ付き）
     */
    @PostMapping
    public String resetRequestPost(
            @RequestParam("email") String email,
            Model model) {

        log.debug("【resetRequestPost】start email={}", email);

        try {
            passwordResetService.requestPasswordReset(email);
        } catch (Exception e) {
            // 内部エラーはログのみ。ユーザーには成功と同じ画面を返す（情報漏洩防止）
            log.error("【resetRequestPost】error email={} message={}", email, e.getMessage(), e);
        }

        model.addAttribute("step", "request");
        model.addAttribute("sent", true);
        log.debug("【resetRequestPost】end");
        return "HM0005";
    }

    // ------------------------------------------------------------------
    // Step 2: 新パスワード入力画面
    // ------------------------------------------------------------------

    /**
     * 新パスワード入力画面を表示する。
     *
     * @param token リセットトークン
     * @param model Model
     * @return HM0005（トークン検証OK）またはエラーメッセージ付き HM0005
     */
    @GetMapping("/confirm")
    public String resetConfirmForm(
            @RequestParam("token") String token,
            Model model) {

        log.debug("【resetConfirmForm】start token={}", token);

        PasswordResetToken prt = passwordResetService.findByToken(token);

        if (prt == null || prt.isUsed() || prt.isExpired()) {
            log.warn("【resetConfirmForm】invalid token={}", token);
            model.addAttribute("step", "request");
            model.addAttribute("tokenError", "リンクが無効または有効期限切れです。再度お申し込みください。");
            return "HM0005";
        }

        model.addAttribute("step", "confirm");
        model.addAttribute("token", token);
        log.debug("【resetConfirmForm】end (token valid)");
        return "HM0005";
    }

    /**
     * パスワードを更新してログイン画面へリダイレクトする。
     *
     * @param token           リセットトークン
     * @param newPassword     新パスワード
     * @param confirmPassword 新パスワード（確認用）
     * @param model           Model
     * @return ログイン画面へリダイレクト or エラー付き HM0005
     */
    @PostMapping("/confirm")
    public String resetConfirmPost(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        log.debug("【resetConfirmPost】start token={}", token);

        // パスワード一致チェック
        if (!newPassword.equals(confirmPassword)) {
            log.debug("【resetConfirmPost】password mismatch");
            model.addAttribute("step", "confirm");
            model.addAttribute("token", token);
            model.addAttribute("error", "パスワードが一致しません。");
            return "HM0005";
        }

        // パスワード長チェック（8文字以上）
        if (newPassword.length() < 8) {
            log.debug("【resetConfirmPost】password too short");
            model.addAttribute("step", "confirm");
            model.addAttribute("token", token);
            model.addAttribute("error", "パスワードは8文字以上で入力してください。");
            return "HM0005";
        }

        try {
            passwordResetService.resetPassword(token, newPassword);
            log.debug("【resetConfirmPost】password reset success");
            return "redirect:/login?passwordReset=true";

        } catch (IllegalArgumentException e) {
            log.warn("【resetConfirmPost】token error message={}", e.getMessage());
            model.addAttribute("step", "request");
            model.addAttribute("tokenError", e.getMessage());
            return "HM0005";

        } catch (Exception e) {
            log.error("【resetConfirmPost】unexpected error message={}", e.getMessage(), e);
            model.addAttribute("step", "confirm");
            model.addAttribute("token", token);
            model.addAttribute("error", "エラーが発生しました。しばらく経ってから再度お試しください。");
            return "HM0005";
        }
    }
}
