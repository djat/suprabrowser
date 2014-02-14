/**
 * Jul 5, 2006 : 11:58:27 AM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UpdateSphereDefinitionHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UpdateSphereDefinitionHandler.class);

	private DialogsMainCli cli;

	public UpdateSphereDefinitionHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleUpdateSphereDefinition(final Hashtable update) {
		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);

		Document sphereDefinition = (Document) update
				.get(SessionConstants.SPHERE_DEFINITION2);// RC

		String sphereId = (String) this.cli.session
				.get(SessionConstants.SPHERE_ID2);
		for( MessagesPane messagesPane : this.cli.getSF().getMessagePanesController().findMessagePanesBySphereId(sphereId) ) {
			messagesPane.checkQueryAndSetSphereDefinition(sphereDefinition);
		}
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_SPHERE_DEFINITION;
	}

	public void handle(Hashtable update) {
		handleUpdateSphereDefinition(update);

	}

}
