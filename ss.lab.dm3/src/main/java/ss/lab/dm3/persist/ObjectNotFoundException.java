package ss.lab.dm3.persist;

import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * @author Dmitry Goncharov
 */
public class ObjectNotFoundException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1650243863475241780L;

	/**
	 * @param criteria
	 */
	public ObjectNotFoundException(Query criteria) {
		super( "Can't find object by " + criteria );
	}
	
	/**
	 * @param criteria
	 */
	public ObjectNotFoundException(Class<? extends DomainObject> objClazz, Long id) {
		super( "Can't find object with class " + objClazz + " and id " + id );
	}
	
	public ObjectNotFoundException(QualifiedObjectId<? extends DomainObject> id ) {
		this( id.getObjectClazz(), id.getId() );
	}

	/**
	 * @param string
	 */
	public ObjectNotFoundException(String message ) {
		super( message );
	}
}
