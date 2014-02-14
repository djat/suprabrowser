package ss.refactor.supraspheredoc.old.unused;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.protocol.getters.AbstractGetterCommandHandler;

public class GetPrivateMembersHandler extends AbstractGetterCommandHandler<GetPrivateMembersCommand, Hashtable<String,String>> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetPrivateMembersHandler.class);
	
	private static final String VALUE = "value";
	
	private static final String CONTACT_NAME = "contact_name";

	/**
	 * @param peer
	 */
	public GetPrivateMembersHandler( DialogsMainPeer peer) {
		super(GetPrivateMembersCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String,String> evaluate(GetPrivateMembersCommand command) throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String sphereId = (String) session.get(SC.SPHERE_ID);
		Vector privateList = this.peer.getXmldb().getMembersForSphere(sphereId);
		Hashtable<String,String> privateMembers = new Hashtable<String,String>();
		for (int j = 0; j < privateList.size(); j++) {
			Document doc = (Document) privateList.get(j);
			String contactName = doc.getRootElement().element(CONTACT_NAME)
					.attributeValue(VALUE);
			privateMembers.put(contactName, contactName);
		}
		return privateMembers;
	}

}
