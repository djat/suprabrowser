package ss.framework.errorreporting.network.handlers;

import ss.framework.errorreporting.CantCreateSessionException;
import ss.framework.errorreporting.ILogStorage;
import ss.framework.errorreporting.SessionInformation;
import ss.framework.errorreporting.network.InitializeCommand;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;

public class InitializeCommandHandler extends RespondentCommandHandler< InitializeCommand,SessionInformation>{

	private final ILogStorage logStorage;
	
	/**
	 * @param logStorage
	 */
	public InitializeCommandHandler( ILogStorage logStorage) {
		super(InitializeCommand.class);
		this.logStorage = logStorage;
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected SessionInformation evaluate(InitializeCommand command) throws CommandHandleException {
		try {
			return this.logStorage.createSession( command );
		} catch (CantCreateSessionException ex) {
			throw new CommandHandleException( ex );
		}		
	}

}
