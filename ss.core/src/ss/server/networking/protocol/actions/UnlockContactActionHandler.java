/**
 * 
 */
package ss.server.networking.protocol.actions;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.protocol.actions.UnlockContactAction;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class UnlockContactActionHandler extends AbstractActionHandler<UnlockContactAction> {
	
	private static final String LOCK_ELEM_NAME = "locked_up";
	
	public UnlockContactActionHandler(final DialogsMainPeer peer) {
		super(UnlockContactAction.class, peer);
	}
	
	
	@Override
	protected void execute(UnlockContactAction action) {
		String loginName = action.getLogin();
		
		String loginSphere = this.peer.getVerifyAuth().getLoginSphere(loginName);
	
		Document membershipDoc = this.peer.getXmldb().getMembershipDoc(loginSphere, loginName);
	
		if (membershipDoc != null) {
			Element lockElement = membershipDoc.getRootElement().element(LOCK_ELEM_NAME);
			if(lockElement==null) {
				return;
			}
			membershipDoc.getRootElement().remove(lockElement);
			
			this.peer.getXmldb().replaceDoc(membershipDoc, loginSphere);
		}
	}

}
