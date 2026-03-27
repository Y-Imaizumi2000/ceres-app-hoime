package com.ceres.hoime.service;

import java.util.List;
import java.util.Map;

/**
 * ホーム画面（ダッシュボード）に表示する情報を提供するサービス。
 */
public interface DashboardService {

    /** 今日の書類一覧を取得 */
    List<String> getTodayDocuments();

    /** AI 提案文を取得 */
    List<String> getAiSuggestions();

    /** 通知一覧を取得 */
    List<String> getNotifications();

    /** 進捗情報を取得 */
    Map<String, Object> getProgress();
}

