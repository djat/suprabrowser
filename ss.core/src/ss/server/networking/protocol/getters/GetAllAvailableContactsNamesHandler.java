/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllVisibleContactsNames;
import ss.domainmodel.ContactStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetAllAvailableContactsNamesHandler extends
		AbstractGetterCommandHandler<GetAllVisibleContactsNames, ArrayList<String>> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetAllAvailableContactsNamesHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetAllAvailableContactsNamesHandler( final DialogsMainPeer peer ) {
		super(GetAllVisibleContactsNames.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected ArrayList<String> evaluate(GetAllVisibleContactsNames command)
			throws CommandHandleException {
		final ArrayList<String> contactNames = new ArrayList<String>();
		final boolean isAdmin = this.peer.getVerifyAuth().isAdmin();
		if (logger.isDebugEnabled()) {
			logger.debug( "isAdmin : " + isAdmin );
		}
		final String selfContact = this.peer.getVerifyAuth().getContactStatement().getContactNameByFirstAndLastNames();
		if ( selfContact == null ){
			logger.error("selfContact is null, returning empty list");
			return contactNames;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("selfContact : " + selfContact );
		}
		final Vector<Document> contacts = this.peer.getXmldb().getAllContacts();
		if ( contacts == null ) {
			logger.error("contacts is null, returning empty list");
			return contactNames;
		}
		if ( contacts.isEmpty() ) {
			if (logger.isDebugEnabled()) {
				logger.debug("contacts is empty");
			}
			return contactNames;
		}
		final List<String> sphereIds = isAdmin ? null : this.peer.getVerifyAuth().getAllSpheresByContactName(selfContact).toSpheresIds();
		if (logger.isDebugEnabled()) {
			logger.debug("sphereIds size: " + ((sphereIds == null) ? "not spoecified, because admin" : sphereIds.size()) );
		}
		addAllToNames( contactNames , contacts, sphereIds );
		if (logger.isDebugEnabled()) {
			logger.debug(" * Result size: " + contactNames.size() );
			int index = 0;
			for ( String s : contactNames ) {
				logger.debug("" + (++index) + " : " + s);
			}
		}
		return contactNames;
	}
	
	private void addAllToNames( final List<String> names, final List<Document> docs, final List<String> filterSpheres ) {
		for ( Document doc : docs ){
			ContactStatement st = ContactStatement.wrap( doc );
			if (logger.isDebugEnabled()) {
				logger.debug("Next contact : " + st.getContactNameByFirstAndLastNames() );
			}
			if ( (filterSpheres == null) || (filterSpheres.contains(st.getCurrentSphere())) ) {
				if (logger.isDebugEnabled()) {
					logger.debug(" - allowed - ");
				}
				addToNames( names, st );
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(" - not allowed - ");
				}
			}
		}
	}
	
	private void addToNames( final List<String> names, final ContactStatement st ){
		if ( !st.isContact() ) {
			logger.error( "Not contact : " + st.getBindedDocument().asXML() );
			return;
		}
		final String contactName = st.getContactNameByFirstAndLastNames();
		if ( !names.contains( contactName ) ){
			if (logger.isDebugEnabled()) {
				logger.debug(" - Not in list yet, added - ");
			}
			names.add( contactName );
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(" - Already in list - ");
			}
		}
	}
}
