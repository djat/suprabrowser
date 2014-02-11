package ss.lab.dm3.events;

import ss.lab.dm3.events.EventExcpetion;
import ss.lab.dm3.events.EventListener;

/**
 * @author Dmitry Goncharov
 *
 */
public interface TestEventListener extends EventListener {

	/**
	 * 
	 */
	void hello( String text) throws EventExcpetion;

}
