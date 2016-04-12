package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * データベース接続履歴用のエンティティクラス。
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ConnectHistory {
    private Date connectedDate;     // 接続日時
    private String libraryPath;     // JARファイルのパス
    private String driver;          // JDBCドライバ名
    private String url;             // データベース接続のURL
    private String user;            // 接続時のユーザー名
    private String password;        // 接続時のパスワード

    // 接続日時のシリアライズ/デシリアライズに使用するフォーマット
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 接続履歴に表示する ConnectHistory の文字列表現。<br>
     * 接続時のパスワードは含まれない。
     * @return ConnectHistory の文字列表現
     */
    @Override
    public String toString() {
        return String.format("%s Lib[%s] Dir[%s] URL[%s] Use[%s]"
                , (connectedDate==null? "": DATE_FORMAT.format(connectedDate))
                , (libraryPath==null? "": libraryPath)
                , (driver==null? "": driver)
                , (url==null? "": url)
                , (user==null? "": user)
        );
    }
}
