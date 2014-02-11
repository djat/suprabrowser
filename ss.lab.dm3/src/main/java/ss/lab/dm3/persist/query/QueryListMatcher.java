package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.QueryMatcher;


public class QueryListMatcher extends QueryMatcher {

	private List<QueryMatcher> matcherList;
	/**
	 * @param matcherList
	 */
	public QueryListMatcher(List<QueryMatcher> matcherList) {
		this.matcherList = matcherList;
	}

	@Override
	public boolean match(DomainObject obj) {
		for( QueryMatcher matcher : this.matcherList ) {
			if ( matcher.match(obj) ) {
				return true;
			}
		}
		return false;
	}

}
