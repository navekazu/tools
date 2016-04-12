package tools.dbconnector6.transfer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ResultDataTransferClipboard extends ResultDataTransfer {

    public ResultDataTransferClipboard(boolean evidenceMode, boolean includeHeader, String delimiter) {
        super(evidenceMode, includeHeader, delimiter);
    }

    @Override
    public void transfer() {
        if (!evidenceMode) {
            return;
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clip = toolkit.getSystemClipboard();
        StringSelection stringSelection = new StringSelection(getTransferData());
        clip.setContents(stringSelection, stringSelection);
    }
}
