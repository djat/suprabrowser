package ss.lab.dm3.connection.configuration;

public class CantCreateConfigurationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8242506068906554836L;

	public CantCreateConfigurationException() {
		super();
	}

	public CantCreateConfigurationException(String message, Throwable cause) {
		super(message, cause);	}

	public CantCreateConfigurationException(String message) {
		super(message);
	}

	public CantCreateConfigurationException(Throwable cause) {
		super(cause);
	}

	
}
