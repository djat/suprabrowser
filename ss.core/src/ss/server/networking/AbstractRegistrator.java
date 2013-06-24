/**
 * 
 */
package ss.server.networking;

import ss.common.ReflectionUtils;
import ss.framework.networking2.Protocol;
import ss.server.networking.protocol.AbstractDialogMainHandler;

/**
 * @author roman
 *
 */
public abstract class AbstractRegistrator {
	
	private final DialogsMainPeer peer;
	
	private final Protocol protocol;
	
	/**
	 * @param peer
	 * @param protocol
	 */
	public AbstractRegistrator(final DialogsMainPeer peer, final Protocol protocol) {
		super();
		this.peer = peer;
		this.protocol = protocol;
	}

	public abstract void registerHandlers();

	/**
	 * @param handler
	 */
	protected final void register(Class<? extends AbstractDialogMainHandler> handlerClass) {
		AbstractDialogMainHandler handler = ReflectionUtils.create( handlerClass, this.peer );
		this.protocol.registerHandler(handler);
	}

}
