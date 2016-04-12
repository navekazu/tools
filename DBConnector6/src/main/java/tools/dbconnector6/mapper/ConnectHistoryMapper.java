package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.ConnectHistory;

/**
 * データベース接続履歴用エンティティの永続化を行う。
 * 内容はフラットファイルとして永続化され、「~/.DBConnector6/config/connection_history」として保存する。<br>
 */
public class ConnectHistoryMapper extends MapperBase<ConnectHistory> {
    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * データベース接続履歴は「connection_history」を返す。
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "connection_history";
    }

    /**
     * 永続化時のファイルの拡張子を返す。
     * データベース接続履歴は拡張子はないので空文字を返す。
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
    protected ConnectHistory unboxing(String line) {
        String[] data = line.split("\t");
        return ConnectHistory.builder()
                .connectedDate(stringToDate(data[0].trim()))
                .libraryPath(data[1].trim())
                .driver(data[2].trim())
                .url(data[3].trim())
                .user(data[4].trim())
                .password(data[5].trim())
                .build();
    }

    /**
     * エンティティから永続化を行う
     * @param connect エンティティインスタンス
     * @return 永続化結果 （テキストファイルの1行）
     */
    @Override
    protected String autoboxing(ConnectHistory connect) {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s"
                , blankToOneSpace(dateToString(connect.getConnectedDate()))
                , blankToOneSpace(connect.getLibraryPath())
                , blankToOneSpace(connect.getDriver())
                , blankToOneSpace(connect.getUrl())
                , blankToOneSpace(connect.getUser())
                , blankToOneSpace(connect.getPassword())
        );
    }
}
