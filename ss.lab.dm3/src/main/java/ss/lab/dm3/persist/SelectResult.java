package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author dmitry
 *
 */
public class SelectResult {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Query query;
	
	private final List<DomainObject> resultList;
	
	private final HashSet<DomainObject> cascadedSet;
	
	private Object generic;
	
	private int resultTotalCount;
	
	/**
	 * @param query
	 */
	public SelectResult(Query query) {
		super();
		this.query = query;
		this.resultList = new ArrayList<DomainObject>();
		this.cascadedSet = new HashSet<DomainObject>();
	}

	public List<DomainObject> getResultList() {
		return this.resultList;
	}

	public HashSet<DomainObject> getCascadedSet() {
		return this.cascadedSet;
	}

	public Query getQuery() {
		return this.query;
	}

	/**
	 * @param string
	 */
	public void debugDump(String message) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( message + " " + this );
		}
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "query", this.query );
		tsb.append( "resultList", this.resultList );
		return tsb.toString();
	}

	public Object getGeneric() {
		return this.generic;
	}

	public void setGeneric(Object generic) {
		this.generic = generic;
	}

	public int getResultTotalCount() {
		return resultTotalCount;
	}

	public void setResultTotalCount(int resultTotalCount) {
		this.resultTotalCount = resultTotalCount;
	}
	
	
}
