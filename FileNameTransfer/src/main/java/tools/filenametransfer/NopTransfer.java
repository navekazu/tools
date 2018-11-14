package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class NopTransfer extends FileNameTransferBase {
	@Override
	public String getName() {
		return getName("");
	}

	@Override
	public void process(String workDirectory, String toDirectory, String fileName) throws IOException {
		Path source = Paths.get(workDirectory, fileName);
		Path dest = Paths.get(toDirectory, fileName);
		Files.move(source, dest);
	}

}
