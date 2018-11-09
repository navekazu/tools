package tools.filenametransfer;

import java.text.DecimalFormat;
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

		DecimalFormat df = new DecimalFormat("00");
		for (int i=0; i<transferList.size(); i++) {
			transferList.get(i).setPrefixName(df.format(i+1));
		}
	}

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
