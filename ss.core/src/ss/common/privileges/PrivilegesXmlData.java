package ss.common.privileges;

import org.dom4j.Element;

import ss.common.VerifyAuth;
import ss.common.VerifyAuthOld;
import ss.common.XmlDocumentUtils;

public class PrivilegesXmlData {

	private VerifyAuthOld verifyAuth;	
		
	public PrivilegesXmlData(VerifyAuthOld verifyAuth) {
		super();
		this.verifyAuth = verifyAuth;
	}

	/**
	 * Returns user permission level or null if no persmission specified.
	 * @param privilegeName privilege name  
	 * @param loginName user login name
	 */
	public String getUserPermissionLevelForPrivilege(String privilegeName, String contactName, String loginName) {
		// skip contact name in xpath
		String selectPermissionLevelXPath = String.format( "//suprasphere/admin/privileges/privilege[@name='%s']/user[@login_name='%s']/permission_level/@value", privilegeName, loginName );
		return XmlDocumentUtils.selectAttibuteValueByXPath( this.verifyAuth.getSupraSphereDocument(), selectPermissionLevelXPath );		
	}
	
	/**
	 * Set permission level for user 
	 * @param privilegeName privilege name
	 * @param contactName user contact name
	 * @param loginName user login name 
	 * @param permissionLevel not null permission level
	 */
	public void setUserPermissionLevelForPrivilege(String privilegeName, String contactName, String loginName, String permissionLevel ) {
		if ( permissionLevel == null ) {
			throw new NullPointerException( "permissionLevel should be not null" );
		}
		Element userElement = ensureExistUserElementForPrivilege(privilegeName, contactName, loginName);
		XmlDocumentUtils.putElementWithAttribute( userElement, "permission_level", "value", permissionLevel );
	}

	private Element ensureExistUserElementForPrivilege(String privilegeName, String contactName, String loginName) {
		Element adminElement = this.verifyAuth.selectSuprasphereElement( "//suprasphere/admin" );
		if ( adminElement == null ) {
			throw new NullPointerException( "Admin element is null" );
		}
		
		Element privilegesElement = XmlDocumentUtils.putElement( adminElement, "privileges" );
		Element privilegeElement = XmlDocumentUtils.selectOrCreateElementWithAttribute(privilegesElement, "privilege", "name", privilegeName );
//		Assume that we have not user with same login but with different contactNames
		Element userElement = XmlDocumentUtils.selectOrCreateElementWithAttribute(privilegeElement, "user", "login_name", loginName );
		userElement.addAttribute( "contact_name", contactName );		
		return userElement;
	}
	
	/**
	 * Reset permission level for user 
	 * @param privilegeName privilege name
	 * @param contactName user contact name
	 * @param loginName user login name 
	 * @param permissionLevel
	 */
	public void clearUserPermissionsForPrivilege(String privilegeName, String contactName, String loginName ) {
		Element userElement = ensureExistUserElementForPrivilege(privilegeName, contactName, loginName);
		userElement.getParent().remove(userElement);
	}
	
	/**
	 * Clear all privileges. For debug purpose.
	 */
	protected void debugOnly_clearAllPrivileges() {
		Element elem = XmlDocumentUtils.selectElementByXPath( this.verifyAuth.getSupraSphereDocument(), "//suprasphere/admin/privileges" );
		if ( elem  != null ) {
			elem.getParent().remove(elem);
		}		
	}

}
