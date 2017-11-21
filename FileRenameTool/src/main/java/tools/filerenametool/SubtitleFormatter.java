package tools.filerenametool;

import java.io.File;
import java.util.List;

public class SubtitleFormatter implements FileRenamerInterface {
    private String[] formatTarget = new String[] {
            " ",
            "【",
            "[",
            "「",
    };

    private String[] endTarget = new String[] {
            "(",
    };

    @Override
    public String getName() {
        return "SubtitleFormatter";
    }

    @Override
    public File execute(File file) {
        String name = file.getName();
        int middleBegin = getIndex(name, formatTarget, true);
        int middleEnd = getIndex(name, endTarget, false);

/*
        for (String s: formatTarget) {
            middleBegin = name.indexOf(s);
            if (middleBegin != -1) {
                break;
            }
        }
*/
        return null;
    }

    int getIndex(String name, String[] arr, boolean findFromBegin) {
        int i = -1;

        for (String s: arr) {
            i = findFromBegin? name.indexOf(s): name.lastIndexOf(s);

            if (i != -1) {
                break;
            }
        }

        return i;
    }


    List<String> getExistList() {
       return null;
    }
}
