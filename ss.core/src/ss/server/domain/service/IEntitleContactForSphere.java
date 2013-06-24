/**
 * 
 */
package ss.server.domain.service;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.server.db.XMLDB;


/**
 *
 */
public interface IEntitleContactForSphere extends ISupraSphereFeature {

	void entitleContactForSphere(Hashtable session,
			String inviteSphereType, Document contactDoc, String tableId,
			String sphereId);

	public Document entitleContactForGroupSphere(Hashtable session,
			Document contactBeingRegistered, String contactName,
			String loginName, String systemName, String displayName,
			Vector enabledMemberDocs, XMLDB xmldb) throws DocumentException;
}
