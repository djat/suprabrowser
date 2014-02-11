package ss.lab.dm3.persist.backend.hibernate;


import ss.lab.dm3.persist.DomainObject;

/**
 * @deprecated Use DomainObject instead
 * @author Dmitry Goncharov
 * 
 */
@Deprecated
public abstract class DataObject {}

//public abstract class DataObject extends DomainObject {
//
//	/**
//	 * 
//	 */
//	public DataObject() {
//		super();
//	}
//
//	/**
//	 * @param id
//	 */
//	public DataObject(Long id) {
//		super(id);
//	}
//
//	/**
//	 * @param objectClazz
//	 * @return
//	 */
//	@Deprecated
//	public static <T extends DataObject> T createNew(Class<T> objectClazz) {
//		throw new UnsupportedOperationException( "This operation is not longer supported");
//	}
//	
//}
