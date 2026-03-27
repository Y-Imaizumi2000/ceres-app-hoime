package com.ceres.hoime.service;

import com.ceres.hoime.entity.User;

/**
 * ユーザー情報の登録・更新・検索を行うサービスのインターフェース。
 *
 * <p>ユーザー登録、メールアドレス重複チェック、ログイン検索、
 * 会員ランク変更、ステータス変更、退会処理など、
 * ユーザー管理に関する主要なビジネスロジックを定義する。</p>
 *
 * <p>実装クラス（UserServiceImpl）を隠蔽することで、
 * コントローラー層との疎結合を保ち、テスト容易性も向上する。</p>
 */
public interface UserService {

    /**
     * 新規ユーザーを登録する。
     * パスワードは実装クラス側でハッシュ化される。
     *
     * @param email    登録メールアドレス
     * @param password 平文パスワード
     * @param nickname ニックネーム
     * @return 登録された User エンティティ
     */
    User registerUser(String email, String password, String nickname);

    /**
     * メールアドレスが既に登録されているかを判定する。
     *
     * @param email チェック対象メールアドレス
     * @return true: 既に存在する / false: 未登録
     */
    boolean existsByEmail(String email);

    /**
     * メールアドレスを元にユーザーを検索する。
     * ログイン処理などで使用される。
     *
     * @param email 検索対象メールアドレス
     * @return 該当ユーザー（存在しない場合は null）
     */
    User findByEmail(String email);

    /**
     * 会員ランク（FREE / PREMIUM など）を更新する。
     *
     * @param userId 対象ユーザーID
     * @param role   新しいロール
     */
    void updateRole(int userId, String role);

    /**
     * ステータス（ACTIVE / WITHDRAWN / SUSPENDED）を更新する。
     *
     * @param userId 対象ユーザーID
     * @param status 新しいステータス
     */
    void updateStatus(int userId, String status);

    /**
     * 退会処理を行う。
     * leave_ymd の設定とステータス変更（WITHDRAWN）を実施する。
     *
     * @param userId 対象ユーザーID
     */
    void withdrawUser(int userId);
    
    /**
     * ユーザーを認証済みに更新する。
     *
     * @param user 対象ユーザー
     */
    void verifyUser(User user);
    
    /**
     * 認証メール再送処理。
     * <p>
     * ユーザーが認証メールを紛失した場合や、
     * 認証リンクの有効期限が切れた場合に再送を行う。
     * </p>
     *
     * @param email 再送対象ユーザーのメールアドレス
     */
    void resendVerificationEmail(String email);


}