package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * アプリケーション設定のうち、エビデンス取得モードの設定を保持するエンティティクラス。<br>
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigEvidenceMode extends AppConfig {
    private boolean evidenceMode;       // エビデンスとしてSQL実行結果をクリップボードに貼り付けるかのフラグ。
    private boolean includeHeader;      // エビデンス取得時にヘッダとしてカラム名を含めるかのフラグ。
    private int evidenceDelimiter;      // エビデンス取得時のカラム区切り文字。0はタブ文字、1はカンマ、2はスペース。

    /**
     * エビデンス取得モードを表すラベル。<br>
     * @return エビデンス取得モードのラベル文字列 "EvidenceMode"
     */
    public static String getLabel() {
        return "EvidenceMode";
    }
}
