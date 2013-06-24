/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.io.Serializable;

import ss.client.networking.protocol.AbstractDialogMainCommand;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.AbstractDialogMainHandler;

/**
 *
 */
public abstract class AbstractGetterCommandHandler<C extends AbstractDialogMainCommand, R extends Serializable> extends AbstractDialogMainHandler<C,R> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public AbstractGetterCommandHandler(Class<C> acceptableCommandClass, DialogsMainPeer peer) {
		super(acceptableCommandClass, peer);
	}


}
