/**
 * 
 */
package ss.client.ui.sphereopen;

/**
 * @author zobo
 *
 */
public class SphereStateController {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereStateController.class);
	
	private final SphereStateRegistry sphereRegistry = new SphereStateRegistry();
	
	SphereStateController(){
		
	}
	
	boolean isOpened( final String sphereId ){
		SphereState state = this.sphereRegistry.getSphereState(sphereId);
		if ( (state != null) && (state == SphereState.OPENED) ){
			return true;
		} else {
			return false;
		}
	}
	
	boolean isOpening( final String sphereId ){
		SphereState state = this.sphereRegistry.getSphereState(sphereId);
		if ( (state != null) && (state == SphereState.OPENING) ){
			return true;
		} else {
			return false;
		}
	}
	
	void setOpening( final String sphereId ){
		if (logger.isDebugEnabled()){
			logger.debug("Set opening for: " + sphereId);
		}
		this.sphereRegistry.setSphereState(sphereId, SphereState.OPENING);	
	}
	
	void setOpened( final String sphereId ){
		if (logger.isDebugEnabled()){
			logger.debug("Set opened for: " + sphereId);
		}
		this.sphereRegistry.setSphereState(sphereId, SphereState.OPENED);
	}
	
	void setClosed( final String sphereId ){
		if (logger.isDebugEnabled()){
			logger.debug("Set closed for: " + sphereId);
		}
		this.sphereRegistry.close(sphereId);		
	}
	
	void cleanUp(){
		this.sphereRegistry.checkPending();
	}
}
