package ss.lab.dm3.persist.model;

import java.util.HashMap;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.IObjectMatcher;
import ss.lab.dm3.persist.query.TypedQueryMatcher;

public class MatchBounds implements IObjectMatcher {

	private final HashMap<Class<? extends DomainObject>, TypedQueryMatcher> classToQueryMatcher; 
	
	
	/**
	 * @param classToQueryMatcher
	 */
	public MatchBounds(HashMap<Class<? extends DomainObject>, TypedQueryMatcher> classToQueryMatcher) {
		super();
		this.classToQueryMatcher = classToQueryMatcher;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends DomainObject>[] getInvolvedClasses() {
		// TODO [dg] Should we expand involved classes by superclasses? 
		return this.classToQueryMatcher.keySet().toArray( new Class[ this.classToQueryMatcher.size() ] );
	}

	/**
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean match(DomainObject object) {
		if ( object == null ) {
			return false;
		}
		else {
			// TODO may be replace hashmap by list with foreach? 
			Class<? extends DomainObject> objectClazz = object.getEntityClass();
			for(;;) {
				TypedQueryMatcher queryMatcher = this.classToQueryMatcher.get( objectClazz );
				if ( queryMatcher != null && queryMatcher.match( object ) ) {
					return true;
				}				
				Class<?> superClazz = objectClazz.getSuperclass();
				if ( objectClazz != DomainObject.class &&
					 superClazz != DomainObject.class ) {
					objectClazz = (Class<? extends DomainObject>) superClazz;
				}				
				else {
					return false;
				}
			}
		}
	}

}
