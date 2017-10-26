package tools.filerenametool;

import java.io.File;

public class SubtitleFormatter implements FileRenamerInterface {
    private String[] formatTarget = new String[] {
            " ",
            "【",
            "[",
            "「",
    };

    @Override
    public String getName() {
        return null;
    }

    @Override
    public File execute(File file) {
        String name = file.getName();
        int middleBegin;
        int middleEnd;

        for (String s: formatTarget) {
            middleBegin = name.indexOf(s);
            if (middleBegin != -1) {
                break;
            }
        }

        return null;
    }

    int getIndex(String name, String[] arr) {
        int i = -1;

        for (String s: arr) {
            i = name.indexOf(s);
            if (i != -1) {
                break;
            }
        }

        return i;
    }
}
