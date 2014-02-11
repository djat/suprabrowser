package ss.lab.dm3.security2;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author Dmitry Goncharov
 */
public class SecurityIdentifierProvider {

//	private ObjectMapperManager<DomainObject> domainObjectMapperManager = null;	
//	private ObjectMapperManager<DataObject> dataObjectMapperManager = null;
	
	/**
	 * @param object
	 */
	public SecurityId getSecurityId(Object object) {
		if ( object == null ) {
			return null;
		}
		if ( object instanceof Class ) {
			return new SecurityId( ((Class<?>) object).getName(), null ); 
		}
		// REFACTOR have a problem with object.getClass()
		else if ( object instanceof MappedObject ) {
			Long id = ((MappedObject) object).getId();
			return new SecurityId( object.getClass().getName(), id );
		}
		else {
			return new SecurityId( object.getClass().getName(), null );
		}
	}

}
