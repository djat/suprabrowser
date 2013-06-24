package ss.server.domainmodel2.db.statements;

/**
 * 
 */
public final class IllegalThreadAccessException extends
		RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7668806321594486922L;

	/**
	 * @param executor
	 */
	public IllegalThreadAccessException(Thread thread,
			StatementExecutor executor) {
		super("Illegal thread " + thread.getName() + " for " + executor);
	}

}