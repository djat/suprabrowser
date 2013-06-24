package ss.server.networking.protocol;

import java.util.Hashtable;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;

/**
 * @deprecated
 * @author dankosedin
 *
 */
public class ReplaceMemberHandler implements ProtocolHandler {


	/**
	 * @deprecated
	 * @param peer
	 */
	public ReplaceMemberHandler() {
		
	}
	
	public String getProtocol() {
		return SSProtocolConstants.REPLACE_MEMBER;
	}

	public void handle(Hashtable update) {
		handleReplaceMember(update);
	}

	public void handleReplaceMember(final Hashtable update) {		
		//Document memDoc = (Document) update.get(SC.MEM_DOC);
		//String old_login = (String) update.get(SC.OLD_LOGIN);
		//this.peer.getXmldb().replaceMember(memDoc, old_login);
	}

}
