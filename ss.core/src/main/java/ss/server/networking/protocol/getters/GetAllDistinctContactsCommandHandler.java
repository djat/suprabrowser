/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllDistinctContactsCommand;
import ss.domainmodel.ContactStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class GetAllDistinctContactsCommandHandler extends
		AbstractGetterCommandHandler<GetAllDistinctContactsCommand, HashSet<Document>> {

	public GetAllDistinctContactsCommandHandler(final DialogsMainPeer peer) {
		super(GetAllDistinctContactsCommand.class, peer);
	}

	@Override
	protected HashSet<Document> evaluate(
			GetAllDistinctContactsCommand command)
			throws CommandHandleException {
		List<Document> contacts = this.peer.getXmldb().getAllContacts();
		HashSet<ContactStatement> contactSet = new HashSet<ContactStatement>();
		for (Document contactDoc : contacts) {
			ContactStatement contact = ContactStatement.wrap(contactDoc);
			if (setContainsSuchContact(contactSet, contact)) {
				continue;
			}
			contactSet.add(contact);
		}
		HashSet<Document> docs = new HashSet<Document>();
		for(ContactStatement statement : contactSet) {
			docs.add(statement.getBindedDocument());
		}
		return docs;
	}

	/**
	 * @param contact
	 * @return
	 */
	private boolean setContainsSuchContact(HashSet<ContactStatement> set,
			ContactStatement contact) {
		for (ContactStatement c : set) {
			if (c.getContactNameByFirstAndLastNames().equals(
					contact.getContactNameByFirstAndLastNames())) {
				return true;
			}
		}
		return false;
	}

}
