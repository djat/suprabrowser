/**
 * 
 */
package ss.server.networking.protocol.actions;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.LockContactAction;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class LockContactActionHandler extends AbstractActionHandler<LockContactAction> {

	private static final String LOCK_ELEM_NAME = "locked_up";
	
	public LockContactActionHandler(final DialogsMainPeer peer) {
		super(LockContactAction.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(LockContactAction action) {
		String loginName = action.getLogin();
		
		String loginSphere = this.peer.getVerifyAuth().getLoginSphere(loginName);
	
		Document membershipDoc = this.peer.getXmldb().getMembershipDoc(loginSphere, loginName);
	
		if (membershipDoc != null) {
			membershipDoc.getRootElement().addElement(
					LOCK_ELEM_NAME);
			this.peer.getXmldb().replaceDoc(membershipDoc, loginSphere);
		}
	}

}
