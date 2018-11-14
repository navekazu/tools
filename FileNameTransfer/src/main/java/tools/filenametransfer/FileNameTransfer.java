package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileNameTransfer {
	private List<FileNameTransferBase> transferList;
	private String fromDirectory;
	private String toDirectory;
	private String workDirectory;

	public FileNameTransfer() {
	}

	private void initTransferList() throws IOException {
		transferList = new ArrayList<>();
		transferList.add(new SplitMainTitleAndSubTitle());
		transferList.add(new FullwidthToHalfwidth());

		// プレフィックスの作成
		DecimalFormat df = new DecimalFormat("00");
		for (int i=0; i<transferList.size(); i++) {
			transferList.get(i).setPrefixName(df.format(i+1));
		}

		// workディレクトリの作成
		for (FileNameTransferBase t: transferList) {
			String w = t.getWorkDirectory(toDirectory);
			Path p = Paths.get(w);
			Files.createDirectories(p);
		}
	}

	private void run() {
	}

	public static void main(String[] args) throws IOException {
		// TODO 自動生成されたメソッド・スタブ

		FileNameTransfer fnt = new FileNameTransfer();
		fnt.fromDirectory = args[0];
		fnt.toDirectory = args[1];
		fnt.workDirectory = args[2];

		fnt.initTransferList();
		fnt.run();
	}

}
