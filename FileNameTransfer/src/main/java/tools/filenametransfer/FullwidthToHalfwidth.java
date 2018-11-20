package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FullwidthToHalfwidth extends FileNameTransferBase {
    private static Map<Character, Character> convertMap;

    @Override
    public String getName() {
        return getName("全角半角変換");
    }

    @Override
    public void process(String workDirectory, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<fileName.length(); i++) {
            char c = fileName.charAt(i);
            if (convertMap.containsKey(c)) {
                c = convertMap.get(c);
            }
            sb.append(c);
        }

        Files.move(Paths.get(workDirectory, fileName),
                Paths.get(workDirectory, sb.toString()));

    }

    static {
        convertMap = new HashMap<>();
        convertMap.put('　', ' ');
        convertMap.put('ａ', 'a');
        convertMap.put('ｂ', 'b');
        convertMap.put('ｃ', 'c');
        convertMap.put('ｄ', 'd');
        convertMap.put('ｅ', 'e');
        convertMap.put('ｆ', 'f');
        convertMap.put('ｇ', 'g');
        convertMap.put('ｈ', 'h');
        convertMap.put('ｉ', 'i');
        convertMap.put('ｊ', 'j');
        convertMap.put('ｋ', 'k');
        convertMap.put('ｌ', 'l');
        convertMap.put('ｍ', 'm');
        convertMap.put('ｎ', 'n');
        convertMap.put('ｏ', 'o');
        convertMap.put('ｐ', 'p');
        convertMap.put('ｑ', 'q');
        convertMap.put('ｒ', 'r');
        convertMap.put('ｓ', 's');
        convertMap.put('ｔ', 't');
        convertMap.put('ｕ', 'u');
        convertMap.put('ｖ', 'v');
        convertMap.put('ｗ', 'w');
        convertMap.put('ｘ', 'x');
        convertMap.put('ｙ', 'y');
        convertMap.put('ｚ', 'z');
        convertMap.put('Ａ', 'A');
        convertMap.put('Ｂ', 'B');
        convertMap.put('Ｃ', 'C');
        convertMap.put('Ｄ', 'D');
        convertMap.put('Ｅ', 'E');
        convertMap.put('Ｆ', 'F');
        convertMap.put('Ｇ', 'G');
        convertMap.put('Ｈ', 'H');
        convertMap.put('Ｉ', 'I');
        convertMap.put('Ｊ', 'J');
        convertMap.put('Ｋ', 'K');
        convertMap.put('Ｌ', 'L');
        convertMap.put('Ｍ', 'M');
        convertMap.put('Ｎ', 'N');
        convertMap.put('Ｏ', 'O');
        convertMap.put('Ｐ', 'P');
        convertMap.put('Ｑ', 'Q');
        convertMap.put('Ｒ', 'R');
        convertMap.put('Ｓ', 'S');
        convertMap.put('Ｔ', 'T');
        convertMap.put('Ｕ', 'U');
        convertMap.put('Ｖ', 'V');
        convertMap.put('Ｗ', 'W');
        convertMap.put('Ｘ', 'X');
        convertMap.put('Ｙ', 'Y');
        convertMap.put('Ｚ', 'Z');
        convertMap.put('０', '0');
        convertMap.put('１', '1');
        convertMap.put('２', '2');
        convertMap.put('３', '3');
        convertMap.put('４', '4');
        convertMap.put('５', '5');
        convertMap.put('６', '6');
        convertMap.put('７', '7');
        convertMap.put('８', '8');
        convertMap.put('９', '9');
        convertMap.put('！', '!');
        convertMap.put('＃', '#');
        convertMap.put('＄', '$');
        convertMap.put('％', '%');
        convertMap.put('＆', '&');
        convertMap.put('’', '\'');
        convertMap.put('（', '(');
        convertMap.put('）', ')');
        convertMap.put('＝', '=');
        convertMap.put('￣', '~');
        convertMap.put('｛', '{');
        convertMap.put('｝', '}');
        convertMap.put('｀', '`');
        convertMap.put('＿', '_');
        convertMap.put('－', '-');
        convertMap.put('＋', '+');

    }
}
