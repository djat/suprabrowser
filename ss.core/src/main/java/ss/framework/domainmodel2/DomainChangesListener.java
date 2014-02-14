/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.EventListener;

/**
 * WARNING! this interface is subject to change
 */
public interface DomainChangesListener extends EventListener {

	void objectRemoved( AffectedDomainObjectList objects );
	
	void objectChanged( AffectedDomainObjectList objects );
	
	void objectCreated( Class<? extends DomainObject> objectClass );
	
}
