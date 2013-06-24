/**
 * Jul 5, 2006 : 1:26:00 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UpdateVerifySphereDocumentHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UpdateVerifySphereDocumentHandler.class);

	private DialogsMainCli cli;

	public UpdateVerifySphereDocumentHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleUpdateVerifySphereDocument(final Hashtable update) {
		this.cli.getVerifyAuth().setSphereDocument(
				(Document) update.get(SessionConstants.SUPRA_SPHERE_DOCUMENT));
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleUpdateVerifySphereDocument(update);
	}

}
