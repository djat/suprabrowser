package ss.server.domainmodel2.db.statements;

/**
 * 
 */
public final class NotInUseException extends
		IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7522648438555609713L;

	/**
	 * @param executor
	 */
	public NotInUseException(StatementExecutor executor) {
		super("Statement executor " + executor
				+ " was not correctly getted from pool "
				+ executor.poolOwner);
	}

}