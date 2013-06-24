/**
 * 
 */
package ss.server.functions.setmark.sphere;

import ss.server.functions.setmark.AssociatedProcedure;

/**
 * @author zobo
 *
 */
@AssociatedProcedure(SetSphereMarkReadProcedure.class)
public class SetSphereMarkReadData extends SetSphereMarkData {

	private static final long serialVersionUID = -315930245608971098L;

	private String sphereId;

	public String getSphereId() {
		return this.sphereId;
	}

	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}
}
