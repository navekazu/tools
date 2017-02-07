package tools.filerenametool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileRenamer {
    public static void main(String[] args) {
    }


    private static final Map<String, String> TO_HALF_CHARACTER_TARGET_MAP = new HashMap<>();
    static {
        TO_HALF_CHARACTER_TARGET_MAP.put("　", " ");         // 全角スペース
        TO_HALF_CHARACTER_TARGET_MAP.put("＃", "#");         // 全角シャープ
        TO_HALF_CHARACTER_TARGET_MAP.put("０", "0");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("１", "1");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("２", "2");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("３", "3");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("４", "4");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("５", "5");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("６", "6");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("７", "7");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("８", "8");         // 全角数値
        TO_HALF_CHARACTER_TARGET_MAP.put("９", "9");         // 全角数値
    }
    String toHalfCharacter(String name) {
        for (String key: TO_HALF_CHARACTER_TARGET_MAP.keySet()) {
            name = name.replaceAll(key, TO_HALF_CHARACTER_TARGET_MAP.get(key));
        }
        return name;
    }

    private static final List<String> MOVE_BEHIND_CHARACTER_LIST = new ArrayList<>();
    static {
        MOVE_BEHIND_CHARACTER_LIST.add("「");
        MOVE_BEHIND_CHARACTER_LIST.add("【");
        MOVE_BEHIND_CHARACTER_LIST.add("～");
        MOVE_BEHIND_CHARACTER_LIST.add(" ");
    }
    String moveBehind(String name) {
        FileName fileName = splitName(name);

        for (String key: MOVE_BEHIND_CHARACTER_LIST) {
        }

        return String.format("%s %s%s%s%s", fileName.title, fileName.datetime,
                (fileName.subtitle.isEmpty()? "": " "+fileName.subtitle),
                (fileName.additionalSubtitle.isEmpty()? "": " "+fileName.additionalSubtitle), fileName.suffix);
    }
    FileName splitName(String name) {
        Pattern pattern = Pattern.compile("(.*)(\\(\\d{8}-\\d{4}\\))(.*)(\\..*)");
        Matcher matcher = pattern.matcher(name);
        boolean b = matcher.matches();
        FileName fileName = new FileName();
        fileName.title = matcher.group(1).trim();
        fileName.datetime = matcher.group(2).trim();
        fileName.subtitle = matcher.group(3).trim();
        fileName.suffix = matcher.group(4).trim();
        return fileName;
    }
    class FileName {
        String title = "";
        String datetime = "";
        String subtitle = "";
        String additionalSubtitle = "";
        String suffix = "";
    }
}
