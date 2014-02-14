/**
 * 
 */
package ss.server.networking.protocol.callbacks;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.RenameContactTypeAction;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereMember;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.getters.AbstractGetterCommandHandler;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class RenameContactTypeActionHandler extends
		AbstractGetterCommandHandler<RenameContactTypeAction, Boolean> {

	public RenameContactTypeActionHandler(final DialogsMainPeer peer) {
		super(RenameContactTypeAction.class, peer);
	}
	
	@Override
	protected Boolean evaluate(RenameContactTypeAction command)
			throws CommandHandleException {
		String newTypeName = command.getStringArg(SessionConstants.NEW_NAME);
		String oldTypeName = command.getStringArg(SessionConstants.OLD_NAME);
		if (StringUtils.isBlank(oldTypeName)
				|| oldTypeName.equals(SphereMember.NO_TYPE)) {
			return false;
		}
		Vector<Document> contactDocs = this.peer.getXmldb().getAllContacts();
		for (Document contactDoc : contactDocs) {
			if (contactDoc == null) {
				continue;
			}
			ContactStatement contact = ContactStatement.wrap(contactDoc);
			if (StringUtils.isBlank(contact.getRole())) {
				continue;
			}
			if (contact.getRole().equals(SphereMember.NO_TYPE)
					|| !contact.getRole().equals(oldTypeName)) {
				continue;
			}
			contact.setRole(newTypeName);
			this.peer.getXmldb().replaceDoc(contact.getBindedDocument(),
					contact.getCurrentSphere());
		}
		return true;
	}

}
