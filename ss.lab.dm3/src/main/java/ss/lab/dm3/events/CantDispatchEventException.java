package ss.lab.dm3.events;

/**
 * @author Dmitry Goncharov
 */
public class CantDispatchEventException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3337660118834210924L;

	/**
	 * @param event2
	 * @param ex
	 */
	public CantDispatchEventException(Event event, Throwable ex) {
		super( "Can't dispatch event " + event, ex );
	}
}
