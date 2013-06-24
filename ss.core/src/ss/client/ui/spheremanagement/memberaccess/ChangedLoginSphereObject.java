/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import ss.domainmodel.MemberReference;

/**
 * @author roman
 *
 */
public class ChangedLoginSphereObject {
	
	private MemberReference member;
	
	private String loginSphereId;
	
	ChangedLoginSphereObject(MemberReference member, String loginSphereId) {
		this.member = member;
		this.loginSphereId = loginSphereId;
	}
	
	public MemberReference getMember() {
		return this.member;
	}
	
	public String getSphereId() {
		return this.loginSphereId;
	}
}
