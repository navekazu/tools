package tools.filenametransfer;

public abstract class FileNameTransferBase {
	protected String prefixName;

	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	protected String getName(String name) {
		return prefixName==null? name: prefixName+"_"+name;
	}

	public abstract String getName();
	public abstract void process(String directory, String fileName);
}
