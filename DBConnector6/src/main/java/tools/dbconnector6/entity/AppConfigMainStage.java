package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * アプリケーション設定のうち、メインステージの設定を保持するエンティティクラス。<br>
 * 起動時の座標などを保持する。
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigMainStage extends AppConfig {
    private boolean maximized;              // メインステージを最大化する場合は true、それ以外は false。
    private double x;                       // メインステージの左上X座標。
    private double y;                       // メインステージの左上Y座標。
    private double width;                   // メインステージの幅。
    private double height;                  // メインステージの高さ。
    private double primaryDividerPosition;  // メインステージ中央の垂直ディバイダの位置。
    private double leftDividerPosition;     // メインステージ左の水平ディバイダの位置。
    private double rightDivider1Position;   // メインステージ右上の水平ディバイダの位置。
    private double rightDivider2Position;   // メインステージ右下の水平ディバイダの位置。

    /**
     * メインステージの設定を表すラベル。<br>
     * @return メインステージ設定のラベル文字列 "MainStage"
     */
    public static String getLabel() {
        return "MainStage";
    }
}
