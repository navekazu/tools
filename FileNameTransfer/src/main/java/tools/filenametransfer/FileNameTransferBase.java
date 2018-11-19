package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileNameTransferBase {
	protected String prefixName;
	protected String baseWorkDirectory;

    public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	protected String getName(String name) {
		return prefixName==null? name: prefixName+"_"+name;
	}

    public void setBaseWorkDirectory(String baseWorkDirectory) {
        this.baseWorkDirectory = baseWorkDirectory;
    }

	public String getWorkDirectory() {
		Path path = Paths.get(this.baseWorkDirectory, getName());
		return path.toString();
	}

	public abstract String getName();
	public abstract void process(String workDirectory, String fileName) throws IOException ;
}
