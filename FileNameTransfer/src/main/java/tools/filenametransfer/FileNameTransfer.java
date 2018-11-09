package tools.filenametransfer;

import java.util.ArrayList;
import java.util.List;

public class FileNameTransfer {
	private List<FileNameTransferBase> transferList;

	public FileNameTransfer() {
		initTransferList();
	}

	private void initTransferList() {
		transferList = new ArrayList<>();
		transferList.add(new SplitMainTitleAndSubTitle());
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
