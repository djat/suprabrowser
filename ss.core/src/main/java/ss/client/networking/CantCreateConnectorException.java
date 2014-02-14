package ss.client.networking;

/**
 *
 */
public class CantCreateConnectorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4879512165075232763L;

	/**
	 * @param message
	 */
	public CantCreateConnectorException() {
		super( "Can't create SupraProtocolConnector");
	}
}