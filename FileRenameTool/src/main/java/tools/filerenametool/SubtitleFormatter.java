package tools.filerenametool;

import java.io.File;

public class SubtitleFormatter implements FileRenamerInterface {
    private String[] formatTarget = new String[] {
            " ",
            "【",
            "[",
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

        return null;
    }
}
