package ss.framework.launch;

public class MessageClassifier {

	public enum MessageType {
		OK,
		ERROR,
		FATAL
	}
	/**
	 * @param message
	 * @return
	 */
	public MessageType classify(String message) {
		if ( message.contains( "ERROR" ) ) {
			return MessageType.ERROR;
		}
		else if ( message.contains( "FATAL") ) {
			return MessageType.FATAL;
		}
		return MessageType.OK;
	}

}
