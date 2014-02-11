package ss.lab.dm3.persist.backend.search;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.query.LuceneExpression;

/**
 * Lucene search query 
 * 
 * @author Dmitry Goncharov
 *
 */
public class SearchQueryAdaptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4050107867023358911L;

	private LuceneExpression restriction;

	private TypedQuery<?> typedQuery;
	
	public SearchQueryAdaptor(TypedQuery<?> typedQuery) {
		this.typedQuery = typedQuery;
		this.restriction = (LuceneExpression) typedQuery.getRestriction();
	}

	public String getText() {
		return this.restriction.getText();
	}
	
	public int getLimitSize() {
		return this.typedQuery.getLimitSize();
	}

	public int getLimitOffset() {
		return this.typedQuery.getLimitOffset();
	}

	public Class<? extends DomainObject> getClassDomainObject() {
		return this.typedQuery.getEntityClass();
	}

	public SecureLockCollector getSecureKeys() {
		return typedQuery.getSecureKeys();
	}

	public boolean isSecure() {
		return typedQuery.isSecure();
	}
	
	
}