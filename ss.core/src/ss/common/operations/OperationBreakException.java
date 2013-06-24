package ss.common.operations;
 
public class OperationBreakException extends Exception {
 
 	/**
 	 * 
 	 */
 	private static final long serialVersionUID = 8854757850571074254L;
 
	public OperationBreakException( IOperation operation, String message ) {
		super( formatMessage(operation) + " cause: " + message  );
 	}
 	
	public OperationBreakException( IOperation operation ) {
		super( formatMessage(operation) );
	}

	public OperationBreakException( IOperation operation, Exception cause ) {
		super( formatMessage(operation), cause );
	}
	
	private static String formatMessage(IOperation operation) {
		return String.format( "Operation %s broke.", operation );
	}
	
	 	
}
