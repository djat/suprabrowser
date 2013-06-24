package ss.client.networking.protocol.callbacks;

import ss.client.networking.DialogsMainCli;
import ss.common.ReflectionUtils;
import ss.framework.networking2.Protocol;

public class CallbackRegistrator {

	private final DialogsMainCli client;

	private final Protocol protocol;

	/**
	 * @param peer
	 * @param protocol
	 */
	public CallbackRegistrator(final DialogsMainCli client,
			final Protocol protocol) {
		super();
		this.client = client;
		this.protocol = protocol;
	}

	public void registerHandlers() {
		register( LargePacketDescriptionHandler.class );
		register( ClubDealsDataChanged.class );
	}

	/**
	 * @param handler
	 */
	protected final void register(
			Class<? extends AbstractCallbackHandler> handlerClass) {
		AbstractCallbackHandler handler = ReflectionUtils.create(handlerClass,
				this.client);
		this.protocol.registerHandler(handler);
	}
}
