/**
 * 
 */
package ss.framework.networking2;

import java.util.EventListener;

/**
 *
 */
public interface ProtocolLifetimeListener extends EventListener  {

	/**
	 * Notify protocol setupped 
	 */
	void started( ProtocolLifetimeEvent e  );
	
	/**
	 * Notify protocol teardowned.
	 */
	void beginClose( ProtocolLifetimeEvent e  );
	
}
