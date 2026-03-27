package com.ceres.hoime.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ceres.hoime.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

/**
 * ホーム画面（ダッシュボード）に表示する情報を生成するサービス実装。
 *
 * <p>現時点ではダミーデータを返却するが、
 * 将来的に DB や AI モデルと連携して動的に生成する予定。</p>
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public List<String> getTodayDocuments() {
        log.debug("【DashboardService.getTodayDocuments】start");

        List<String> docs = List.of("連絡帳（3件）", "週案（1件）");

        log.debug("【DashboardService.getTodayDocuments】end docs={}", docs);
        return docs;
    }

    @Override
    public List<String> getAiSuggestions() {
        log.debug("【DashboardService.getAiSuggestions】start");

        List<String> suggestions = List.of(
                "今日は気温が高く、外遊びが気持ちよくできました。",
                "春の制作活動にぴったりの導入文です。"
        );

        log.debug("【DashboardService.getAiSuggestions】end suggestions={}", suggestions);
        return suggestions;
    }

    @Override
    public List<String> getNotifications() {
        log.debug("【DashboardService.getNotifications】start");

        List<String> notifications = List.of(
                "認証メールが未確認です",
                "AI生成の残り 2回です"
        );

        log.debug("【DashboardService.getNotifications】end notifications={}", notifications);
        return notifications;
    }

    @Override
    public Map<String, Object> getProgress() {
        log.debug("【DashboardService.getProgress】start");

        Map<String, Object> progress = Map.of(
                "documentsThisMonth", 12,
                "aiUsage", 8,
                "renrakuchoRate", "20/25"
        );

        log.debug("【DashboardService.getProgress】end progress={}", progress);
        return progress;
    }
}