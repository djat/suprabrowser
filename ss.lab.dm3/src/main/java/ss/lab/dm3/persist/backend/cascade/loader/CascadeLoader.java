package ss.lab.dm3.persist.backend.cascade.loader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.ObjectCollector;
import ss.lab.dm3.persist.backend.hibernate.ObjectSelector;

/**
 * @author Dmitry Goncharov
 */
public class CascadeLoader {

	private final ObjectSelector objectSelector = null;
	
	private final List<QualifiedObjectId<? extends DomainObject>> expansion = new ArrayList<QualifiedObjectId<? extends DomainObject>>();

	private final Hashtable<Class<?>, ExplicitLoadExpander<?>> expanders = new Hashtable<Class<?>, ExplicitLoadExpander<?>>();

	private ObjectCollector objectCollector;

	public void addExpander(ExplicitLoadExpander<?> expander) {
		this.expanders.put( expander.getObjectClazz(), expander );
	}
	
	public ObjectCollector load( ss.lab.dm3.persist.Query dm3Criteria ) {
		final Iterable<DomainObject> initialObjectSet = this.objectSelector.select( dm3Criteria );
		return expand(initialObjectSet);
	}
	
	public ObjectCollector load( org.hibernate.Criteria hibCriteria ) {
		final Iterable<DomainObject> initialObjectSet = this.objectSelector.select( hibCriteria );
		return expand(initialObjectSet);
	}

	/**
	 * @param initialObjectSet
	 * @return
	 */
	public ObjectCollector expand(final Iterable<DomainObject> initialObjectSet) {
		this.objectCollector = new ObjectCollector(this.objectSelector);
		for( DomainObject object : initialObjectSet ) {
			addAndExpand(object);
		}
		while( this.expansion.size() > 0 ) {
			// Copy expansion to active expansion and clear expansion
			List<QualifiedObjectId<? extends DomainObject>> activeExpansion = new ArrayList<QualifiedObjectId<? extends DomainObject>>( this.expansion );
			this.expansion.clear();
			for( QualifiedObjectId<? extends DomainObject> id : activeExpansion ) {
				DomainObject object = this.objectSelector.select( id );
				addAndExpand(object);
			}
		}
		return this.objectCollector;
	}
	
	/**
	 * @param object
	 */
	private void addAndExpand(DomainObject object) {
		if (object == null) {
			throw new NullPointerException( "object" );
		}
		// First check access
//		if (this.accessChecker != null) {
//			if (!this.accessChecker.isAccessible(this.accessContext, object)) {
//				this.objectCollector.block( object.createQualifiedId() );
//				return;
//			}
//		}
		// Add object to collector, if collector does not have it than expand load
		if ( this.objectCollector.add(object) ) {
			// Expand load by object
			ExplicitLoadExpander<?> expander = this.expanders
					.get(object.getEntityClass());
			if (expander != null) {
				expander.expand(this, object);
			}
		}
	}

	/**
	 * @param targetClass
	 * @param targetId
	 */
	void addToExpansion(Class<? extends DomainObject> targetClass,
			Long targetId) {
		QualifiedObjectId<? extends DomainObject> qualifiedObjectId = QualifiedObjectId
				.create(targetClass, targetId);
		if ( !this.objectCollector.contains(qualifiedObjectId) ) {
			this.expansion.add(qualifiedObjectId);
		}
	}

}
