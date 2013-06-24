/**
 * 
 */
package ss.server.install.update;

import ss.framework.install.QualifiedVersion;
import ss.framework.install.update.UpdateHelloCommand;
import ss.framework.install.update.UpdateResponse;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;

/**
 *
 */
public class HelloCommandHandler extends RespondentCommandHandler<UpdateHelloCommand,UpdateResponse> {

	/**
	 * @param acceptableCommandClass
	 */
	public HelloCommandHandler() {
		super(UpdateHelloCommand.class);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected UpdateResponse evaluate(UpdateHelloCommand command) throws CommandHandleException {
		final QualifiedVersion clientVersion = command.getClientVersion();
		final ClientUpdate clientUpdate = ClientUpdateManager.INSTANCE.create( clientVersion );
		if ( clientUpdate != null ) {
			return clientUpdate.createResponse(); 
		}
		else {
			return UpdateResponse.formatUnknown( "Server can't found any information about blank client for " + clientVersion );
		}
	}

}
