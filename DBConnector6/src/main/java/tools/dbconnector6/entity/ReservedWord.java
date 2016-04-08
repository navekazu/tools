package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * 予約語用のエンティティクラス。
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ReservedWord {
    /**
     * 予約語のタイプ
     */
    public enum ReservedWordType{
        /**
         * SQL
         */
        SQL,

        /**
         * テーブル（テーブル、ビュー、シノニムなど）
         */
        TABLE,

        /**
         * カラム
         */
        COLUMN,
    }

    private ReservedWordType type;  // 予約語のタイプ
    private String word;            // 予約語

    /**
     * 予約語の文字列表現。<br>
     * 予約語フィールドの値を返す
     * @return 予約語フィールドの値
     */
    @Override
    public String toString() {
        return word;
    }
}
