package ss.framework.io.structure;

public class NothingMatchFileNamePattern implements IFileNamePattern {
	public final static NothingMatchFileNamePattern INSTANCE = new NothingMatchFileNamePattern();

	private NothingMatchFileNamePattern() {
	}

	/* (non-Javadoc)
	 * @see ss.framework.io.structure.IFilePattern#match(java.lang.String)
	 */
	public boolean match(String fileName) {
		return false;
	}

	@Override
	public String toString() {
		return "[NOTHING]";
	}
	
	
}
