/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class SupraSearchResultsObject extends XmlEntityObject {

	private final ISimpleEntityProperty keywordQuery = super
	.createAttributeProperty( "keywordQuery/@value" );
	
	private final ISimpleEntityProperty resultsCount = super
	.createAttributeProperty( "resultsCount/@value" );
	
	private final ISimpleEntityProperty totalCount = super
	.createAttributeProperty( "totalCount/@value" );
	
	private final ISimpleEntityProperty pageCount = super
	.createAttributeProperty( "pageCount/@value" );
	
	private final ISimpleEntityProperty pageId = super
	.createAttributeProperty( "pageId/@value" );
	
	private final ISimpleEntityProperty id = super
	.createAttributeProperty( "id/@value" );
	
	private final SearchResultCollection results = super
	.bindListProperty( new SearchResultCollection(), "results" );
	
	public SupraSearchResultsObject() {
		super("root");
	}
	
	public SearchResultCollection getResults() {
		return this.results;
	}
	
	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SupraSearchResultsObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SupraSearchResultsObject.class);
	}

	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SupraSearchResultsObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SupraSearchResultsObject.class);
	}
	
	public void setKeywordsQuery(String value) {
		this.keywordQuery.setValue(value);
	}
	
	public String getKeywordsQuery() {
		return this.keywordQuery.getValue();
	}
	
	public void setResultsCount(int value) {
		this.resultsCount.setIntValue(value);
	}
	
	public int getResultsCount() {
		return this.resultsCount.getIntValue();
	}
	
	public void setTotalCount(int value) {
		this.totalCount.setIntValue(value);
	}
	
	public int getTotalCount() {
		return this.totalCount.getIntValue();
	}
	
	public void setPageCount(int value) {
		this.pageCount.setIntValue(value);
	}
	
	public int getPageCount() {
		return this.pageCount.getIntValue();
	}
	
	public void setPageId(int value) {
		this.pageId.setIntValue(value);
	}
	
	public int getPageId() {
		return this.pageId.getIntValue();
	}
	
	public void setId(int value) {
		this.id.setIntValue(value);
	}
	
	public int getId() {
		return this.id.getIntValue();
	}
}
