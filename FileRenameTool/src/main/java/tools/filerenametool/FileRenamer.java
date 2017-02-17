package tools.filerenametool;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileRenamer {
    List<String> keywordList = new ArrayList<>();
    Map<String, String> changeTitleMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length==0) {
            System.out.println("usage: java tools.filerenametool.FileRenamer [keywordFile (in app directory)] [target file]...");
            return ;
        }

        FileRenamer fileRenamer = new FileRenamer();
        fileRenamer.readKeywordList(args[0]);
        fileRenamer.readChangeTitleMap(args[1]);

//        Arrays.asList(args, 1);
        for (int i=2; i<args.length; i++) {
            fileRenamer.execute(args[i]);
        }
    }

    void execute(String name) throws IOException {
        Path srcPath = Paths.get(name);
        if (!isTargetFile(srcPath.getFileName().toString())) {
            return;
        }

        Path destPath = Paths.get(srcPath.getParent().toString(), moveBehind(srcPath.getFileName().toString()));
        Files.move(srcPath, destPath);
    }

    void readKeywordList(String file) throws IOException {
        keywordList = Files.readAllLines(Paths.get(FileRenameToolUtil.getToolDirectory().toString(), file), Charset.forName("MS932")).stream()
                .filter(str -> !str.trim().startsWith("#"))     // #から始まる行はコメント行
                .filter(str -> !str.trim().isEmpty())           // 空行は無視
                .collect(Collectors.toList());
    }

    void readChangeTitleMap(String file) throws IOException {
        Files.readAllLines(Paths.get(FileRenameToolUtil.getToolDirectory().toString(), file), Charset.forName("MS932")).stream()
                .filter(str -> !str.trim().startsWith("#"))     // #から始まる行はコメント行
                .filter(str -> !str.trim().isEmpty())           // 空行は無視
                .forEach(str -> {
                    String[] sp = str.split("\t+");             // タブが一文字以上
                    if (sp.length==2) {
                        changeTitleMap.put(sp[0], sp[1]);
                    }
                });
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
    }
    String moveBehind(String name) {
        name = toHalfCharacter(name);
        FileName fileName = splitTitle(splitName(name));
        fileName.title = changeTitle(fileName.title);
        return String.format("%s %s%s%s%s", fileName.title, fileName.datetime,
                (fileName.subtitle.isEmpty()? "": " "+fileName.subtitle),
                (fileName.additionalSubtitle.isEmpty()? "": " "+fileName.additionalSubtitle), fileName.suffix);
    }

    String changeTitle(String title) {
        if (!changeTitleMap.containsKey(title)) {
            return title;
        }
        return changeTitleMap.get(title);
    }

    FileName splitTitle(FileName fileName) {
        int index = -1;
        int keywordIndex = getKeywordIndex(fileName.title);
        int moveBehindIndex = getMoveBehindIndex(fileName.title);
        int spaceIndex = fileName.title.indexOf(" ");

        if (keywordIndex!=-1) {
            index = keywordIndex;
        } else if (moveBehindIndex!=-1) {
            index = moveBehindIndex;
        } else if (spaceIndex!=-1) {
            index = spaceIndex;
        }

        if (index==-1) {
            return fileName;
        }

        fileName.additionalSubtitle = fileName.title.substring(index).trim();
        fileName.title = fileName.title.substring(0, index).trim();

        return fileName;
    }

    int getKeywordIndex(String title) {
        for (String keyword: keywordList) {
            if (title.startsWith(keyword)) {
                return keyword.length();
            }
        }

        return -1;
    }

    int getMoveBehindIndex(String title) {
        for (String keyword: MOVE_BEHIND_CHARACTER_LIST) {
            if (title.indexOf(keyword)!=-1) {
                return title.indexOf(keyword);
            }
        }

        return -1;
    }

    Matcher getRenamerMatcher(String name) {
        Pattern pattern = Pattern.compile("(.*)(\\(\\d{8}-\\d{4}\\))(.*)(\\..*)");
        return pattern.matcher(name);
    }

    boolean isTargetFile(String name) {
        return getRenamerMatcher(name).matches();
    }

    FileName splitName(String name) {
        Matcher matcher = getRenamerMatcher(name);
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
