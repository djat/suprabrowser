/**
 * 
 */
package ss.server.functions.changecontact;

import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.AbstractDocument;

import ss.common.SphereReferenceList;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereMember;
import ss.domainmodel.SphereMemberCollection;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.VotedMember;
import ss.domainmodel.admin.AdminItem;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class ChangeContactAbstractFunction {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeContactAbstractFunction.class);
	
	private final DialogsMainPeer peer;
	
	public ChangeContactAbstractFunction( final DialogsMainPeer peer ){
		this.peer = peer;
	}
	
	protected void replaceInMembership( final String loginName, final String newContactName ){
		if (logger.isDebugEnabled()) {
			logger.debug("replaceInMembership perfoming");
		}
		final String loginSphereId = this.peer.getVerifyAuth().getLoginSphere(loginName);
		if ( loginSphereId == null ) {
			logger.error("loginSphereId is not found");
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("loginSphereId is: " + loginSphereId);
			}
		}
		final Document membership = this.peer.getXmldb().getMembershipDoc(loginSphereId, loginName);
		if ( membership == null ) {
			logger.error("membership is null");
			return;
		}
		final Element contactElement = membership.getRootElement().element("contact_name");
		if ( contactElement != null ) {
			contactElement.addAttribute("value",newContactName);
		} else {
			logger.error("contactElement element is null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Result membership: " + membership.asXML());
		}
		safeReplaceDoc(membership, loginSphereId);
	}
	
	protected void replaceInVoutingAndGiver( final String oldContactName, final String newContactName ){
		final SphereReferenceList sphereList = this.peer.getVerifyAuth().getAllSpheres();
		if ( sphereList == null ) {
			logger.error("sphereList is null");
			return;
		}
		for ( SphereReference sphere : sphereList ) {
			replaceInVoutingAndGiverInSphere(oldContactName, newContactName, sphere.getSystemName());
		}
	}
	
	protected void replaceInVoutingAndGiverInSphere(final String oldContactName, final String newContactName, final String sphereId){
		if ( sphereId == null ) {
			logger.error("sphereId is null");
			return;
		}
		final Vector<Document> messages = this.peer.getXmldb().getAllMessages(sphereId);
		if ( messages == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("messages is null for sphereId: " + sphereId);
			}
			return;
		}
		for ( Document doc : messages ) {
			if ( doc == null ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Doc is null, sphere id: " + sphereId);
				}
				continue;
			}
			Statement st = Statement.wrap( doc );
			boolean isReplace = false;
			if ( oldContactName.equals(st.getGiver()) ) {
				st.setGiver(newContactName);
				isReplace = true;
			}
			if ( st.getVotedMembers() != null ) {
				try {
					VotedMember member = st.getVotedMembers().getByContactName(oldContactName);
					if ( member != null ) {
						member.setName(newContactName);
						isReplace = true;
					}
				} catch ( Exception ex ){
					logger.error("Exception in changing vouting model",ex);
				}
			}
			if ( isReplace ) {
				safeReplaceDoc(st.getBindedDocument(), sphereId);
			}
		}
	}
	
	protected Document safeReplaceDoc( final Document doc, final String sphereId ){
		if ( doc == null ) {
			logger.error("doc is null");
			return null;
		}
		if ( sphereId == null ) {
			logger.error("sphereId is null");
			return null;
		}
		try {
			return this.peer.getXmldb().replaceDoc( doc, sphereId );
		} catch (Exception ex) {
			logger.error( "error in replacing document in sphere: " + sphereId, ex );
			return null;
		}
	}
	
	protected void replaceInSphereDefinitions( final String oldContactName, final String newContactName ){
		final Vector<Document> spheres = this.peer.getXmldb().getAllSpheres();
		if ( (spheres == null) || (spheres.isEmpty())) {
			logger.error("No sphere definitions returned");
			return;
		}
		for ( Document doc : spheres ) {
			SphereStatement sphere = SphereStatement.wrap(doc);
			SphereMemberCollection members = sphere.getSphereMembers();
			for (SphereMember sphereMember : members) {
				if ( oldContactName.equals(sphereMember.getContactName()) ) {
					sphereMember.setContactName(newContactName);
					safeReplaceDoc(sphere.getBindedDocument(), sphere.getCurrentSphere());
					continue;
				}
			}
		}
	}
	
	protected void replaceContactNamesInContacts( final String oldContactName, final String newFirstName, final String newLastName ){
		final AbstractDocument ids = this.peer.getXmldb().getContactMessageIds(oldContactName);
		if ( ids == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("contact ids is null");
			}
			return;
		}
		Element root = ids.getRootElement();
		for(Object el : root.elements("element")) {
			String sphereId = ((Element)el).attributeValue("sphere_id");
			String messageId = ((Element)el).attributeValue("message_id");
			Document contactDoc = this.peer.getXmldb().getDocByMessageId(messageId, sphereId);
			if ( contactDoc != null ) {
				ContactStatement st = ContactStatement.wrap( contactDoc );
				if (st.isContact() && oldContactName.equals(st.getContactNameByFirstAndLastNames())) {
					st.setFirstName( newFirstName );
					st.setLastName( newLastName );
					st.setSubject( st.getContactNameByFirstAndLastNames() );
					safeReplaceDoc(st.getBindedDocument(), sphereId);
				}
			}
		}
	}
	
	protected void replaceAllInfoInContacts( final String oldContactName, final ContactStatement newContactSt ){
		final AbstractDocument ids = this.peer.getXmldb().getContactMessageIds(oldContactName);
		if ( ids == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("contact ids is null");
			}
			return;
		}
		Element root = ids.getRootElement();
		for(Object el : root.elements("element")) {
			String sphereId = ((Element)el).attributeValue("sphere_id");
			String messageId = ((Element)el).attributeValue("message_id");
			Document contactDoc = this.peer.getXmldb().getDocByMessageId(messageId, sphereId);
			if ( contactDoc != null ) {
				ContactStatement st = ContactStatement.wrap( contactDoc );
				if (st.isContact() && oldContactName.equals(st.getContactNameByFirstAndLastNames())) {
					
					st.copyFields( newContactSt );

					safeReplaceDoc(st.getBindedDocument(), sphereId);
				}
			}
		}
	}
	
	protected void AddChangesToSupraSphereDoc( final String login, final String oldContactName, final String newContactName ) {
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
		} catch(DocumentException ex) {
			logger.error("Can not get suprasphere document", ex);
			return;
		}
		if ( supraDoc == null ) {
			logger.error("SupraDoc is null");
			return;
		}
		final SupraSphereStatement supra = SupraSphereStatement.wrap( supraDoc );
		for (SupraSphereMember member : supra.getSupraMembers()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Processing member: " + member.getContactName());
			}
			if (oldContactName.equals( member.getContactName() )) {
				member.setContactName( newContactName );
			}
			final SphereItem item = member.getSphereByDisplayName( oldContactName );
			if ( item != null ) {
				item.setDisplayName( newContactName );
			}
		}
		final AdminItem admin = supra.getAdmins().getAdminByLoginAndContact(login, oldContactName);
		if ( admin != null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("This contact is admin");
			}
			admin.setContact( newContactName );
		}
		
		this.peer.getXmldb().updateSupraSphereDoc(supra.getBindedDocument());
	}
	
	protected void updateVerifyAuth() {
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

	protected DialogsMainPeer getPeer() {
		return this.peer;
	}
}
