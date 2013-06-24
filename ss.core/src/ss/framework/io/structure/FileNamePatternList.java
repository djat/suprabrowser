package ss.framework.io.structure;

import java.util.List;

import ss.common.ListUtils;

public class FileNamePatternList implements IFileNamePattern {

	private final List<IFileNamePattern> patterns;
	
	/**
	 * @param patterns
	 */
	public FileNamePatternList(final List<IFileNamePattern> patterns) {
		super();
		this.patterns = patterns;
	}

	/* (non-Javadoc)
	 * @see ss.framework.io.structure.IFilePattern#match(java.lang.String)
	 */
	public boolean match(String fileName) {
		for( IFileNamePattern pattern : this.patterns ) {
			if ( pattern.match(fileName)) {
				return true;	
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return ListUtils.allValuesToString( this.patterns );
	}
	
	

}
