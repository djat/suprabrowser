package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.persist.query.QueryList;
import ss.lab.dm3.persist.query.QueryListMatcher;
import ss.lab.dm3.persist.query.TypedQueryMatcher;


/**
 * @author Dmitry Goncharov
 */
public class QueryMatcherFactory {

	public final static QueryMatcherFactory INSTANCE = new QueryMatcherFactory();

	public QueryMatcherFactory() {
	}

	public QueryMatcher create( Query query ) {
		if ( query instanceof TypedQuery ) {
			return new TypedQueryMatcher( (TypedQuery<?>)query );
		}
		else if ( query instanceof QueryList ) {
			QueryList criteriaList = (QueryList) query;
			List<QueryMatcher> matcherList = new ArrayList<QueryMatcher>();
			for( Query subCriteria : criteriaList.getQueries() ) {
				 matcherList.add( create( subCriteria ) );
			}
			return new QueryListMatcher( matcherList );
		} 
		else {
			throw new IllegalArgumentException( "Unsupported critia " + query );
		}
	}
}
