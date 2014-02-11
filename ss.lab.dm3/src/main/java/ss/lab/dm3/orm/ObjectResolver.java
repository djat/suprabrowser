package ss.lab.dm3.orm;

/**
 * @author Dmitry Goncharov
 */
public abstract class ObjectResolver implements IObjectResolver {

	public <T extends MappedObject> T resolveManagedOrNull(Class<T> entityClass, Long id) {
		if ( entityClass == null || id == null ) {
			return null;
		}
		final T resolved = resolve(entityClass, id);
		return resolved != null && isObjectManagedByOrm( resolved ) ? resolved : null; 
	}

	public boolean isObjectManagedByOrm(MappedObject object) {
		return object != null;
	}

	public abstract <T extends MappedObject> T resolve(Class<T> entityClass, Long id);

	@SuppressWarnings("unchecked")
	public <T extends MappedObject> QualifiedObjectId<? extends T> getQualifiedObjectId(
			T bean) {
		if ( bean == null ) {
			return null;
		}
		else {
			return new QualifiedObjectId( bean.getClass(), bean.getId() );
		}
	}

	
}
