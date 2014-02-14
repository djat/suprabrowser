/**
 * 
 */
package ss.common;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SearchSettings implements Serializable {

	private static final Logger logger = SSLogger.getLogger(SearchSettings.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 3702876731897490462L;

	private boolean isAnonymous = false;
	
	private boolean searchOnlyTagged = false;
	
	private String stringQuery;
	
	private Query query;
	
	public SearchSettings(Query query, String sQuery, boolean isAnonymous, boolean searchOnlyTagged) {
		this.stringQuery = sQuery;
		this.query = query;
		this.isAnonymous = isAnonymous;
		this.searchOnlyTagged = searchOnlyTagged;
	}

	/**
	 * @return the stringQuery
	 */
	public String getStringQuery() {
		return this.stringQuery;
	}



	/**
	 * @return the isAnonymous
	 */
	public boolean isAnonymous() {
		return this.isAnonymous;
	}

	/**
	 * @return the searchOnlyTagged
	 */
	public boolean isSearchOnlyTagged() {
		return this.searchOnlyTagged;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return this.query;
	}
	
	public void setQuery(final Query query) {
		this.query = query;
	}

	/**
	 * @param reader
	 */
	public void rewriteQuery(IndexReader reader) {
		try {
			this.query = this.query.rewrite(reader);
		} catch (IOException ex) {
			logger.warn("Can't rewrite query", ex);
		}
	}
}