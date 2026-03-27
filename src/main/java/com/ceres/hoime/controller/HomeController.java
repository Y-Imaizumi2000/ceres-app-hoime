package com.ceres.hoime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ceres.hoime.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

/**
 * ホーム画面（ダッシュボード）を表示するコントローラー。
 *
 * <p>今日の書類、AI提案、通知、進捗など、
 * ユーザーが最初に確認すべき情報をまとめて表示する。</p>
 *
 * <p>画面表示に必要なデータ取得は Service 層に委譲し、
 * Controller は画面遷移と Model へのデータ設定のみを担当する。</p>
 */
@Slf4j
@Controller
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * ホーム画面（HM0006）を表示する。
     *
     * @param model 画面に表示するデータを格納する Model
     * @return HM0006（ホーム画面）
     */
    @GetMapping("/home")
    public String home(Model model) {

        // --- method start ---
        log.debug("【HomeController.home】start");

        // 今日の書類一覧
        model.addAttribute("todayDocs", dashboardService.getTodayDocuments());
        log.debug("【HomeController.home】todayDocs loaded");

        // AI 提案
        model.addAttribute("aiSuggestions", dashboardService.getAiSuggestions());
        log.debug("【HomeController.home】aiSuggestions loaded");

        // 通知
        model.addAttribute("notifications", dashboardService.getNotifications());
        log.debug("【HomeController.home】notifications loaded");

        // 進捗
        model.addAttribute("progress", dashboardService.getProgress());
        log.debug("【HomeController.home】progress loaded");

        // --- method end ---
        log.debug("【HomeController.home】end");

        return "HM0006";
    }
}
