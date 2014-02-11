package ss.lab.dm3.orm;

/**
 * @author Dmitry Goncharov
 */
public interface IObjectResolver {	 

	/**
	 * 
	 * Returns object or proxy object (proxy object is unmanaged) by class and id. 
	 * 
	 * Returns null if entityClass OR id is null.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param id
	 * @return
	 */
	<T extends MappedObject> T resolve(Class<T> entityClass, Long id);
	
	/**
	 * Returns managed object or null if no managed object found
	 * 
	 * Returns null if entityClass OR id is null.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param id
	 * @return
	 */
	<T extends MappedObject> T resolveManagedOrNull(Class<T> entityClass, Long id);	
	
	boolean isObjectManagedByOrm(MappedObject object);
	
	<T extends MappedObject> QualifiedObjectId<? extends T> getQualifiedObjectId(T bean);
}
