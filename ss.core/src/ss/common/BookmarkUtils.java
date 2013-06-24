package ss.common;

public class BookmarkUtils {

	/**
	 * 
	 */
	private static final String HTTP = "http://";

	/**
	 * 
	 */
	private static final String HTTP_WWW = "http://www.";

	/**
	 * 
	 */
	private static final String EMPTY_STR = "";
	
	private static final String [] PREFIXES = new String[] {
		HTTP_WWW, "www.", HTTP, "http:"  
	};
	
	public static String getSignificantPart( String url ) {
		if ( url == null ) {
			return null;
		}
		if ( url.length() == 0 ) {
			return EMPTY_STR;
		}
		for( String prefix : PREFIXES ) {
			if ( url.startsWith( prefix ) ) {
				return url.substring( prefix.length() );
			}
		}
		return url;
	}
	
	public static boolean isStartOfUrl( String potentialUrl ) {
		for( String prefix : PREFIXES ) {
			if ( potentialUrl.startsWith( prefix ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param filter
	 * @return
	 */
	public static String toHttpWwwUrl(String url) {		
		if ( url == null ) {
			return null;
		}
		return HTTP_WWW + getSignificantPart(url);
	}
	
	public static String toHttpUrl(String url) {
		if ( url == null ) {
			return null;
		}
		return HTTP + getSignificantPart(url);
	}
}
