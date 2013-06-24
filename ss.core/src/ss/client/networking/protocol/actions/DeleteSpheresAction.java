/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.List;
import java.util.Vector;

import ss.client.ui.spheremanagement.ManagedSphere;

/**
 * @author roman
 *
 */
public class DeleteSpheresAction extends AbstractAction {

	private static final String SPHERE_IDS = "sphereIds";
	
	private boolean removeTotally = false;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeleteSpheresAction.class);

	private static final long serialVersionUID = -2292502168231600752L;

	public DeleteSpheresAction() {
	}
	
	public void setSpheresList( final List<ManagedSphere> list ){
		Vector<String> ids = new Vector<String>();
		for(ManagedSphere sphere : list) {
			ids.add(sphere.getId());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("spheres size : "+list);
		}
		putArg(SPHERE_IDS, ids);		
	}
	
	public Vector<String> getSpheresList(){
		return (Vector<String>) getObjectArg( SPHERE_IDS );
	}
	
	public void setRemoveTotally( final boolean removeTotally ){
		this.removeTotally = removeTotally;
	}
	
	public boolean isRemoveTotally(){
		return this.removeTotally;
	}
}
