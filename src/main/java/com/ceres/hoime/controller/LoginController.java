package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.service.InquiryService;
import com.ceres.hoime.service.UserService;
import com.ceres.hoime.service.impl.CustomUserDetails;

/**
 * ログイン・新規登録・各種静的ページ（利用規約・プライバシーポリシー・特商法）への
 * 画面遷移を担当するコントローラー。
 *
 * <p>画面ID（HM0001 / HM0002 / HM0030 / HM0031 / HM0032）に対応する
 * HTML テンプレートを返却する役割を持つ。</p>
 *
 * <p>ビジネスロジックは一切持たず、単純な画面遷移のみを担当する。</p>
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private InquiryService inquiryService;

    /**
     * ログイン画面（HM0001）を表示する。
     *
     * @return ログイン画面テンプレート名
     */
    @GetMapping("/login")
    public String login() {
        return "HM0001";
    }

    /**
     * 新規登録画面（HM0002）を表示する。
     *
     * @return 新規登録画面テンプレート名
     */
    @GetMapping("/signup")
    public String signup() {
        return "HM0002";
    }

    /**
     * プライバシーポリシー画面（HM0030）を表示する。
     *
     * @return プライバシーポリシー画面テンプレート名
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "HM0030";
    }

    /**
     * 利用規約画面（HM0031）を表示する。
     *
     * @return 利用規約画面テンプレート名
     */
    @GetMapping("/terms")
    public String terms() {
        return "HM0031";
    }

    /**
     * 特定商取引法に基づく表記（HM0032）を表示する。
     *
     * @return 特商法ページのテンプレート名
     */
    @GetMapping("/law")
    public String law() {
        return "HM0032";
    }

    /**
     * テンプレ一覧画面（HM0021）を表示する。
     *
     * @return テンプレ一覧画面のテンプレート名
     */
    @GetMapping("/templates")
    public String templateList(Model model, Authentication auth) {
        setNickName(model, auth);
        return "HM0021";
    }

    /**
     * 書類作成画面（HM0008）を表示する。
     *
     * @return 書類作成画面のテンプレート名
     */
    @GetMapping("/document/create")
    public String createDocument(Model model, Authentication auth) {
        setNickName(model, auth);
        return "HM0008";
    }

    /**
     * お知らせ画面（HM0007）を表示する。
     *
     * @return お知らせ画面のテンプレート名
     */
    @GetMapping("/news")
    public String documents(Model model, Authentication auth) {
        setNickName(model, auth);
        return "HM0007";
    }

    /**
     * ログイン状態に応じて Model に nickname を設定する。
     */
    private void setNickName(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            try {
                var user = userService.findByEmail(auth.getName());
                model.addAttribute("nickname", user.getNickname());
                return;
            } catch (Exception ignored) {
                // intentionally fall through to guest
            }
        }
        model.addAttribute("nickname", "ゲスト");
    }

    /**
     * お問合せ画面（HM0029）を表示する。
     *
     * @return お問い合わせ画面のテンプレート名
     */
    @GetMapping("/contact")
    public String contact(Model model, Authentication auth) {
        setNickName(model, auth);
        return "HM0029";
    }

    @PostMapping("/contact/submit")
    public String submitContact(
            @RequestParam("category") String category,
            @RequestParam("message") String message,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByEmail(currentUser.getUsername());
            inquiryService.createInquiry(user, category, message);
            model.addAttribute("success", "送信が完了しました。ありがとうございます。\n");
            return "HM0029";

        } catch (Exception e) {
            model.addAttribute("error", "送信処理中にエラーが発生しました。再度お試しください。");
            return "HM0029";
        }
    }
}