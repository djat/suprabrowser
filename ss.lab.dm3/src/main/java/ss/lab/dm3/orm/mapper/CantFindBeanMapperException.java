package ss.lab.dm3.orm.mapper;

/**
 * @author Dmitry Goncharov
 */
public class CantFindBeanMapperException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3788343876023345622L;

	/**
	 * @param message
	 */
	public CantFindBeanMapperException(Long mapId) {
		super( "Can't find mapper by id " + mapId );
	}

	/**
	 * @param objClazz
	 */
	public CantFindBeanMapperException(
			Class<?> objClazz) {
		super( "Can't find mapper by class " + objClazz );
	}

	
}
