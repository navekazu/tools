package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.List;

/**
 * メインステージ左下「Table structure」の右タブ「tableColumnTab」に表示するインデックス一覧用エンティティクラス。<br>
 * java.sql.DatabaseMetaData#getPrimaryKeys メソッドの結果と java.sql.DatabaseMetaData#getIndexInfo メソッドの結果を格納する。
 * @see java.sql.DatabaseMetaData#getPrimaryKeys
 * @see java.sql.DatabaseMetaData#getIndexInfo
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TableIndexTab {
    private String indexName;           // インデックス名
    private boolean primaryKey;         // このインデックスがプライマリーキー（getPrimaryKeysで取得した）なら true それ以外（getIndexInfoで取得した）なら false。
    private boolean uniqueKey;          // このインデックスがユニークインデックスなら true それ以外なら false。
    private List<String> columnList;    // このインデックスの列一覧

    /**
     * インデックス名を返す
     * @return インデックス名
     */
    @Override
    public String toString() {
        return indexName;
    }
}
