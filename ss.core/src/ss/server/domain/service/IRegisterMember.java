/**
 * 
 */
package ss.server.domain.service;

import java.util.Hashtable;

import org.dom4j.Document;

/**
 *
 */
public interface IRegisterMember extends ISupraSphereFeature {

	public void registerMember(final Hashtable update, Hashtable session,
			String inviteUsername, String inviteContact, String sphereName,
			String sphereId, String realName, String username,
			String inviteSphereType, Document contactDoc, String supraSphere);
}
