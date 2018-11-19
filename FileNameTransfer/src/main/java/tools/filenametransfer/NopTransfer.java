package tools.filenametransfer;

import java.io.IOException;


public class NopTransfer extends FileNameTransferBase {
	@Override
	public String getName() {
		return getName("");
	}

	@Override
	public void process(String workDirectory, String fileName) throws IOException {
	}

}
