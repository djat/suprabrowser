/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.collections.KeywordItemCollection;

/**
 * @author roman
 *
 */
public class KeywordsObject extends DomainObject {

	private long uniqueId;
	
	private int numberOfTags;
	
	private int numberWithThisTag;
	
	private final KeywordItemCollection keywords = new KeywordItemCollection();

	/**
	 * @return the uniqueId
	 */
	public long getUniqueId() {
		return this.uniqueId;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the numberOfTags
	 */
	public int getNumberOfTags() {
		return this.numberOfTags;
	}

	/**
	 * @param numberOfTags the numberOfTags to set
	 */
	public void setNumberOfTags(int numberOfTags) {
		this.numberOfTags = numberOfTags;
	}

	/**
	 * @return the numberWithThisTag
	 */
	public int getNumberWithThisTag() {
		return this.numberWithThisTag;
	}

	/**
	 * @param numberWithThisTag the numberWithThisTag to set
	 */
	public void setNumberWithThisTag(int numberWithThisTag) {
		this.numberWithThisTag = numberWithThisTag;
	}

	/**
	 * @return the keywords
	 */
	public KeywordItemCollection getKeywords() {
		return this.keywords;
	}
}
