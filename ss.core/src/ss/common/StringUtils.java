/**
 * 
 */
package ss.common;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ss.client.ui.PreviewHtmlTextCreator;


/**
 *
 */
public final class StringUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StringUtils.class);

	static char [] addtional_symb = new char[] { '1', '2' };
	
	private static String lineSeparator = "\r\n";
	/**
	 * @return
	 */
	public static synchronized String getLineSeparator() {
		if (lineSeparator == null) {
			lineSeparator = System.getProperty( "line.separator", "\r\n" );
		}
		return lineSeparator;
	}
	
	public static String normalizeName( String strValue ) {
		if ( strValue == null ) {
			return null;
		}
		char[] result = new char[ strValue.length() ];
		strValue.getChars( 0, strValue.length(), result, 0 );
		for (int n = 0; n < result.length; n++) {
			result[ n ] = isAllowed( result[ n ] ) ? result[ n ] : '_';
		}
		return new String( result );
	}
	
	private static boolean isAllowed( char ch ) {
		if ( ch >= 'a' && ch <= 'z' ||
			 ch >= 'A' && ch <= 'Z' ||
		     ch >= '0' && ch <= '9' ||
		     ch == '+' || ch == '.' ||
		     ch == '_' ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param str
	 * @return false if str is null or just spaces.
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 
	 * @param str
	 * @return true if str is null or just spaces.
	 */
	public static boolean isBlank(String str) {
		if ( (str == null) || (str.length() == 0) || str.trim().equals("") ) 
			return true;
		return false;
	}
	
	public static String wrapInLapki(String str){
		if (isBlank(str)){
			return null;
		}
		return "\"" + str + "\"";
	}

	/**
	 * @param number
	 * @param faxsetted
	 * @return
	 */
	public static boolean equals( final String str1, final String str2 ) {
		if ((str1 == null)||(str2 == null)){
			return false;
		}
		return str1.equals(str2);
	}
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return false is both is null
	 */
	public static boolean equalsIgnoreCase( final String str1, final String str2 ) {
		if ((str1 == null)||(str2 == null)){
			return false;
		}
		return str1.equalsIgnoreCase( str2 );
	}

	/**
	 * With default separator ", "
	 * @param strings
	 * @return
	 */
	public static String asOneString( final String[] strings ) {
		return asOneString(strings, ", ");
	}
	
	/**
	 * @param strings
	 * @return
	 */
	public static String asOneString( final String[] strings, final String separator ) {
		String returnString = "";
		boolean isInsertSeparator = false;
		final String localSeparator = (separator == null) ? "" : separator;
		for (String s : strings) {
			if (isInsertSeparator) {
				returnString += localSeparator;
			} else {
				isInsertSeparator = true;
			}
			returnString += s;
		}
		return returnString;
	}
	
	public static String cleanUpSearchResultText( final String rawText ){
		if (isBlank( rawText )) {
			return rawText;
		}
		
		String text = rawText;
		 
		//text = PreviewHtmlTextCreator.excludeParagraphs(text);
		//text = PreviewHtmlTextCreator.excludeTag(text, "head");
		//text = PreviewHtmlTextCreator.excludeTag(text, "body");
		//text = PreviewHtmlTextCreator.excludeTag(text, "table");
		text = PreviewHtmlTextCreator.excludeTag(text, "td");
		text = PreviewHtmlTextCreator.excludeTag(text, "tr");
		text = PreviewHtmlTextCreator.excludeTag(text, "br");
		
		return text;
	}

	/**
	 * @param string
	 * @return
	 */
	public static String cutOfTooLongRemainder( final String text, final int limit ) {
		if ( text == null ) {
			return text;
		}
		if ( text.length() <= limit ) {
			return text;
		}
		return ( text.substring(0, limit) + "..." );
	}
	
	public static String getNotNullString( final String str ){
		return ( str != null ) ? str : "";
	}
	
	public static String getTrimmedString(final String str) {
		if(isBlank(str)) {
			return "";
		}
		return str.trim();
	}
	
	public static int safeCompare( final String str1, final String str2 ){
		if ( (str1 == null) && (str2 == null) ) {
			return 0;
		}
		if ( str1 == null ) {
			return -1;
		}
		if ( str2 == null ) {
			return 1;
		}
		return str1.compareTo( str2 );
	}
	
	private static final Pattern NEWLINE_PATTERN = Pattern.compile( "\\r\\n|\\r|\\n" );
	  
	private static final Pattern HTML_TAG_PATTERN = Pattern.compile( "<([A-Z][A-Z0-9]*) ?[^>]*>", Pattern.CASE_INSENSITIVE );
	
	private static final Pattern HTML_TAG_BODY_PATTERN = Pattern.compile( "<body ?[^>]*>(.*?)</body>", Pattern.CASE_INSENSITIVE );
	
	public static String normalizeHtml( String html ) {
		if ( html == null ) {
			return null;
		}
		try {
			final Matcher bodyMatcher = HTML_TAG_BODY_PATTERN.matcher( html );
			if ( bodyMatcher.find() ) {
				String escaped = bodyMatcher.group(1);
				return StringUtils.getNotNullString( escaped );
			}
			final Matcher htmlTagMatcher = HTML_TAG_PATTERN.matcher( html );
			while ( htmlTagMatcher.find() ) {
				if ( isTag( htmlTagMatcher.group( 1 ) ) ) {
					return html;
				}
			}
			final Matcher newLineMatcher = NEWLINE_PATTERN.matcher( html ); 
			return newLineMatcher.replaceAll( "<br>" );	
		} catch (Exception ex) {
			logger.error("Error in normalizing", ex);
		}
		return html;
	}
	
	private final static HashSet<String> TAGS;
	
	static {
		TAGS = new HashSet<String>();
		final String[] tagsList = {"a", "abbr", "acronym", "address", "applet", "area", "b", "base", "basefont", 
				"bdo", "bgsound", "big", "blockquote", "body", "br", "button", "caption", "center", "cite", "code", 
				"col", "colGroup", "comment", "custom", "dd", "del", "dfn", "dir", "div", "dl", "dt", "em", "embed", 
				"fieldset", "font", "form", "frame", "frameset", "head", "hn", "hr", "html", "i", "iframe", "img", 
				"input", "ins", "isindex", "kbd", "label", "legend", "li", "link", "listing", "map", "marquee", 
				"menu", "meta", "nobr", "noframes", "noscript", "object", "ol", "optgroup", "option", "p", "param", 
				"plaintext", "pre", "q", "rt", "ruby", "s", "samp", "script", "select", "small", "span", "strike", 
				"strong", "style", "sub", "sup", "table", "tbody", "td", "textArea", "tfoot", "th", "thead", "title", 
				"tr", "tt", "u", "ul", "var", "wbr", "xml", "xmp"};
		for ( String s : tagsList ) {
			TAGS.add( s );
		}
	}
	
	public static final boolean isTag( final String tag ){
		if ( tag == null ) {
			return false;
		}
		return TAGS.contains( tag.toLowerCase() );
	}
}
