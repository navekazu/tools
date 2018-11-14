package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileNameTransferBase {
	protected String prefixName;
	protected Path workDirectory;

	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	protected String getName(String name) {
		return prefixName==null? name: prefixName+"_"+name;
	}

	public String getWorkDirectory(String base) {
		Path path = Paths.get(base, getName());
		return path.toString();
	}

	public abstract String getName();
	public abstract void process(String workDirectory, String toDirectory, String fileName) throws IOException ;
}
