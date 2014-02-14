/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;
import ss.common.domain.model.collections.SphereReferencesCollection;


/**
 * @author roman
 *
 */
public class SupraSphereMemberObject extends DomainObject {

	private String contactName;
	
	private String loginName;
	
	private final DomainReference<SimpleSphereReferenceObject> loginSphereRef = DomainReference.create( SimpleSphereReferenceObject.class );
	
	private final DomainReference<SimpleSphereReferenceObject> sphereCoreRef = DomainReference.create( SimpleSphereReferenceObject.class );
	
	private final DomainReference<PerspectiveObject> perspectiveRef = DomainReference.create(PerspectiveObject.class);

	private final SphereReferencesCollection spheres = new SphereReferencesCollection();

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return this.contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the loginSphere
	 */
	public SimpleSphereReferenceObject getLoginSphere() {
		return this.loginSphereRef.get();
	}

	/**
	 * @param loginSphere the loginSphere to set
	 */
	public void setLoginSphere(SimpleSphereReferenceObject loginSphere) {
		this.loginSphereRef.set(loginSphere);
	}
	
	/**
	 * @return the loginSphereRef
	 */
	public DomainReference<SimpleSphereReferenceObject> getLoginSphereRef() {
		return this.loginSphereRef;
	}
	
	/**
	 * @return the perspectiveRef
	 */
	public DomainReference<PerspectiveObject> getPerspectiveRef() {
		return this.perspectiveRef;
	}

	/**
	 * @return the perspective
	 */
	public PerspectiveObject getPerspective() {
		return this.perspectiveRef.get();
	}

	/**
	 * @param perspective the perspective to set
	 */
	public void setPerspective(PerspectiveObject perspective) {
		this.perspectiveRef.set(perspective);
	}

	/**
	 * @return the spheres
	 */
	public SphereReferencesCollection getSpheres() {
		return this.spheres;
	}

	/**
	 * @return the sphereCoreRef
	 */
	public DomainReference<SimpleSphereReferenceObject> getSphereCoreRef() {
		return this.sphereCoreRef;
	}

	/**
	 * @return
	 */
	public SimpleSphereReferenceObject getLoginSphereObject() {
		return this.sphereCoreRef.get();
	}

	/**
	 * @param obj
	 */
	public void setLoginSphereObject(SimpleSphereReferenceObject obj) {
		this.sphereCoreRef.set(obj);
	}
	
	
}
