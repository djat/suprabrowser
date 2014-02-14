package ss.framework.networking2;

public abstract class ActiveMessageHandlingContext<M extends ActiveMessage> extends
		MessageHandlingContext<M> {

	/**
	 * @param message
	 */
	public ActiveMessageHandlingContext(M message) {
		super(message);
	}	

}
