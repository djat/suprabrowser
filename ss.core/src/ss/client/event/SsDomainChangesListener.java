/**
 * 
 */
package ss.client.event;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.domainmodel2.Sphere;
import ss.common.domainmodel2.Sphere.SystemNameDescriptor;
import ss.framework.domainmodel2.AffectedDomainObjectList;
import ss.framework.domainmodel2.DescriptorManager;
import ss.framework.domainmodel2.DomainChangesListener;
import ss.framework.domainmodel2.DomainObject;

/**
 * @author roman
 *
 */
public class SsDomainChangesListener implements DomainChangesListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SsDomainChangesListener.class);
	
	public void objectChanged(AffectedDomainObjectList objects) {
		for (DomainObject o : objects.iterator()) {
			if (!o.getClass().equals(Sphere.class)) {
				logger.debug("it's not sphere preferences");
				return;
			}
			String sphereId = o.getField(DescriptorManager.INSTANCE.get( SystemNameDescriptor.class )).get();
			MessagesPane pane = SupraSphereFrame.INSTANCE.getMessagesPaneFromSphereId(sphereId);
			if(pane == null) {
				logger.debug("pane is null");
				return;
			}
			pane.reactOnPreferencesChange();
		}
	}

	public void objectCreated(Class<? extends DomainObject> objectClass) {
		logger.warn("object created : "+objectClass.getName());
	}

	public void objectRemoved(AffectedDomainObjectList objects) {
		logger.warn("object removed : "+objects.size());
	}
}
