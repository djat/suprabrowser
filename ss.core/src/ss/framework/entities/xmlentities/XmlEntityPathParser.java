package ss.framework.entities.xmlentities;

import ss.common.ArgumentNullPointerException;

public class XmlEntityPathParser {

	/**
	 * Parse xpath
	 * @param path
	 * @return
	 */
	public static final String[] parsePath(final String path, final boolean attributePath ) {
		if (path == null) {
			throw new ArgumentNullPointerException(path);
		}
		final String[] pathParts = path.split("/");
		if ( pathParts.length < 1 ) {
			throw new IllegalArgumentException( "path is empty" );
		}
		final String lastPart = pathParts[ pathParts.length - 1 ];
		boolean lastPathPartIsAttribute = lastPart.startsWith( "@" );
		if ( lastPathPartIsAttribute ) {
			pathParts[ pathParts.length - 1 ] = lastPart.substring(1); 
		}
		if ( attributePath != lastPathPartIsAttribute ) {
			throw new IllegalArgumentException( "invalid path " + path + " expected attributePath "+ attributePath );
		}		
		return pathParts;
	}

}
