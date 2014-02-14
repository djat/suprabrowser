/**
 * 
 */
package ss.common.textformatting;

import java.util.List;

import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public abstract class DefaultKeywordFormatResolver {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DefaultKeywordFormatResolver.class);
	
	public final List<String> parse( final String text, final String keyword ) {
		if ( StringUtils.isBlank( text ) ) {
			logger.error("text is null");
			return null;
		}
		if ( StringUtils.isBlank( keyword ) ) {
			logger.error("keyword is null");
			return null;
		}
		return parseImpl( text, keyword );
	}
	
	protected String getKeywordSingleTag( final String keyword ){
		return "[ss." + keyword + ":]";
	}
	
	protected String getKeywordOpenTag( final String keyword ){
		return "[ss." + keyword + "]";
	}

	protected String getKeywordCloseTag( final String keyword ){
		return "[/ss." + keyword + "]";
	}

	protected abstract List<String> parseImpl( final String text, final String keyword );
}
