/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.AbstractDialogMainCommand;
import ss.framework.networking2.ReplyHandler;

/**
 * 
 */
public abstract class AbstractAction extends AbstractDialogMainCommand {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3167172815923034215L;

	/**
	 * @param cli
	 */
	public void beginExecute(DialogsMainCli cli) {
		beginExecute( cli.getProtocol() );
	}

	public final void beginExecute(DialogsMainCli cli, ReplyHandler replyHandler) {
		beginExecute(cli.getProtocol(), replyHandler);
	}	
}
