package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetContactFromLoginCommand;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetContactFromLoginHandler extends AbstractGetterCommandHandler<GetContactFromLoginCommand, AbstractDocument> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetContactFromLoginHandler(DialogsMainPeer peer) {
		super(GetContactFromLoginCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetContactFromLoginCommand command) throws CommandHandleException {
		final String loginName = command.getStringArg( SC.USERNAME );
		final String realName = command.getStringArg( SC.REAL_NAME );
		if (StringUtils.isNotBlank( loginName )) {
			final String loginSphere = this.peer.getXmldb().getUtils().getLoginSphereSystemName(loginName);
			return (AbstractDocument) this.peer.getXmldb().getContactDoc(loginSphere, loginName);
		}
		if (StringUtils.isNotBlank( realName )) {
			Vector<Document> contacts = this.peer.getXmldb().getAllContacts();
			if ( contacts == null ) {
				return null;
			}
			for ( Document doc : contacts ) {
				ContactStatement st = ContactStatement.wrap( doc );
				if (!st.isContact()) {
					continue;
				}
				if (st.getContactNameByFirstAndLastNames().equals(realName)) {
					return (AbstractDocument) doc;
				}
			}
		}
		return null;
	}

}
