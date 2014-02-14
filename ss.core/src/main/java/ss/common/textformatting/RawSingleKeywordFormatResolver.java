/**
 * 
 */
package ss.common.textformatting;

import java.util.ArrayList;
import java.util.List;

import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class RawSingleKeywordFormatResolver extends DefaultKeywordFormatResolver {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RawSingleKeywordFormatResolver.class);
	
	public static final RawSingleKeywordFormatResolver INSTANCE = new RawSingleKeywordFormatResolver();
	
	/* (non-Javadoc)
	 * @see ss.common.textformatting.DefaultKeywordFormatResolver#parseImpl(java.lang.String)
	 */
	protected List<String> parseImpl( final String text, final String keyword ) {
		final String keywordTag = getKeywordSingleTag( keyword ); 
		final String result = getSingleResult(text, keywordTag);
		if ( StringUtils.isBlank( result ) ) {
			return null;
		} else {
			final List<String> toReturn = new ArrayList<String>();
			toReturn.add( result );
			return toReturn;
		}
	}

	/**
	 * @param text
	 * @param keywordTag
	 * @return
	 */
	private String getSingleResult(final String text,
			final String keywordTag) {
		final int index = text.toLowerCase().indexOf( keywordTag );
		if ( index == -1 ) {
			if (logger.isDebugEnabled()) {
				logger.debug("No keyword " + keywordTag + " in following text: ");
				logger.debug( text );
			}
			return null;
		}
		String str = text.substring( index + keywordTag.length() );
		if ( StringUtils.isBlank( str ) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Text after keyword is blank");
			}
			return null;
		}
		str = str.trim();
		if (logger.isDebugEnabled()) {
			logger.debug("Text after keyword as following:");
			logger.debug( str );
		}
		String resultType = null;
		if (str.startsWith("\"")) {
			resultType = str.substring( 1 );
			int i = resultType.indexOf("\"");
			if ( i == -1 ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Wrong usage of \" symbol");
				}
				return null;
			}
			resultType = resultType.substring(0, i);
		} else {
			resultType = str;
			int i = resultType.indexOf(" ");
			if ( i == -1 ) {
				i = resultType.indexOf("\n");
			}
			if ( i != -1 ) {
				if (logger.isDebugEnabled()) {
					logger.debug(" Space or curret is found ");
				}
				resultType = resultType.substring(0, i);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Space is not found, all the remaining text is returning");
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "result is : " + resultType );
		}
		return (StringUtils.isNotBlank(resultType) ? resultType.trim(): null);
	}
}
