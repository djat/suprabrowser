package ss.client.networking.protocol.obosolete;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @deprecated
 * @author dankosedin
 *
 * TODO:#member-refactoring
 * Not used.
 * Should change login?
 * 
 */
public class ReplaceMemberHandler implements ProtocolHandler {

	private DialogsMainCli cli;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplaceMemberHandler.class);

	/**
	 * @deprecated
	 * @param cli
	 */
	public ReplaceMemberHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {	
		return SSProtocolConstants.REPLACE_MEMBER;
	}

	public void handle(Hashtable update) {
		// to-server-only hander 
	}

	@SuppressWarnings("unchecked")
	public void replaceMember(Hashtable session, Document memDoc, String old_login) {		
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.REPLACE_MEMBER);
		update.put(SessionConstants.OLD_LOGIN, old_login);
		update.put(SessionConstants.MEM_DOC, memDoc);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
