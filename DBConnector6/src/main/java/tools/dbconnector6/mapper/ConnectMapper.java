package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.Connect;

/**
 * データベース接続用エンティティの永続化を行う。
 * 内容はフラットファイルとして永続化され、「~/.DBConnector6/config/connection」として保存する。<br>
 */
public class ConnectMapper extends MapperBase<Connect> {
    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * データベース接続は「connection」を返す。
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "connection";
    }

    /**
     * 永続化時のファイルの拡張子を返す。
     * データベース接続は拡張子はないので空文字を返す。
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return "";
    }

    /**
     * 永続化結果（テキストファイルの1行）からエンティティを復元する
     * @param line テキストファイルの1行
     * @return 復元したエンティティインスタンス
     */
    @Override
    protected Connect unboxing(String line) {
        String[] data = line.split("\t");
        return Connect.builder()
                .libraryPath(data[0].trim())
                .driver(data[1].trim())
                .url(data[2].trim())
                .user(data[3].trim())
                .password(data[4].trim())
                .build();
    }

    /**
     * エンティティから永続化を行う
     * @param connect エンティティインスタンス
     * @return 永続化結果 （テキストファイルの1行）
     */
    @Override
    protected String autoboxing(Connect connect) {
        return String.format("%s\t%s\t%s\t%s\t%s"
                , blankToOneSpace(connect.getLibraryPath())
                , blankToOneSpace(connect.getDriver())
                , blankToOneSpace(connect.getUrl())
                , blankToOneSpace(connect.getUser())
                , blankToOneSpace(connect.getPassword())
        );
    }
}
