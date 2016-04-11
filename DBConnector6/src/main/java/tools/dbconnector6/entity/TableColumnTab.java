package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * メインステージ左下「Table structure」の中央タブ「tableColumnTab」に表示するテーブルのカラム一覧用エンティティクラス。<br>
 * java.sql.DatabaseMetaData#getColumns メソッドの戻り値を格納する。
 * @see java.sql.DatabaseMetaData#getColumns
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TableColumnTab {
    private String name;                // 列名。
    private String type;                // データ・ソース依存の型名。
    private Integer size;               // 列サイズ。
    private Integer decimalDigits;      // 小数点以下の桁数。
    private String nullable;            // NULLが許されるか。
                                        //   "No Null" - NULL値を許さない可能性がある（DatabaseMetaData.columnNoNulls）。
                                        //   "" - 必ずNULL値を許す（DatabaseMetaData.columnNullable）。
                                        //   "Unknown" - NULL値を許すかどうかは不明（DatabaseMetaData.columnNullableUnknown）。
    private Integer primaryKey;         // 列のコメント
    private String remarks;             // 列のコメント
    private String columnDefault;       // 列のデフォルト値
    private String autoincrement;       // 列が自動インクリメントされるかどうか。
                                        // "YES" - 列が自動インクリメントされる場合
                                        // "NO" - 列が自動インクリメントされない場合
                                        // 空の文字列 - 列が自動インクリメントされるかどうかが判断できない場合
    private String generatedColumn;     // 生成された列かどうか。
                                        // "YES" - これが生成された列である場合
                                        // "NO" - これが生成された列でない場合
                                        // 空の文字列 - これが生成された列かどうかが判断できない場合
}
