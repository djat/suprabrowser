/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.enums.SphereType;

/**
 * @author roman
 *
 */
public class SimpleSphereReferenceObject extends DomainObject {

	protected String displayName;
	
	protected String systemName;
	
	protected SphereType sphereType;

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return this.systemName;
	}

	/**
	 * @param systemName the systemName to set
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * @return the sphereType
	 */
	public SphereType getSphereType() {
		return this.sphereType;
	}

	/**
	 * @param sphereType the sphereType to set
	 */
	public void setSphereType(SphereType sphereType) {
		this.sphereType = sphereType;
	}
	
	
	
}
