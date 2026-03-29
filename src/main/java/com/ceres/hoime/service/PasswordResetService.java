package com.ceres.hoime.service;

import com.ceres.hoime.entity.PasswordResetToken;

/**
 * パスワードリセットフローを担当するサービスのインターフェース。
 *
 * <p>「メールアドレス入力 → トークン発行 → メール送信 → 新パスワード設定」
 * という一連の処理を定義する。</p>
 */
public interface PasswordResetService {

    /**
     * パスワードリセットのメール送信処理。
     *
     * <p>指定メールアドレスのユーザーが存在する場合のみトークンを発行してメールを送信する。
     * 存在しない場合は何もしない（ユーザー存在確認攻撃の防止）。</p>
     *
     * @param email 再設定対象のメールアドレス
     */
    void requestPasswordReset(String email);

    /**
     * トークン文字列から PasswordResetToken を取得する。
     *
     * @param token トークン文字列
     * @return 該当トークン（存在しない場合は null）
     */
    PasswordResetToken findByToken(String token);

    /**
     * トークン検証後にパスワードを更新する。
     *
     * <p>トークンが無効・期限切れ・使用済みの場合は例外をスローする。</p>
     *
     * @param token       トークン文字列
     * @param newPassword 新しいパスワード（平文）
     * @throws IllegalArgumentException トークンが無効な場合
     */
    void resetPassword(String token, String newPassword);
}
