package ss.framework.networking2;

public class ReplyVoidHandler extends ReplyHandler {

	/* (non-Javadoc)
	 * @see ss.common.networking2.ReplyHandler#commandSuccessfullyExecuted(ss.common.networking2.Command, ss.common.networking2.SuccessReply)
	 */
	@Override
	protected final void commandSuccessfullyExecuted(Command command, SuccessReply successReply) {
		commandExecuted();
	}	

	/**
	 * 
	 */
	protected void commandExecuted() {		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for (void)" ;
	}
}
