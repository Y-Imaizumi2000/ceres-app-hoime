package com.ceres.hoime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログイン・新規登録・利用規約画面への遷移を担当するコントローラー。
 *
 * <p>画面ID（HM0001 / HM0002 / HM0031）に対応する
 * HTML テンプレートを返却する役割を持つ。</p>
 *
 * <p>ビジネスロジックは一切持たず、
 * 単純な画面遷移のみを担当する。</p>
 */
@Controller
public class LoginController {

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
     * 利用規約画面（HM0031）を表示する。
     *
     * @return 利用規約画面テンプレート名
     */
    @GetMapping("/terms")
    public String terms() {
        return "HM0031";
    }
}