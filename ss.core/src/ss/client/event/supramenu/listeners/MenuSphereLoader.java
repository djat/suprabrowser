/**
 * 
 */
package ss.client.event.supramenu.listeners;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;

/**
 * @author zobo
 * 
 */
public class MenuSphereLoader {

	private static final Logger logger = SSLogger
			.getLogger(FavouriteItemListener.class);

	public static synchronized void openSphere(final String sphereId,
			final SupraSphereFrame sF) {
		if (sF.tabbedPane.isAlreadyOpenenedSameSphere(sphereId)){
			return;
		}
		final Document doc = sF.client.getSphereDefinition(sphereId);
		
		if(doc==null) {
			return;
		}
		
		final SphereStatement sphereSt = SphereStatement.wrap(doc);
		
		final String newmp = sphereSt.getDisplayName();
		final String systemName = sphereSt.getSystemName();

		if (!sF.tabbedPane.isAlreadyOpenenedSameSphere(systemName)) {
			Thread t = new Thread() {

				@SuppressWarnings("unchecked")
				public void run() {
					sF.tabbedPane.putSphereToTabQueue(systemName);
					Hashtable newSession = sF.getMainVerbosedSession().rawClone();
					newSession.put("sphere_id", systemName);
					String sphereType = sF.client.getVerifyAuth()
							.getSphereType(systemName);
					logger.info("Sphere type: " + sphereType);
					newSession.put("sphere_type", sphereType);
					/*
					 * if (doc==null) { SphereDefinitionCreator sdc = new
					 * SphereDefinitionCreator(); doc =
					 * sdc.createDefinition(newmp,systemName); }
					 */
					sF.client.searchSphere(newSession, doc, "false");

				}

			};
			t.start();
		}

		else {
			sF.tabbedPane.selectTabByTitle( newmp );
		}
	}
}
