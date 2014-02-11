package ss.lab.dm3.snapshoots;

/**
 * @author Dmitry Goncharov
 *
 */
public class SnapshotObjectHandlerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6495887829590326011L;

	/**
	 * 
	 */
	public SnapshotObjectHandlerException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SnapshotObjectHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SnapshotObjectHandlerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SnapshotObjectHandlerException(Throwable cause) {
		super(cause);
	}

	
}
