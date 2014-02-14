/**
 * 
 */
package ss.search;

import ss.domainmodel.SearchResultObject;

/**
 * @author roman
 *
 */
public interface ISearchResult {
	
	boolean isComposed();
	
	SearchResultObject getSearchResultObject();
	
	String getComparisonParameter();
	
	boolean isSuch(ISearchResult result);
	
	String getType();
}
