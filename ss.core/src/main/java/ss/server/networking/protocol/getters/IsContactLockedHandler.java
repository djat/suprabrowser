/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.IsContactLockedCommand;
import ss.common.StringUtils;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class IsContactLockedHandler extends AbstractGetterCommandHandler<IsContactLockedCommand, Boolean> {

	private static final String LOCK_ELEM_NAME = "locked_up";
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public IsContactLockedHandler(DialogsMainPeer peer) {
		super(IsContactLockedCommand.class, peer);
	}

	@Override
	protected Boolean evaluate(IsContactLockedCommand command)
			throws CommandHandleException {
		String loginName = command.getLogin();
		
		if(StringUtils.isBlank(loginName)) {
			return false;
		}
		
		String loginSphere = this.peer.getVerifyAuth().getLoginSphere(loginName);
		if(StringUtils.isBlank(loginSphere)) {
			return false;
		}
	
		Document membershipDoc = this.peer.getXmldb().getMembershipDoc(loginSphere, loginName);
		if(membershipDoc==null) {
			return false;
		}
	
		return membershipDoc.getRootElement().element(LOCK_ELEM_NAME)!=null;
	}
}
