package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * アプリケーション設定のうち、エディタの設定を保持するエンティティクラス。<br>
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigEditor extends AppConfig {
    private String editorPath;      // エディタのパス

    /**
     * エディタの設定を表すラベル。<br>
     * @return エディタのラベル文字列 "Editor"
     */
    public static String getLabel() {
        return "Editor";
    }
}
