/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;
import ss.common.domain.model.collections.SphereEmailObjectCollection;
import ss.common.domain.model.collections.SupraSphereMemberObjectCollection;
import ss.common.domain.model.trash.UiObject;


/**
 * @author roman
 *
 */
public class SupraSphereObject extends DomainObject {

	private final DomainReference<UiObject> uiObjectRef = DomainReference.create(UiObject.class);
	
	private String systemName;
	
	private String displayName;
	
	private final DomainReference<AdminObject> adminRef = DomainReference.create(AdminObject.class);
	
	/**
	 * <email>
	 *  <sphere_domain value="$loginAddress"/>
	 * </email>
	 */
	private String emailDomain;
	
	/**
	 * Maybe is not used
	 */
	private String status;
	
	private final SphereEmailObjectCollection spheresEmails = new SphereEmailObjectCollection();
	
	private final SupraSphereMemberObjectCollection members = new SupraSphereMemberObjectCollection();

	/**
	 * @return the uiObject
	 */
	public UiObject getUiObject() {
		return this.uiObjectRef.get();
	}

	/**
	 * @param uiObject the uiObject to set
	 */
	public void setUiObject(UiObject uiObject) {
		this.uiObjectRef.set(uiObject);
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
	 * @return the adminRef
	 */
	public DomainReference<AdminObject> getAdminRef() {
		return this.adminRef;
	}

	/**
	 * @return the admin
	 */
	public AdminObject getAdmin() {
		return this.adminRef.get();
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(AdminObject admin) {
		this.adminRef.set(admin);
	}

	/**
	 * @return the emailDomain
	 */
	public String getEmailDomain() {
		return this.emailDomain;
	}

	/**
	 * @param emailDomain the emailDomain to set
	 */
	public void setEmailDomain(String emailDomain) {
		this.emailDomain = emailDomain;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the spheresEmails
	 */
	public SphereEmailObjectCollection getSpheresEmails() {
		return this.spheresEmails;
	}

	/**
	 * @return the members
	 */
	public SupraSphereMemberObjectCollection getMembers() {
		return this.members;
	}
}
