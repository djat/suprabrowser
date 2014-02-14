package ss.common;

public class ArgumentNullPointerException extends NullPointerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4372163286252059428L;

	public ArgumentNullPointerException( String argumentName ) {
        super( argumentName + " is null." );    
    }
}
