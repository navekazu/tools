package tools.filerenametool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SmallCharacterRenamer implements FileRenamerInterface {
    private static Map<String, String> renameMap = new HashMap<>();

    static {
        renameMap.put("　", " ");
        renameMap.put("Ａ", "A");
        renameMap.put("Ｂ", "B");
        renameMap.put("Ｃ", "C");
        renameMap.put("Ｄ", "D");
        renameMap.put("Ｅ", "E");
        renameMap.put("Ｆ", "F");
        renameMap.put("Ｇ", "G");
        renameMap.put("Ｈ", "H");
        renameMap.put("Ｉ", "I");
        renameMap.put("Ｊ", "J");
        renameMap.put("Ｋ", "K");
        renameMap.put("Ｌ", "L");
        renameMap.put("Ｍ", "M");
        renameMap.put("Ｎ", "N");
        renameMap.put("Ｏ", "O");
        renameMap.put("Ｐ", "P");
        renameMap.put("Ｑ", "Q");
        renameMap.put("Ｒ", "R");
        renameMap.put("Ｓ", "S");
        renameMap.put("Ｔ", "T");
        renameMap.put("Ｕ", "U");
        renameMap.put("Ｖ", "V");
        renameMap.put("Ｗ", "W");
        renameMap.put("Ｘ", "X");
        renameMap.put("Ｙ", "Y");
        renameMap.put("Ｚ", "Z");
        renameMap.put("ａ", "a");
        renameMap.put("ｂ", "b");
        renameMap.put("ｃ", "c");
        renameMap.put("ｄ", "d");
        renameMap.put("ｅ", "e");
        renameMap.put("ｆ", "f");
        renameMap.put("ｇ", "g");
        renameMap.put("ｈ", "h");
        renameMap.put("ｉ", "i");
        renameMap.put("ｊ", "j");
        renameMap.put("ｋ", "k");
        renameMap.put("ｌ", "l");
        renameMap.put("ｍ", "m");
        renameMap.put("ｎ", "n");
        renameMap.put("ｏ", "o");
        renameMap.put("ｐ", "p");
        renameMap.put("ｑ", "q");
        renameMap.put("ｒ", "r");
        renameMap.put("ｓ", "s");
        renameMap.put("ｔ", "t");
        renameMap.put("ｕ", "u");
        renameMap.put("ｖ", "v");
        renameMap.put("ｗ", "w");
        renameMap.put("ｘ", "x");
        renameMap.put("ｙ", "y");
        renameMap.put("ｚ", "z");
        renameMap.put("０", "0");
        renameMap.put("１", "1");
        renameMap.put("２", "2");
        renameMap.put("３", "3");
        renameMap.put("４", "4");
        renameMap.put("５", "5");
        renameMap.put("６", "6");
        renameMap.put("７", "7");
        renameMap.put("８", "8");
        renameMap.put("９", "9");
        renameMap.put("！", "!");
        renameMap.put("＃", "#");
        renameMap.put("＄", "$");
        renameMap.put("％", "%");
        renameMap.put("＆", "&");
        renameMap.put("’", "'");
        renameMap.put("（", "(");
        renameMap.put("）", ")");
        renameMap.put("＝", "=");
        renameMap.put("￣", "~");
        renameMap.put("｛", "{");
        renameMap.put("｝", "}");
        renameMap.put("｀", "`");
        renameMap.put("＿", "_");
        renameMap.put("＋", "+");
        renameMap.put("－", "-");
        renameMap.put("＠", "@");
        renameMap.put("［", "[");
        renameMap.put("］", "]");
//        renameMap.put("．", ".");
        renameMap.put("，", ",");
    }

    @Override
    public String getName() {
        return "SmallCharacterRenamer";
    }

    @Override
    public File execute(File file) {
        String smallCharacterFileName = getSmallCharacter(file.getName());
        if (file.getName().equals(smallCharacterFileName)) {
            return file;
        }

        File newFile = new File(file.getParentFile(), smallCharacterFileName);
        file.renameTo(newFile);

        return newFile;
    }

    String getSmallCharacter(String name) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            String substring = name.substring(i, i+1);
            if (renameMap.containsKey(substring)) {
                substring = renameMap.get(substring);
            }
            sb.append(substring);
        }

        return sb.toString();
    }
}
