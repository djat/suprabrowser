/**
 * 
 */
package ss.common.textformatting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zobo
 * Keyword 
 */
public class SimpleBeginningFormatResolver extends DefaultKeywordFormatResolver {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SimpleBeginningFormatResolver.class);
	
	/* (non-Javadoc)
	 * @see ss.common.textformatting.DefaultKeywordFormatResolver#parseImpl(java.lang.String, java.lang.String)
	 */
	protected List<String> parseImpl( final String text, final String keyword ) {
		if (logger.isDebugEnabled()) {
			logger.debug( "SimpleBeginningFormatResolver called for text: " + text );
		}
		final int index = text.indexOf(":");
		if ( index != -1 ) {
			final String first = text.substring(0, index);
			if (logger.isDebugEnabled()) {
				logger.debug("First is : " + first);
			}
			if ((first != null) && (first.trim().equalsIgnoreCase( keyword ))){
				if (logger.isDebugEnabled()) {
					logger.debug("It equals to keyword: " + keyword);
				}
				final String second = text.substring( index+1 );
				if (logger.isDebugEnabled()) {
					logger.debug("second is : " + second);
				}
				if (second != null) {
					final List<String> results = new ArrayList<String>();
					results.add( second.trim() );
					return results;
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("It does not equals to keyword: " + keyword);
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No command keyword in the text");
			}
		}
		return null;
	}

}
