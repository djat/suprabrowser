/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.AbstractAction;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.AbstractDialogMainHandler;

/**
 * @author roman
 * 
 */
public abstract class AbstractActionHandler<A extends AbstractAction>
		extends AbstractDialogMainHandler<A, String> {

	/**
	 * @param acceptableCommandClass
	 * @return
	 */
	public AbstractActionHandler(Class<A> acceptableCommandClass,
			DialogsMainPeer peer) {
		super(acceptableCommandClass, peer );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected final String evaluate(A command) throws CommandHandleException {
		execute( command );
		return "OK";
	}

	/**
	 * @param command
	 */
	protected abstract void execute(A action);
	
}