package ss.client.networking.protocol.callbacks;

import ss.client.networking.DialogsMainCli;
import ss.framework.networking2.EventHandler;
import ss.framework.networking2.EventHandlingContext;
import ss.server.networking.protocol.callbacks.AbstractEvent;

public abstract class AbstractCallbackHandler<E extends AbstractEvent> extends EventHandler<E> {

	protected final DialogsMainCli client;
	
	/**
	 * @param notificationClass
	 * @param client
	 */
	public AbstractCallbackHandler(Class<E> notificationClass, final DialogsMainCli client) {
		super(notificationClass);
		this.client = client;
	}

	@Override
	protected final void handleEvent(EventHandlingContext<E> context) {
		handleEvent(context.getMessage());
	}

	protected abstract void handleEvent(E event);

}
