/**
 * 
 */
package ss.search;

import java.util.ArrayList;
import java.util.List;

import ss.common.StringUtils;
import ss.domainmodel.IdItem;
import ss.domainmodel.SearchResultObject;

/**
 * @author roman
 *
 */
public class ComposedSearchResult implements ISearchResult {

	private List<SimpleSearchResult> list = new ArrayList<SimpleSearchResult>();
	
	private final String subject;
	
	private final String type;
	
	public ComposedSearchResult(SimpleSearchResult result) {
		this.subject = result.getComparisonParameter();
		this.type = result.getType();
	}
	
	public void joinResult(SimpleSearchResult searchResult) {
		this.list.add(searchResult);
	}
	
	public Iterable<SimpleSearchResult> getResults() {
		return this.list;
	}

	public boolean isComposed() {
		return true;
	}
	
	public boolean isSuch(final ISearchResult result) {
		return getComparisonParameter().equals(result.getComparisonParameter())
				&& result.getType().equals(getType());
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getComparisonParameter() {
		return this.subject;
	}

	public SearchResultObject getSearchResultObject() {
		SearchResultObject resultObject = new SearchResultObject();
		
		if(this.list.size()>0) {
			SimpleSearchResult simpleResult = this.list.get(0);
			resultObject.setSubject(simpleResult.getFormattedSubject());
			resultObject.setAddress(simpleResult.getAddress());
			resultObject.setBody(simpleResult.getFormattedBody());
			resultObject.setComment(simpleResult.getFormattedComment());
			resultObject.setContact(simpleResult.getFormattedContact());
			resultObject.setContent(simpleResult.getFormattedContent());
			resultObject.setGiver(simpleResult.getGiver());
			resultObject.setKeywords(simpleResult.getFormattedKeywords());
			resultObject.setType(simpleResult.getType());
		}
		
		for(SimpleSearchResult result : this.list) {
			String role = result.getRole();
			if (StringUtils.isNotBlank(role)) {
				String existedRole = resultObject.getRole();
				if (StringUtils.isNotBlank(existedRole)) {
					role = existedRole + ", " + role;
				}
				resultObject.setRole(role);
			}
			IdItem item = new IdItem();
			item.setMessageId(result.getMessageId());
			item.setSphereId(result.getSphereId());
			resultObject.getIdCollection().add(item);
		}
		return resultObject;
	}

	/**
	 * @param subject2
	 * @return
	 */
	public static ComposedSearchResult createNew(SimpleSearchResult result) {
		return new ComposedSearchResult(result);
	}
}
