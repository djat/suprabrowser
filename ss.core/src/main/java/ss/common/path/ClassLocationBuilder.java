/**
 * 
 */
package ss.common.path;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 */
public class ClassLocationBuilder {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClassLocationBuilder.class);
	
	private static final char DOT = '.';
	private static final char URL_SLASH = '/';
	static final String JAR_SUFFIX = "!";
	static final String JAR_PREFIX = "jar:";
	private static final String CLASS_EXTENSION = ".class";
	
	private final Class clazz;
	
	private final Class compilationUnitClazz;

	private String compilationUnitBaseUri;

	private boolean jar;

	/**
	 * @param clazz
	 */
	public ClassLocationBuilder(final Class clazz) {
		super();
		this.clazz = clazz;
		this.compilationUnitClazz = getCompilationUnitClass(clazz); 
	}
	
	public ClassLocation getResult() throws CantCreateClassLocationException {
		setUpCompilationUnitBaseUri();
		URI uri;
		try {
			uri = new URI( this.compilationUnitBaseUri );
		} catch (URISyntaxException ex) {
			throw new CantCreateClassLocationException( "Can't parse compilation unit uri " + this.compilationUnitBaseUri, ex );
		}
		final File file = new File( uri );
		return new ClassLocation( this.clazz, this.compilationUnitClazz, file.getPath(), uri, this.jar);
	}
	


	/**
	 * @throws CantCreateClassLocationException 
	 * 
	 */
	private void setUpCompilationUnitBaseUri() throws CantCreateClassLocationException {
		final Package clazzPackage = this.compilationUnitClazz.getPackage();
		final String packageUrl = clazzPackage.getName().replace( DOT, URL_SLASH );
		final String clazzFileName = this.compilationUnitClazz.getSimpleName() + CLASS_EXTENSION;
		final String clazzUrl = this.compilationUnitClazz.getResource( clazzFileName ).toString();
		final String clazzRelativePath = packageUrl + URL_SLASH + clazzFileName;
		if (logger.isDebugEnabled()) {
			logger.debug( "Class url " + clazzUrl + ", clazzRelativePath " + clazzRelativePath );
		}
		if ( !endsWithIgnoreCase( clazzUrl, clazzRelativePath ) ) {
			throw new CantCreateClassLocationException( "Can't find compilation unit. Class url " + clazzUrl + ", relative path " + clazzUrl );
		}
		String base = clazzUrl.substring( 0, clazzUrl.length() - clazzRelativePath.length() - 1 );
		if ( startWithIgnoreCase( base, JAR_PREFIX ) && endsWithIgnoreCase( base,JAR_SUFFIX ) ) {
			this.jar = true;
			base = base.substring( JAR_PREFIX.length() );
			this.compilationUnitBaseUri = base.substring( 0, base.length() - JAR_SUFFIX.length() );
		}
		else {
			this.jar = false;
			this.compilationUnitBaseUri = base;
		}
	}
	
	/**
	 * @param clazzFileName
	 * @param clazzRelativePath
	 * @return
	 */
	private static boolean endsWithIgnoreCase(String target, String suffix ) {
		if ( target == null || suffix == null ) {
			return false;
		}
		target = target.toLowerCase();
		suffix = suffix.toLowerCase();
		return target.endsWith( suffix );
	}
	
	/**
	 * @param clazzFileName
	 * @param clazzRelativePath
	 * @return
	 */
	private static boolean startWithIgnoreCase(String target, String prefix ) {
		if ( target == null || prefix == null ) {
			return false;
		}
		target = target.toLowerCase();
		prefix = prefix.toLowerCase();
		return target.startsWith( prefix );
	}
	
	private static Class getCompilationUnitClass( Class clazz ) {
		while( clazz != null ) {
			if ( clazz.getDeclaringClass() == null ) {
				return clazz;
			}
			clazz = clazz.getDeclaringClass();
		}
		return clazz;
	}
}
