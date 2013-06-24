/**
 * 
 */
package ss.client.event.tagging;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author zobo
 *
 */
public class InfoOnRelatedKeywordsData implements Serializable {

	private static final long serialVersionUID = -5754969359648628798L;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InfoOnRelatedKeywordsData.class);
	
	private final Hashtable<String, Integer> keywordsCounts;
	
	private final Hashtable<String, Integer> bookmarksCount;
	
	public InfoOnRelatedKeywordsData(){
		this.keywordsCounts = new Hashtable<String, Integer>();
		this.bookmarksCount = new Hashtable<String, Integer>();
	}

	public void setKeywordsCountForTag( final String uniqueId, final int count ){
		if ( uniqueId == null ) {
			logger.error("uniqueId is null");
			return;
		}
		this.keywordsCounts.put( uniqueId , new Integer( count ) );
	}
	
	public Integer getKeywordsCountForTag( final String uniqueId ){
		if ( uniqueId == null ) {
			logger.error("uniqueId is null");
			return null;
		}
		return this.keywordsCounts.get( uniqueId );
	}
	
	public void setBookmarksCountForTag( final String uniqueId, final int count ){
		if ( uniqueId == null ) {
			logger.error("uniqueId is null");
			return;
		}
		this.bookmarksCount.put( uniqueId , new Integer( count ) );
	}
	
	public Integer getBookmarksCountForTag( final String uniqueId ){
		if ( uniqueId == null ) {
			logger.error("uniqueId is null");
			return null;
		}
		return this.bookmarksCount.get( uniqueId );
	}
}
