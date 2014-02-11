package ss.lab.dm3.utils;


/**
 * @author Dmitry Goncharov
 *
 */
public class CantResolveClassException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8337096801599936004L;

	/**
	 * @param domainObjectClassName
	 * @param ex
	 */
	public CantResolveClassException(String domainObjectClassName,
			ClassNotFoundException ex) {
		super( "Can't find domain object class by " + domainObjectClassName );
	}

	/**
	 * @param objClazz
	 */
	public CantResolveClassException(Class<?> expClass, Class<?> objClazz ) {
		super( objClazz + " is not subclass of " + expClass );
	}

	/**
	 * @param objectName
	 */
	public CantResolveClassException(String objectName, Iterable<Package> packazes ) {
		super( "Can't find class for object name " + objectName + " in " + toString(packazes) );
	}

	/**
	 * @param packazes
	 * @return
	 */
	private static String toString(Iterable<Package> packazes) {
		StringBuilder sb = new StringBuilder();
		for( Package packaze : packazes ) {
			if ( sb.length() > 0 ) {
				sb.append( " " );
			}
			sb.append( packaze );
		}
		return sb.toString();
	}
}
