package ss.lab.dm3.persist.backend.cascade.loader;

import ss.lab.dm3.persist.DomainObject;


/**
 * @author Dmitry Goncharov
 */
public abstract class ExplicitLoadExpander<T extends DomainObject> implements ILoadExpander {

	private final Class<T> objectClazz;

	/**
	 * @param objectClazz
	 */
	public ExplicitLoadExpander(Class<T> objectClazz) {
		super();
		this.objectClazz = objectClazz;
	}
	
	public void expand(CascadeLoader loader, DomainObject object) {
		T typedObject = this.objectClazz.cast( object );
		typedExpand(loader, typedObject);
	}
	
	protected abstract void typedExpand( CascadeLoader loader, T object );
	
	public Class<T> getObjectClazz() {
		return this.objectClazz;
	}
	
}
