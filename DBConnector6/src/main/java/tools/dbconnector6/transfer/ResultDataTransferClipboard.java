package tools.dbconnector6.transfer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * クエリ実行結果の内容をクリップボードに転送する。<br>
 */
public class ResultDataTransferClipboard extends ResultDataTransfer {

    /**
     * コンストラクタ
     * @param evidenceMode エビデンス取得モード。クエリ実行結果をエビデンスとして転送する場合は true、それ以外は false
     * @param includeHeader エビデンスにヘッダ（カラム）を含めるか？ 含める場合は true、それ以外は false
     * @param delimiter カラムの区切り文字
     */
    public ResultDataTransferClipboard(boolean evidenceMode, boolean includeHeader, String delimiter) {
        super(evidenceMode, includeHeader, delimiter);
    }

    /**
     * クエリ実行結果の転送処理。<br>
     * クエリ実行結果はクリップボードに転送する。<br>
     */
    @Override
    public void transfer() {
        if (!getEvidenceMode()) {
            return;
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clip = toolkit.getSystemClipboard();
        StringSelection stringSelection = new StringSelection(getTransferData());
        clip.setContents(stringSelection, stringSelection);
    }
}
