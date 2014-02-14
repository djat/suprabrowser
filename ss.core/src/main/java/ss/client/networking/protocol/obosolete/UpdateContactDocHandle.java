/**
 * Jul 5, 2006 : 1:45:57 PM
 */
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
 * Unused. Should update contact document in local verifyAuth.
 * Called by server.
 * 
 */
public class UpdateContactDocHandle implements ProtocolHandler {

	private DialogsMainCli cli;

	/**
	 * @deprecated
	 * @param cli
	 */
	public UpdateContactDocHandle(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleUpdateContactDoc(final Hashtable update) {
		this.cli.getVerifyAuth().setContactDocument(
				(Document) update.get(SessionConstants.CONTACT_DOC));
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_CONTACT_DOC;
	}

	public void handle(Hashtable update) {
		handleUpdateContactDoc(update);
	}

}
