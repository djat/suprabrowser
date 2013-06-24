package ss.client.networking.protocol.callbacks;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.server.networking.protocol.callbacks.LargePacketEvent;


public class LargePacketDescriptionHandler extends AbstractCallbackHandler<LargePacketEvent>{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LargePacketDescriptionHandler.class);
	
	/**
	 * @param notificationClass
	 * @param client
	 */
	public LargePacketDescriptionHandler(DialogsMainCli client) {
		super(LargePacketEvent.class, client);
	}

	/* (non-Javadoc)
	 * @see ss.client.networking.protocol.callbacks.AbstractCallbackHandlers#handleEvent(ss.client.networking.protocol.callbacks.AbstractEvent)
	 */
	@Override
	protected void handleEvent(LargePacketEvent event) {
		final String name = event.getTitle();
		final int packetId = event.getPacketId();
		if ( logger.isDebugEnabled() ) {
			logger.debug( "We will receive large packet: " + event.getTitle() );
		}
		SphereOpenManager.INSTANCE.setSphereNameWhichLoading(name, packetId);
	}

}
