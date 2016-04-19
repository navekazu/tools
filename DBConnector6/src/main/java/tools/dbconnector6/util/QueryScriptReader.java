package tools.dbconnector6.util;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * SQLスクリプトを読み込む。<br>
 * 幾つかの文字コードで読み込みをトライする。<br>
 */
public class QueryScriptReader {
    // トライする文字コード
    private static final Charset[] AVAILABLE_CHARSETS;
    static {
        AVAILABLE_CHARSETS = new Charset[] {
                Charset.forName("MS932"),
                Charset.forName("UTF-8"),
                Charset.forName("EUC-JP"),
        };
    }

    /**
     * 指定されたパスのテキストファイルを読み込む。<br>
     * 読み込む際には幾つかの文字コードでトライし、最初に読み込めた結果を返す。<br>
     * 以下の順で文字コードをトライする。
     * <ul>
     *     <li>MS932</li>
     *     <li>UTF-8</li>
     *     <li>EUC-JP</li>
     * </ul>
     * @param path 読み込むファイルのパス
     * @return 読み込んだテキスト内容
     * @throws IOException 読み込みに失敗した時
     */
    public static List<String> readAllLines(Path path) throws IOException {
        for (Charset charset: AVAILABLE_CHARSETS) {
            try {
                return Files.readAllLines(path, charset);
            } catch(MalformedInputException e) {
                // 文字コード例外が発生した場合は、握りつぶして次の文字コードを試す。
            }
        }
        throw new CharacterCodingException();
    }
}
