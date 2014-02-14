/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.domainmodel.MemberReference;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SearchP2PAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7404168090480475744L;
	
	public SearchP2PAction(Hashtable session, MemberReference member, String systemName) {
		super();
		putSessionArg(session);
		putArg(SessionConstants.CONTACT_NAME, member.getContactName());
		putArg(SessionConstants.SYSTEM_NAME, systemName);
	}

}
