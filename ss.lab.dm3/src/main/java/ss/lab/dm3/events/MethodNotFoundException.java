package ss.lab.dm3.events;

/**
 * @author Dmitry Goncharov
 */
public class MethodNotFoundException extends EventExcpetion {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3859014970066877711L;

	/**
	 * @param methodName
	 * @param ex
	 */
	public MethodNotFoundException(String methodName, Throwable ex) {
		super( "Can't find method " + methodName, ex );
	}

}
