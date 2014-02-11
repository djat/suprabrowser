package ss.lab.dm3.persist.backend.cascade.loader;

import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 */
public class FieldRelationLoadExander implements ILoadExpander {

	private final Property<Long> referenceAccessor;
	
	/**
	 * @param referenceProperty
	 */
	public FieldRelationLoadExander(Property<Long> referenceProperty) {
		super();
		this.referenceAccessor = referenceProperty;
	}

	public void expand( CascadeLoader loader, DomainObject object ) {
		// TODO data reference accessor
		Class<? extends DomainObject> targetClass = null;
		Long targetId = this.referenceAccessor.getValue(object);
		loader.addToExpansion( targetClass, targetId );
	}
	
}
