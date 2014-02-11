package ss.lab.dm3.persist.model;

import java.util.HashMap;

import ss.lab.dm3.orm.query.ExpressionHelper;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.TypedQuery;

public class MatchBoundsCollector {

	private HashMap<Class<? extends DomainObject>, TypedQuery<?>> classToQuery = new HashMap<Class<? extends DomainObject>, TypedQuery<?>>(); 
	
	public void add(TypedQuery<?> query) {
		final Class<? extends DomainObject> entityClass = query.getEntityClass();
		TypedQuery<?> existedQuery = this.classToQuery.get(entityClass);
		if ( existedQuery != null ) {
			existedQuery.setRestriction( ExpressionHelper.or( existedQuery.getRestriction(), query.getRestriction() ) ); 
		}
		else {
			this.classToQuery.put( entityClass, query );
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.classToQuery.isEmpty();
	}

	/**
	 * @return
	 */
	public MatchBounds createMatchBounds() {
		// TODO Implement query matcher instancing
		return null;
	}
}
