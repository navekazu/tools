package tools.sqlbreaker;

import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;

public class App {
    private static final String[] BREAK_WORD = new String[]{
        "select",
        "from",
        "where",
        "group by",
        "having",
        "order by",
    };

    private static final String[] INDENT_BREAK_WORD = new String[]{
        "inner join",
        "left join",
        "right join",
    };

    private static final String DELIMETER = "\n";
    private static final String INDENT = " ";

    private App() { }

    public void execute(String[] args) throws IOException {
        if (args.length == 0) {
            sqlBreak(new InputStreamReader(System.in));
        } else {
            for (String arg: args) {
                sqlBreak(new FileReader(arg));
            }
        }

    }

    private void sqlBreak(Reader in) throws IOException {
        String sql = readSql(in);   // 読み込み
        sql = convertStream(sql);   // 複数行を1行に
        sql = breakStructure(sql, BREAK_WORD, 0);           // 分解（インデントなし）
        sql = breakStructure(sql, INDENT_BREAK_WORD, 4);    // 分解（インデント付き）
        System.out.println(sql);
    }

    private String readSql(Reader in) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(in)) {
            reader.lines()
                .forEach(l -> sb.append(l).append(" "));
        }

        return sb.toString();
    }

    private String convertStream(String value) {
        return value.replaceAll("\n", " ");
    }

    private String breakStructure(String sql, String[] keywords, int indentSize) {
        StringBuilder sb = new StringBuilder(sql);

        for (String keyword: keywords) {
            for (int i = 0; i < sb.length()-keyword.length(); i++) {
                if (sb.substring(i).toLowerCase().startsWith(keyword)) {
                    int indentCount = 0;

                    for (int j = 0; j < indentSize; j++) {
                        sb.insert(i, INDENT);
                        indentCount++;
                    }

                    sb.insert(i, DELIMETER);

                    i+=(INDENT.length()*indentCount);
                    i+=DELIMETER.length();
                }
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        (new App()).execute(args);
    }
}
