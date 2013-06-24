/**
 * 
 */
package ss.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author roman
 *
 */
public class FormattedResultsBuilder {

	public static List<ISearchResult> formatResults(final List<SimpleSearchResult> results) {
		final List<ISearchResult> formattedResults = new ArrayList<ISearchResult>();
		
		for(SimpleSearchResult result : results) {
			if(!result.isGroupItem()) {
				formattedResults.add(result);
				continue;
			}
			ComposedSearchResult composed = findSuch(formattedResults, result);
			if(composed==null) {
				composed = ComposedSearchResult.createNew(result);
				composed.joinResult(result);
				formattedResults.add(composed);
			} else {
				composed.joinResult(result);
			}
			
		}
		return formattedResults;
	}

	/**
	 * @param result
	 * @return
	 */
	private static ComposedSearchResult findSuch(final List<ISearchResult> formattedResults, 
			final SimpleSearchResult result) {
		for(ISearchResult iRes : formattedResults) {
			if(!iRes.isComposed()) {
				continue;
			}
			if(iRes.isSuch(result)) {
				return (ComposedSearchResult)iRes;
			}
		}
		return null;
	}
	
	
}
