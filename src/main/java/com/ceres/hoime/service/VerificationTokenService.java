package com.ceres.hoime.service;

import com.ceres.hoime.entity.User;
import com.ceres.hoime.entity.VerificationToken;

/**
 * メールアドレス認証用トークンを生成するサービスのインターフェース。
 *
 * <p>ユーザー登録後に呼び出され、UUID を元に認証トークンを生成し、
 * 実装クラス側で DB に保存する責務を持つ。</p>
 *
 * <p>このインターフェースを利用することで、コントローラーは
 * 実装クラスに依存せず、疎結合な構造を保つことができる。</p>
 */
public interface VerificationTokenService {

    /**
     * 指定されたユーザーに紐づく認証トークンを生成し、
     * 実装クラス側で DB に保存した上でトークン文字列を返却する。
     *
     * @param user 認証トークンを発行する対象ユーザー
     * @return 生成されたトークン文字列（UUID）
     */
    String createToken(User user);
    
    /**
     * トークン文字列から VerificationToken を取得する。
     *
     * @param token トークン文字列
     * @return VerificationToken（存在しない場合は null）
     */
    VerificationToken findByToken(String token);

    /**
     * トークンを削除する。
     *
     * @param token 削除対象のトークン
     */
    void delete(VerificationToken token);

}