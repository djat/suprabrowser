package ss.framework.io.structure;

final class ComplexFileNamePattern implements IFileNamePattern {
	
	private final IFileNamePattern exclude;
	
	private final IFileNamePattern include;
		/**
	 * @param include
	 * @param exclude
	 */
	public ComplexFileNamePattern(final IFileNamePattern include, final IFileNamePattern exclude) {
		super();
		this.include = include;
		this.exclude = exclude;
	}

	public boolean match( String fileName ) {
		return !this.exclude.match(fileName) &&
			this.include.match(fileName);
	}

	@Override
	public String toString() {
		return "Exclude: " +this.exclude + ", Include: " + this.include;
		
	}
	
	
}
