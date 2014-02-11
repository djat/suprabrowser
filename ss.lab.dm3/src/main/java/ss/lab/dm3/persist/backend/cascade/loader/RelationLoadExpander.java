package ss.lab.dm3.persist.backend.cascade.loader;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.persist.DomainObject;


/**
 * @author Dmitry Goncharov
 */
public class RelationLoadExpander<T extends DomainObject> extends ExplicitLoadExpander<T>  {

	private final List<FieldRelationLoadExander> relationLoadExpander = new ArrayList<FieldRelationLoadExander>();
	
	/**
	 * @param objectClazz
	 */
	public RelationLoadExpander(Class<T> objectClazz) {
		super(objectClazz);
		// TODO 
		// 1. get map, 
		// 2. creates relation load expander
	}
	
	public int getRelationCount() {
		return this.relationLoadExpander.size();
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.cascade.loader.ExplicitLoadExpander#expand(ss.lab.dm3.persist.cascade.loader.CascadeLoader, ss.lab.dm3.persist.core.DomainObject)
	 */
	@Override
	public void typedExpand(CascadeLoader loader, T object) {
		for( FieldRelationLoadExander referenceLoader : this.relationLoadExpander ) {
			referenceLoader.expand(loader, object);
		}
	}

}
