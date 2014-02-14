package ss.framework.entities;

import ss.common.UnexpectedRuntimeException;

public class UnexpectedItemClassException extends UnexpectedRuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8162218498796694071L;

	public UnexpectedItemClassException( IEntityObject collection, Class expected, Class actual ) {
        super( String.format( "Unexcpected item class for collection %s. Expected %s. Actual %s", collection.getClass(), expected, actual ) );
    }
}
