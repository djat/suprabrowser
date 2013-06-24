/**
 * 
 */
package ss.client.networking.protocol.getters;

import java.io.Serializable;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.AbstractDialogMainCommand;
import ss.common.UiUtils;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.ReplyObjectHandler;

/**
 *
 */
public abstract class AbstractGetterCommand extends AbstractDialogMainCommand {

	
	public static final int LONG_DM_TIMEOUT = DEFAULT_COMMAND_TIMEOUT * 2;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(AbstractGetterCommand.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3739382841632301661L;

	private static final int CALL_FROM_UI_TIMEOUT = 20000;

	/**
	 * @param cli
	 * @param name
	 */
	public <R extends Serializable> R execute(DialogsMainCli cli, Class<R> returnObjectClass ) {
		return this.execute(cli, returnObjectClass, LONG_DM_TIMEOUT );
	}
	/**
	 * @param cli
	 * @param name
	 */
	public <R extends Serializable> R execute(DialogsMainCli cli, Class<R> returnObjectClass, int timeout ) {
		try {
			UiUtils.checkUnsafeCallFromUi();
			if ( UiUtils.isCallFromUi() && timeout > CALL_FROM_UI_TIMEOUT ) {
				timeout = CALL_FROM_UI_TIMEOUT;
				logger.warn( "Decrease block time out to 20 seconds" );
			}
			return super.execute( cli.getProtocol(), returnObjectClass, timeout );
		} catch (CommandExecuteException ex) {
			logger.error( "Command failed: " + this, ex);
			return null;
		}
	}

	public <R extends Serializable> void beginExecute(DialogsMainCli cli, ReplyObjectHandler<R> replyHandler  ) {
		this.beginExecute(cli.getProtocol(), replyHandler );
	}
	/**
	 * @param cli
	 * @param name
	 */
	public <R extends Serializable> void beginExecute(DialogsMainCli cli, ReplyObjectHandler<R> replyHandler, int timeout ) {
		super.beginExecute(cli.getProtocol(), replyHandler, timeout );
	}
	
	/**
	 * @param cli
	 * @param name
	 */
	public <R extends Serializable> void beginExecuteForUi(DialogsMainCli cli, ReplyObjectHandler<R> replyHandler, int timeout ) {
		beginExecute( cli.getProtocol(), replyHandler, UiUtils.UI_HANDLER_EXECUTOR, timeout );
	}
	
	/**
	 * @param client
	 * @param name
	 */
	public <R extends Serializable> void beginExecuteForUi(DialogsMainCli cli, ReplyObjectHandler<R> replyHandler) {
		this.beginExecute( cli, replyHandler );
	}

	
	
}

