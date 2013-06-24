/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.networking.protocol.getters.GetContactSpheresForSpecificContactCommand;
import ss.common.StringUtils;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereMemberCollection;
import ss.domainmodel.SupraSphereStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetContactSpheresForSpecificContactCommandHandler extends
		AbstractGetterCommandHandler<GetContactSpheresForSpecificContactCommand, Hashtable<String, Document>> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetContactSpheresForSpecificContactCommandHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetContactSpheresForSpecificContactCommandHandler(DialogsMainPeer peer) {
		super(GetContactSpheresForSpecificContactCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected Hashtable<String, Document> evaluate(
			GetContactSpheresForSpecificContactCommand command)
			throws CommandHandleException {
		if (logger.isDebugEnabled()) {
			logger.debug("GetContactSpheresForSpecificContactCommandHandler started");
		}
		final String contactName = command.getContactName();
		if (StringUtils.isBlank(contactName)) {
			logger.error("contactName is blank");
			return null;
		}
		
		final Hashtable<String, Document> preResult = this.peer.getXmldb().getContactDocsInSpheresForContactName(contactName);
		if ( (preResult==null) || (preResult.isEmpty()) ) {
			return preResult;
		}
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
		} catch(DocumentException ex) {
			logger.error("Can not get suprasphere document", ex);
			return preResult;
		}
		if ( supraDoc == null ) {
			logger.error("SupraDoc is null");
			return preResult;
		}
		final SupraSphereStatement supraSt = SupraSphereStatement.wrap(supraDoc);
		SupraSphereMemberCollection members = supraSt.getSupraMembers();
		if ( members == null ) {
			return preResult;
		}
		final Hashtable<String, Document> result = new Hashtable<String, Document>();
		for ( String id : preResult.keySet() ) {
			if ( isNotMember(id, members) ) {
				result.put(id, preResult.get(id));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("GetContactSpheresForSpecificContactCommandHandler finished");
		}
		return result;
	}
	
	private boolean isNotMember( final String sphereId, SupraSphereMemberCollection members ){
		for (SupraSphereMember member : members) {
			SphereItem item = member.getSphereBySystemName(sphereId);
			if  ( item != null ) {
				if ( item.isMember() ) {
					return false;
				} else {
					return true; 
				}
			}
		}
		return true;
	}
}
