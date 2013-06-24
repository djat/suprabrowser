/**
 * 
 */
package ss.client.ui.sphereopen;

import java.util.Hashtable;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.progressbar.DownloadProgressBar;

/**
 * @author zobo
 *
 */
class SphereNamesLoadingController {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereNamesLoadingController.class);
	
	private final Hashtable<Integer, String> sphereNames = new Hashtable<Integer, String>();
	
	void setSphereName( final String sphereName, final int packetId ){
		if (sphereName == null){
			logger.error("Sphere name for packetId: " + packetId + " is null");
			return;
		}
		synchronized (this.sphereNames) {
			try {
			if (!notifyProgressBar(sphereName, packetId)){
				if (logger.isDebugEnabled()) {
					logger.debug("Setted sphereName: " + sphereName + " for packetId: " + packetId);
				}
				this.sphereNames.put(packetId, sphereName);
			}
			} catch (Throwable ex) {
				logger.error("Error while setting next sphere name", ex);
			}
		}
	}
	
	String getSphereName( final int packetId ){
		synchronized (this.sphereNames) {
			String name = this.sphereNames.get(packetId);
			if (name == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No sphere name for requested packetId: " + packetId);
				}
			} else {
				this.sphereNames.remove(packetId);
				if (logger.isDebugEnabled()) {
					logger.debug("Sphere name for packetId: " + packetId + " is: " + name);
				}
			}
			return name;
		}
	}
	
	private boolean notifyProgressBar( final String sphereName, final int packetId ){
		final DownloadProgressBar progressBar = SupraSphereFrame.INSTANCE.client.getProgressBar();
		if ( progressBar == null ){
			if (logger.isDebugEnabled()) {
				logger.debug("Download Progress Bar is null, no setting");
			}
			return false;
		}
		if ( progressBar.changeTitleNotify(sphereName, packetId) ){
			return true; 
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Wrong Download Progress Bar to set sphereName: " + sphereName + ", packetId: " + packetId);
			}
			return false;
		}
	}
}
