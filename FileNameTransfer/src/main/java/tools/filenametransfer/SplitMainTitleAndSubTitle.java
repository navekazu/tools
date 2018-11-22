package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SplitMainTitleAndSubTitle extends FileNameTransferBase {
    private List<String> specialNameList;

    @Override
    public void initialize() {
        super.initialize();

        try {
            specialNameList = Files.readAllLines(Paths.get("special_name.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
	public String getName() {
		return getName("タイトルとサブタイトルを分ける");
	}

    private class FileNameStructure {
        public String title;
        public String date;
        public String subTitle;
        public String extension;
    }

	@Override
	public void process(String workDirectory, String fileName) throws IOException {
	    FileNameStructure structure = getFileNameStructure(fileName);
	    String newFileName = String.format("%s (%s)%s.%s"
	            , structure.title
	            , structure.date
	            , (structure.subTitle.isEmpty()? "": " ") + structure.subTitle
	            , structure.extension);

        Files.move(Paths.get(workDirectory, fileName),
                Paths.get(workDirectory, newFileName));
	}

    private FileNameStructure getFileNameStructure(String fileName) {
        return null;
    }


}

