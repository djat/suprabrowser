/**
 * 
 */
package ss.server.networking.protocol;

import java.io.Serializable;

import ss.client.networking.protocol.AbstractDialogMainCommand;
import ss.framework.networking2.RespondentCommandHandler;
import ss.server.networking.DialogsMainPeer;

/**
 *
 */
public abstract class AbstractDialogMainHandler<C extends AbstractDialogMainCommand, R extends Serializable> extends RespondentCommandHandler<C,R> {

	protected final DialogsMainPeer peer;
	
	/**
	 * @param acceptableCommandClass
	 */
	public AbstractDialogMainHandler(Class<C> acceptableCommandClass, DialogsMainPeer peer ) {
		super(acceptableCommandClass);
		this.peer = peer;
	}

}
