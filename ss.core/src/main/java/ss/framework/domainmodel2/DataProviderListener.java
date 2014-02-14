/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.EventListener;

/**
 *
 */
public interface DataProviderListener extends EventListener {

	void dataChanged( DataChangedEvent e );
	
}
