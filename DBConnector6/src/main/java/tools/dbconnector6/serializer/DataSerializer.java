package tools.dbconnector6.serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

/**
 * データ永続化の最基底クラス。
 */
public abstract class DataSerializer {
    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * @return 永続化時のファイル名
     */
    protected abstract String getArchiveFileName();

    /**
     * 永続化時のファイルの拡張子を返す。<br>
     * @return 永続化時のファイルの拡張子
     */
    protected abstract String getArchiveFileSuffix();

    /**
     * 永続化する際のファイル名を含むパスを取得する
     * @return 永続化パス
     */
    protected abstract Path getArchiveFilePath() throws IOException ;

    // ユニットテストフラグ
    // テスト用の設定ファイルと実際の設定ファイルをミックスしないようにユニットテスト時のファイル名を変更するフラグ
    private static boolean utMode = false;

    /**
     * ユニットテストフラグの設定をする。<br>
     * ユニットテスト実行時は true を、それ以外は false を設定する。<br>
     * 初期値は false なので、ユニットテスト時以外は呼び出す必要はない。<br>
     * @param utMode 設定するユニットテストフラグの値
     */
    public static void setUtMode(boolean utMode) {
        DataSerializer.utMode = utMode;
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する。<br>
     * パスは下記のようになる。<br>
     * ~/.DBConnector6/{引数kindの値}/{getArchiveFileName()の結果}{getArchiveFileSuffix()の結果}<br>
     * @param kind ファイルの種類。永続化パスの一部となる。
     * @return 永続化パス
     */
    protected Path getArchiveFilePath(String kind) {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", kind, getArchiveFileName() + getArchiveFileSuffix() + (utMode? "_test": ""));
    }

    /**
     * 一時ファイルのパスを取得する。<br>
     * パスは下記のようになる。<br>
     * ~/.DBConnector6/{引数kindの値}/{getArchiveFileName()の結果}ユニーク値{getArchiveFileSuffix()の結果}<br>
     * @param kind ファイルの種類。一時ファイルパスの一部となる。
     * @return 作成した一時ファイルのパス
     * @throws IOException 一時ファイルの作成に失敗した場合
     */
    protected Path getTempFilePath(String kind) throws IOException {
        Path parent = Paths.get(System.getProperty("user.home"), ".DBConnector6", kind);
        Files.createDirectories(parent);
        return Files.createTempFile(parent, getArchiveFileName(), getArchiveFileSuffix() + (utMode? "_test": ""));
    }

    /**
     * 具象クラスが返すパスを使用してテキストを追記する。
     * @param text 追記するテキスト
     * @return 書き込んだファイルのパス
     * @throws IOException 書き込みに失敗した場合
     */
    public Path appendText(String text) throws IOException {
        return writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * 具象クラスが返すパスを使用してテキストを更新する。
     * @param text 更新するテキスト
     * @return 書き込んだファイルのパス
     * @throws IOException 書き込みに失敗した場合
     */
    public Path updateText(String text) throws IOException {
        return writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * VM終了時に削除するようマークした一時ファイルを作成してテキストを更新する。
     * @param text 更新するテキスト
     * @return 作成した一時ファイルのパス
     * @throws IOException ファイルの作成か書き込みに失敗した場合
     */
    public Path createTempolaryFile(String text) throws IOException {
        Path path = writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        path.toFile().deleteOnExit();   // VM終了時に削除するようマークする
        return path;
    }

    /**
     * 指定されたパスのファイルを指定されたオープン方法で開き、テキストを更新する。
     * @param path オープンするファイルのパス
     * @param text 更新するテキスト
     * @param options オープン方法
     * @return オープンしたファイルのパス
     * @throws IOException ファイルのオープンか書き込みに失敗した場合
     */
    private Path writeText(Path path, String text, OpenOption... options) throws IOException {
        Files.createDirectories(path.getParent());

        try {
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, options))) {
                for (int loop = 0; loop < text.length(); loop++) {
                    if (text.charAt(loop) == '\n') {        // 改行コードはOS依存のコードを使用する
                        out.println();
                    } else {
                        out.print(text.charAt(loop));
                    }
                }

                // 最後の文字が改行でないなら、改行を付加する
                if (!text.endsWith("\n")) {
                    out.println();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            throw e;
        }

        return path;
    }

    /**
     * 具象クラスが返すパスを使用してテキストを読み込む。
     * @return 読み込んだファイルの内容
     * @throws IOException 読み込みに失敗した場合
     */
    public String readText() throws IOException {
        return readText(getArchiveFilePath());
    }

    /**
     * 指定されたパスのテキストを読み込む。
     * @param path 読み込むファイルのパス
     * @return 読み込んだファイルの内容
     * @throws IOException 読み込みに失敗した場合
     */
    public String readText(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        StringBuilder stringBuilder = new StringBuilder();

        if (!Files.exists(path)) {
            return "";
        }

        Files.readAllLines(path).stream()
                .forEach(s -> {
                    stringBuilder.append(s);
                    stringBuilder.append("\n");
                });

        return stringBuilder.toString();
    }
}
