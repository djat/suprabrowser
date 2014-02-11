/**
 * 
 */
package ss.lab.dm3.persist;

import ss.lab.dm3.persist.ObjectController.State;

/**
 * @author Dmitry Goncharov
 */
public class CantChangeDomainObjectStateException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1295602540268031854L;


	/**
	 * @param domainObject
	 * @param extectedState
	 */
	public CantChangeDomainObjectStateException(DomainObject domainObject,
			State targetState ) {
		super( "Can't change state of object " + domainObject + " to state " + targetState );
	}


	/**
	 */
	public CantChangeDomainObjectStateException(State toState, State fromState) {
		super( "Can't change state to " + toState + " from " + fromState);
	}
	
}
