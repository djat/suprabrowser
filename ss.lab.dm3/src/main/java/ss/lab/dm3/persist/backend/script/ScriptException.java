package ss.lab.dm3.persist.backend.script;

/**
 * @author Dmitry Goncharov
 */
public class ScriptException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1020380866172199931L;

	/**
	 * 
	 */
	public ScriptException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ScriptException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ScriptException(Throwable cause) {
		super(cause);
	}

	
}
