/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.domainmodel.MemberReference;

/**
 * @author roman
 *
 */
public class SearchPrivateSphereAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3898985007573857167L;

	public SearchPrivateSphereAction(Hashtable session, MemberReference member) {
		super();
		putSessionArg(session);
		putArg("contact_name", member.getContactName());
		putArg("user_name", member.getLoginName());
	}
}
