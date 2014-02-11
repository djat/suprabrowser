package ss.lab.dm3.persist.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.persist.Query;


/**
 * @author Dmitry Goncharov
 */
public class QueryList extends Query {

	
	private final Query[] queries;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8442534905969096575L;
	
	/**
	 * @param queries
	 */
	public QueryList(Query[] queries) {
		this.queries = queries;
	}

	public Query[] getQueries() {
		return this.queries;
	}

	@Override
	public String toString() {
		final ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "queries", this.queries );		
		return tsb.toString();
	}

	
}
