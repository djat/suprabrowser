/**
 * 
 */
package ss.framework.io.structure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ss.common.PathUtils;
import ss.framework.text.IReplace;
import ss.framework.text.ReplaceBuilder;

/**
 *
 * Represents file pattern in form <name-pattern>.<extension-pattern>
 * 
 */
class SingleFileNamePattern implements IFileNamePattern {
	
	private static final IReplace PATTERN_NORMALIZER = new ReplaceBuilder()
		.add( '?', '.' )
		.add( '.', "\\." )
		.add( '*', ".*" )
		.getResult();
	
	private final Pattern pathAndNamePattern;
	
	private final Pattern extensionPattern;

	/**
	 * @param pathAndNamePattern
	 * @param extensionPattern
	 */
	public SingleFileNamePattern(String pattern ) {
		super();
		pattern = pattern.toLowerCase();
		this.pathAndNamePattern = convertToRegexp( PathUtils.localPathToUnifiedPath( PathUtils.getPathAndNameWithoutExtension( pattern ) ) );
		this.extensionPattern = convertToRegexp( PathUtils.getExtension( pattern, false ) );
	}
	

	/**
	 * @param fileNameWithourExtension
	 * @return
	 */
	private static Pattern convertToRegexp(String pattern) {
		return Pattern.compile( PATTERN_NORMALIZER.replace( pattern ), Pattern.CASE_INSENSITIVE );
	}


	/* (non-Javadoc)
	 * @see ss.framework.io.structure.IFilePattern#match(java.lang.String)
	 */
	public boolean match( String fileName ) {
		final String pathAndName = PathUtils.getPathAndNameWithoutExtension( fileName  );
		final String extension = PathUtils.getExtension( fileName, false );
		return match( this.pathAndNamePattern, pathAndName ) && match( this.extensionPattern, extension );
	}
	
	/**
	 */
	private static boolean match(Pattern pattern, String name) {
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}


	@Override
	public String toString() {
		return "<" + this.pathAndNamePattern + "><" + this.extensionPattern + ">";
	}

	
}
