package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.sql.Connection;

/**
 * データベース接続用のエンティティクラス。
 * 内容はフラットファイルとして永続化され、「~/.DBConnector6/config/connection」として保存する。<br>
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Connect {
    private String libraryPath;     // JARファイルのパス
    private String driver;          // JDBCドライバ名
    private String url;             // データベース接続のURL
    private String user;            // 接続時のユーザー名
    private String password;        // 接続時のパスワード
    private Connection connection;  // DB接続子（永続化対象外）

    /**
     * 接続リストに表示するマスクしたパスワード文字列を返す。<br>
     * passwordフィールドに値がある場合は固定で「**********」を返し、それ以外は空文字を返す。
     * @return passwordフィールドに値がある場合は固定で「**********」を返し、それ以外は空文字を返す。
     */
    public String getMaskedPassword() {
        if (password==null || "".equals(password)) {
            return "";
        }
        return "**********";
    }
}
