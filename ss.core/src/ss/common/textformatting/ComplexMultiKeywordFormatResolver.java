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
public class ComplexMultiKeywordFormatResolver extends DefaultKeywordFormatResolver {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ComplexMultiKeywordFormatResolver.class);
	
	public static final ComplexMultiKeywordFormatResolver INSTANCE = new ComplexMultiKeywordFormatResolver();
	
	/* (non-Javadoc)
	 * @see ss.common.textformatting.DefaultKeywordFormatResolver#parseImpl(java.lang.String, java.lang.String)
	 */
	protected List<String> parseImpl( final String text, final String keyword ) {
		if (logger.isDebugEnabled()) {
			logger.debug( "ComplexMultiKeywordFormatResolver called for text: " + text );
		}
		final String openTag = getKeywordOpenTag( keyword );
		final String closeTag = getKeywordCloseTag( keyword );
		if (logger.isDebugEnabled()) {
			logger.debug( "openTag : " + openTag + ", closeTag : " + closeTag );
		}
		final List<String> results = new ArrayList<String>();
		parseNext(text, results, openTag, closeTag);
		if (logger.isDebugEnabled()) {
			logger.debug("results size: " + results.size());
			int i = 0;
			for (String r : results) {
				logger.debug("result " + (++i) +": " + r);
			}
		}
		return results;
	}
	
	private void parseNext( final String text, final List<String> results, final String openTag, final String closeTag ){
		if ( StringUtils.isBlank( text ) ) {
			if (logger.isDebugEnabled()) {
				logger.debug( "Text is null, returning" );
			}
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "Next remainder: " + text );
		}
		final String loweredText = text.toLowerCase();
		int beginIndex = loweredText.indexOf( openTag );
		int endIndex = loweredText.indexOf( closeTag );
		if (logger.isDebugEnabled()) {
			logger.debug("beginIndex: " + beginIndex + ", beginIndex: " + beginIndex);
		}
		if ( (beginIndex == -1) || (endIndex == -1) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("One of tags is not present, returning");
			}
			return;
		}
		beginIndex += openTag.length();
		if (logger.isDebugEnabled()) {
			logger.debug( "Increased beginIndex : " + beginIndex );
		}
		if (endIndex > beginIndex) {
			final String result = text.substring(beginIndex, endIndex);
			if (logger.isDebugEnabled()) {
				logger.debug("Obtained result: " + result);
			}
			if (StringUtils.isNotBlank( result )){
				results.add(result);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(" endIndex <= beginIndex, strange ");
			}
		}
		if (!isMultiLoad()) {
			return;
		}
		endIndex += closeTag.length();
		if (logger.isDebugEnabled()) {
			logger.debug( "Increased endIndex : " + endIndex );
		}
		final String nextText = text.substring(endIndex);
		if (logger.isDebugEnabled()) {
			logger.debug("text was: " + text);
			logger.debug("text now: " + nextText);
		}
		parseNext( nextText, results, openTag, closeTag );
	}

	/**
	 * @return
	 */
	protected boolean isMultiLoad() {
		return true;
	}
}
