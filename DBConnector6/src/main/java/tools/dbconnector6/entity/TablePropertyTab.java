package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * メインステージ左下「Table structure」の左タブ「tablePropertyTab」に表示するデータベースもしくはテーブルの情報一覧用エンティティクラス。<br>
 * メインステージ左上「DB structure」で、ツリーのルートを選択したときはデータベースの情報を、テーブル等（テーブル、ビュー、シノニム等）を選択したときはテーブルの情報を表示する。<br>
 * データベースの情報は java.sql.DatabaseMetaData#getDatabaseProductVersion メソッドや java.sql.DatabaseMetaData#getDriverName メソッドや java.sql.DatabaseMetaData#getJDBCMajorVersion メソッドの戻り値を格納する。
 * @see java.sql.DatabaseMetaData#getDatabaseProductVersion
 * @see java.sql.DatabaseMetaData#getDriverName
 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TablePropertyTab {
    private String key;         // 情報名
    private String value;       // 値
}
