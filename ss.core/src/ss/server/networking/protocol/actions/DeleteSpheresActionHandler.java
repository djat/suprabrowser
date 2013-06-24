/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.networking.protocol.actions.DeleteSpheresAction;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.search.LuceneSearch;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class DeleteSpheresActionHandler extends AbstractActionHandler<DeleteSpheresAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeleteSpheresActionHandler.class);
	
	public DeleteSpheresActionHandler(DialogsMainPeer peer) {
		super(DeleteSpheresAction.class, peer);
	}
	
	@Override
	protected void execute(DeleteSpheresAction action) {
		if (logger.isDebugEnabled()) {
			logger.debug("start delete sphere");
		}
		Vector<String> spheres = action.getSpheresList();
		Hashtable session = action.getSessionArg();
		
		this.peer.getXmldb().conditionalDeleteSphereDefinitions(spheres);
		if ( action.isRemoveTotally() ) {
			this.peer.getXmldb().deleteSphereMessagesFromDataBase(spheres);
		}

		LuceneSearch.deletedSpheres( spheres );
		
		updateSupraSphereDoc(spheres);
		
		updateVerifyAuth(session);
		
		if (logger.isDebugEnabled()) {
			logger.debug("finished delete sphere");
		}
	}

	private void updateSupraSphereDoc(Vector<String> spheres) {
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			if (logger.isDebugEnabled()) {
				logger.debug("before: "+SupraSphereStatement.wrap(supraDoc));
			}
		} catch(DocumentException ex) {
			
		}
		
		final SupraSphereStatement supraSt = SupraSphereStatement.wrap(supraDoc);
		
		for(String sphereId : spheres) {
			for(SupraSphereMember member : supraSt.getSupraMembers()) {
				SphereItem itemToRemove = member.getSphereBySystemName(sphereId);
				if ( itemToRemove != null ) { 
					member.removeItem(itemToRemove);
				}
			}
			SphereEmail sphereEmail = supraSt.getSpheresEmails().getSphereEmailBySphereId(sphereId);
			if ( sphereEmail != null ) {
				supraSt.getSpheresEmails().remove(sphereEmail);
			}
		}
		
		this.peer.getXmldb().updateSupraSphereDoc(supraSt.getBindedDocument());
		
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("after: "+XmlDocumentUtils.toPrettyString(this.peer.getXmlDbOld().getSupraSphereDocument()));
			} catch (DocumentException ex) {
				logger.error("document ex", ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateVerifyAuth(Hashtable session) {
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			
		} catch(DocumentException ex) {
			logger.error("can't update verify:(", ex);
		}		
		
		if(supraDoc==null) {
			logger.warn("supra doc from database is null");
			return;
		}
		
		this.peer.getVerifyAuth().setSphereDocument(supraDoc);
		DialogsMainPeer.updateVerifyAuthForAll(supraDoc);
	}
}
