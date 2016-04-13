package tools.dbconnector6.mapper;

import tools.dbconnector6.serializer.DataSerializer;

import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * エンティティの永続化を行う基底クラス。
 * 内容はフラットファイルとして永続化され、それぞれの具象クラスでファイル名と、永続化/復元化を実装する。<br>
 */
public abstract class MapperBase<T> extends DataSerializer {
    /**
     * 永続化結果（テキストファイルの1行）からエンティティを復元する
     * @param line テキストファイルの1行
     * @return 復元したエンティティインスタンス
     */
    protected abstract T unboxing(String line);

    /**
     * エンティティから永続化を行う
     * @param t エンティティインスタンス
     * @return 永続化結果 （テキストファイルの1行）
     */
    protected abstract String autoboxing(T t);

    /**
     * Date型の項目を永続化する際の文字列形式
     */
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 値がnullかtrimした結果が空文字の場合に空文字を返す
     * @param s 評価値
     * @return 評価値がnullかtrimした結果が空文字の場合に空文字を、それ以外は評価値をそのまま返す
     */
    protected String blankToOneSpace(String s) {
        if (s==null || "".equals(s.trim())) {
            return " ";
        }
        return s;
    }

    /**
     * 永続化したファイルを読み込み、全エンティティを取得する
     * @return 読み込んだエンティティリスト。ファイルがなかった場合、空のリストを返す
     * @throws IOException 永続化情報の読み込みに失敗したとき
     */
    public List<T> selectAll() throws IOException {
        Path path = getArchiveFilePath();

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(path).stream()
                .map(s -> unboxing(s))
                .collect(Collectors.toList());
    }

    /**
     * 指定されたエンティティのリストを永続化する
     * @param list 永続化するエンティティのリスト
     * @throws IOException 永続化情報の書き込みに失敗したとき
     */
    public void save(List<T> list) throws IOException {
        Path path = getArchiveFilePath();
        List<String> stringList = list.stream()
                .map(t -> autoboxing(t))
                .collect(Collectors.toList());
        Files.createDirectories(path.getParent());
        Files.write(path, stringList);
    }

    /**
     * 永続化したファイルを削除する
     * @throws IOException ファイルの削除に失敗した場合
     */
    public void clear() throws IOException {
        Path path = getArchiveFilePath();
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する
     * @return 永続化パス
     * @throws IOException
     */
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("config");
    }

    /**
     * Dateを文字列に変換する。<br>
     * 変換にはDATE_FORMATフィールドを使用する
     * @param date 対象のDate
     * @return 変換後の文字列
     * @see tools.dbconnector6.mapper.MapperBase#DATE_FORMAT
     */
    protected String dateToString(Date date) {
        if (date==null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * 文字列情報を日付型に変換する
     * 変換にはDATE_FORMATフィールドを使用する
     * @param value 変換する文字列情報
     * @return 変換した日付
     * @see tools.dbconnector6.mapper.MapperBase#DATE_FORMAT
     */
    protected Date stringToDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
