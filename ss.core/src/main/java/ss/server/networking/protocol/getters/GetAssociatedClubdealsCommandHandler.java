/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAssociatedClubdealsCommand;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.framework.networking2.CommandHandleException;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetAssociatedClubdealsCommandHandler extends
		AbstractGetterCommandHandler<GetAssociatedClubdealsCommand, Vector<String>> {

	private static final Logger logger = SSLogger.getLogger(GetAssociatedClubdealsCommandHandler.class);
	
	public GetAssociatedClubdealsCommandHandler(final DialogsMainPeer peer) {
		super(GetAssociatedClubdealsCommand.class, peer);
	}
	@Override
	protected Vector<String> evaluate(GetAssociatedClubdealsCommand command)
			throws CommandHandleException {
		String dataId = command.getStringArg(SessionConstants.DATA_ID);
		List<Document> allSpheres = this.peer.getXmldb().getAllSpheres();
		Set<String> clubdeals = new HashSet<String>();
		for(Document doc : allSpheres) {
			SphereStatement sphere = SphereStatement.wrap(doc);
			if(sphere.isClubDeal()) {
				clubdeals.add(sphere.getSystemName());
			}
		}
		Set<String> spheresWithFile = this.peer.getXmldb().getSpheresWithFile(dataId);
		Vector<String> associatedClubdeals = new Vector<String>();
		for(String sphere : spheresWithFile) {
			if(!clubdeals.contains(sphere)) {
				continue;
			}
			associatedClubdeals.add(sphere);
		}
		return associatedClubdeals;
	}

}
