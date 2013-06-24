package ss.server.domain.service;

import java.util.Hashtable;

import org.dom4j.Document;

public interface ISupraSphereEditFacade {

	/**
	 * @param tempUsername
	 * @param username
	 */
	void updateUserLogin(String tempUsername, String username, String loginSphere, Document contactDoc);

	/**
	 *  
	 * @param supraSphereName
	 * @param doc
	 * @param inviteContact
	 * @param inviteUsername
	 * @param inviteSphereName
	 * @param inviteSphereId
	 * @param cname
	 * @param username
	 * @param inviteSphereType
	 * @param session
	 */
	void registerMember(String supraSphereName, Document doc,
			String inviteContact, String inviteUsername,
			String inviteSphereName, String inviteSphereId, String cname,
			String username, String inviteSphereType, Hashtable session);

	/**
	 * @param parentSphereSystemName
	 * @param sphereSystemName
	 * @param sphereDisplayName
	 * @param memberLogin
	 * @param memberContactName
	 */
	void removeMemberForGroupSphereLight(String parentSphereSystemName,
			String sphereSystemName, String sphereDisplayName,
			String memberLogin, String memberContactName);

	/**
	 * @param parentSphereSystemName
	 * @param sphereSystemName
	 * @param sphereDisplayName
	 * @param memberLogin
	 * @param memberContactName
	 */
	void addMemberToGroupSphereLight(String parentSphereSystemName,
			String sphereSystemName, String sphereDisplayName,
			String memberLogin, String memberContactName);

	/**
	 * @param supraSphereName
	 * @param login
	 * @param sphereId
	 * @param sphereName
	 * @param sphereType
	 */
	Document makeCurrentSphereCore(String supraSphereName, String login,
			String sphereId, String sphereName, String sphereType);

}
