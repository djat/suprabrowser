/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.memberaccess.ClubDealMemberBundle;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereMember;
import ss.domainmodel.clubdeals.ClubDeal;
import ss.domainmodel.clubdeals.ClubDealUtils;
import ss.domainmodel.clubdeals.ClubdealCollection;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.domainmodel.configuration.ClubdealContactTypeCollection;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.ModerateAccessMember;
import ss.domainmodel.configuration.ModerationAccessModel;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class ClubdealManager {
	
	private static final Logger logger = SSLogger.getLogger(ClubdealManager.class);
	
	private final Set<ContactStatement> members;
	
	private ClubdealCollection clubdeals;
	
	private final Set<ClubDeal> sphereToRemove = new HashSet<ClubDeal>();
	
	private final List<ClubDealMemberBundle> contactsToRemove = new ArrayList<ClubDealMemberBundle>();
	
	private final List<ClubDealMemberBundle> contactsToAdd = new ArrayList<ClubDealMemberBundle>();
	
	private final List<ClubDealMemberBundle> contactsToChange = new ArrayList<ClubDealMemberBundle>();
	
	private final List<ClubDealMemberBundle> typesForContacts = new ArrayList<ClubDealMemberBundle>();
	
	private ClubdealContactTypeCollection types = SsDomain.CONFIGURATION.getMainConfigurationValue().getClubdealContactTypes();
	
	private final DialogsMainCli client;
	
	private boolean includeAllClubDeals = false;
	
		
	public ClubdealManager(){
		this(SupraSphereFrame.INSTANCE.client);
	}
	
	public ClubdealManager(final DialogsMainCli client){
		this.client = client;
		this.members = new TreeSet<ContactStatement>(new Comparator<ContactStatement>(){

			public int compare( final ContactStatement c1, final ContactStatement c2 ) {
				return c1.getContactNameByFirstAndLastNames().compareToIgnoreCase(c2.getContactNameByFirstAndLastNames());
			}
			
		});
	}
		
	public void setUp(boolean includeAllClubDeals) {
		this.includeAllClubDeals = includeAllClubDeals;
		setUp();
	}
	
	public void setUp() {
		clearAllLists();
		if(this.client.isAdmin()) {
			this.members.addAll(this.client.getDistinctContactDocs());
		}	else {
			this.members.addAll(this.client.getAvailableContactDocs());
		}
		if ( this.includeAllClubDeals ) {
			this.clubdeals = ClubDealUtils.INSTANCE.getRealyAllClubdeals(this.client);
		}
		else {
			this.clubdeals = ClubDealUtils.INSTANCE.getAllAvaliableClubdealsForUser(this.client);
		}
		initContactTypes();
	}
	/**
	 * 
	 */
	private void clearAllLists() {
		clearLists();
		this.types = SsDomain.CONFIGURATION.getMainConfigurationValue().getClubdealContactTypes();
		this.contactsToChange.clear();
		this.typesForContacts.clear();
		this.sphereToRemove.clear();
	}
	/**
	 * 
	 */
	private void initContactTypes() {
		this.types.addType(SphereMember.NO_TYPE);
		for(ClubdealWithContactsObject cd : this.clubdeals) {
			for(ContactStatement contact : cd.getContacts()) {
				this.types.addType(contact.getRole());
			}
		}
	}

	public ClubdealCollection getAllClubdeals() {
		return this.clubdeals;
	}
	
	public List<ClubdealWithContactsObject> getClubdeals(final String contactName) {
		if(contactName==null) {
			return new ArrayList<ClubdealWithContactsObject>();
		}
		return this.clubdeals.getClubdealsForContact(contactName);
	}
	
	public Set<ContactStatement> getMembers() {
		return this.members;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public String getContactType(final ClubDeal cd, final String contactName) {
		return cd.getMemberByContactName(contactName).getType();
	}

	/**
	 * @param cd
	 * @param contactName
	 */
	public void putContactToRemoveList(final String id, final ContactStatement contact) {
		boolean changed = this.clubdeals.getClubdealById(id).removeContact(contact);
		if(!changed) {
			return;
		}
		ClubDealMemberBundle bundle = new ClubDealMemberBundle(id, contact);
		this.contactsToRemove.add(bundle);
	}

	/**
	 * @param cd
	 * @param contactName
	 */
	public void putContactToAddList(final String id, final ContactStatement contact) {
		boolean changed = this.clubdeals.getClubdealById(id).addContact(contact);
		if(!changed) {
			return;
		}
		ClubdealWithContactsObject cd = this.clubdeals.getClubdealById(id);
		ClubDealMemberBundle bundle = new ClubDealMemberBundle(cd.getClubdealSystemName(), contact);
		this.contactsToAdd.add(bundle);
	}
	
	public void typeForContactChanged( final ClubDeal deal, final ContactStatement contact, final String type, final boolean existedReference ){
		if ( deal == null ) {
			logger.error("Club Deal is null");
		}
//		logger.debug("typeForContactChanged, contact: " + contact.getContactNameByFirstAndLastNames() + ", type: " + type + ", existedReference: " + existedReference);
		final ClubDealMemberBundle bundleNew = new ClubDealMemberBundle(deal.getSystemName(), contact, type);
		boolean existed = false;
		for (ClubDealMemberBundle bundle : this.typesForContacts) {
			if (bundle.equals(bundleNew)) {
				bundle.setType( type );
				existed = true;
				break;
			}
		}
		if (!existed) {
			this.typesForContacts.add( bundleNew );
		}
		if ( existedReference ) {
			if (!this.contactsToChange.contains( bundleNew )) {
				this.contactsToChange.add( bundleNew );
			}
		}
	}
	
	/**
	 * @param clubdeal
	 * @param existedContact
	 * @return
	 */
	public String getChangedType(final String clubdealId, ContactStatement existedContact) {
		final ClubDealMemberBundle bundleNew = new ClubDealMemberBundle( clubdealId, existedContact );
		for (ClubDealMemberBundle bundle : this.typesForContacts) {
			if (bundle.equals(bundleNew)) {
				return bundle.getType();
			}
		}
		return null;
	}


	/**
	 * 
	 */
	public void saveContactType(final ClubDeal cd, final String contactName, final String type) {
		cd.getMemberByContactName(contactName).setType(type);
	}

	/**
	 * @param selectedClubdeal
	 */
	public void putClubdealToRemoveList(ClubdealWithContactsObject selectedClubdeal) {
		if (logger.isDebugEnabled()) {
			logger.debug("selected:"+selectedClubdeal);
		}
		if(selectedClubdeal==null) {
			return;
		}
		this.clubdeals.remove(selectedClubdeal);
		this.sphereToRemove.add(selectedClubdeal.getClubdeal());
	}

	/**
	 * @param cd
	 */
	@SuppressWarnings("unchecked")
	public void addClubdeal( final ClubDeal sphere, final String  prefEmailAlias) {
		this.clubdeals.add( new ClubdealWithContactsObject(sphere) );
		final Hashtable sessionCopy = (Hashtable)this.client.session.clone();
		sessionCopy.put("delivery_type", "normal");
		if(StringUtils.isNotBlank(prefEmailAlias)) {
			sessionCopy.put(SessionConstants.EMAIL_ALIAS, prefEmailAlias);
		}
		sessionCopy.put(SessionConstants.SPHERE_ID2, sphere.getCurrentSphere());
		this.client.openSphereForMembers(
				sessionCopy, sphere.getBindedDocument(),
				new Vector<Document>(), sphere.getSystemName(), sphere.getDisplayName());
		
		this.client.updateClubdealVisibilityForAdmin(sphere);
	}

	/**
	 * @param prefId
	 * @return
	 */
	boolean checkNameAvailable( final String name ) {
		for(ClubdealWithContactsObject cd : getAllClubdeals()) {
			if( cd.getClubDealDisplayName().equals( name ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param selection
	 * @return
	 */
	public ClubdealWithContactsObject getClubdealByStringId(final String strId) {
		if(strId==null) {
			return null;
		}
		return this.clubdeals.getClubdealById(strId);
	}

	/**
	 * 
	 */
	public void saveToServer() {
		removeContacts();

		addContacts();
		
		changeContacts();
		
		removeSpheres();

		clearLists();
	}

	/**
	 * @param client
	 */
	private void removeSpheres() {
		for(ClubDeal cd : this.sphereToRemove) {
			try {
				this.client.removeSphere(this.client.session, cd.getBindedDocument());
			} catch ( Exception ex ){
				logger.error("Error in removing club deals", ex);
			}
		}
	}

	/**
	 * @param client
	 */
	@SuppressWarnings("unchecked")
	private void removeContacts() {
		ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
		for ( ClubDealMemberBundle bundle : this.contactsToRemove) {
			try {
				Hashtable session = (Hashtable) this.client.session.clone();
				session.put(SessionConstants.SPHERE_ID2, bundle.getClubdealId());
				this.client.recallContactMessage(bundle.getClubdealId(), bundle
						.getContact());
				if(this.client.getVerifyAuth().isAdmin(bundle.getContact())) {
					continue;
				}
				ModerationAccessModel model = configuration.getClubdealModerateAccesses().getBySystemName(bundle.getClubdealId());
				ModerateAccessMember member = model.getMemberList().getMemberByContactName(bundle.getContact().getContactNameByFirstAndLastNames());
				if(member!=null && member.isModerator()) {
					this.client.updateClubdealVisibilityForMember(
							getAllClubdeals().getClubdealById(bundle.getClubdealId()).getClubdeal()
										, bundle.getContact().getContactNameByFirstAndLastNames(), false);
				}
				member.setModerator(false);
			} catch ( Exception ex ){
				logger.error("Error in removing contacts", ex);
			}
		}
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
	}

	/**
	 * @param client
	 */
	private void addContacts() {
		for ( ClubDealMemberBundle bundle : this.contactsToAdd) {
			try {
				List<String> sphereIds = new ArrayList<String>();
				sphereIds.add( bundle.getClubdealId() );
				List<Document> docsToForward = new ArrayList<Document>();
				ContactStatement st = bundle.getContact();
				final String type = getChangedType(bundle.getClubdealId(), st);
				if (StringUtils.isNotBlank( type )) {
					st.setRole( type );
				}
				docsToForward.add(bundle.getContact().getBindedDocument());

				this.client.forwardMessagesSubTree(sphereIds, docsToForward);
			} catch ( Exception ex ){
				logger.error("Error in adding contacts", ex);
			}
		}
	}

	/**
	 * @param client
	 */
	@SuppressWarnings("unchecked")
	private void changeContacts() {
		for ( ClubDealMemberBundle bundle : this.contactsToChange ) {
			try {
				if (this.contactsToRemove.contains( bundle )) {
					continue;
				}
				
				final Hashtable hash = (Hashtable) this.client.session.clone();
				hash.put(SessionConstants.SPHERE_ID2, bundle.getClubdealId());
				ContactStatement st = bundle.getContact();
				final String type = getChangedType(bundle.getClubdealId(), st);
				if (StringUtils.isNotBlank( type )) {
					st.setRole( type );
				}
				this.client.replaceDoc(hash, st.getBindedDocument() );
			} catch ( Exception ex ){
				logger.error("Error in changing contacts", ex);
			}
		}
	}

	/**
	 * 
	 */
	private void clearLists() {
		this.contactsToAdd.clear();
		this.contactsToRemove.clear();
	}

	public ClubdealContactTypeCollection getContactTypes() {
		return this.types;
	}

	/**
	 * @param name
	 */
	public void addNewType(String name) {
		this.types.addType(name);
		ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
		configuration.getClubdealContactTypes().addType(name);
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
	}
	
	public boolean removeType(final String type) {
		return renameType(type, "");
	}

	public boolean renameType(final String oldName, final String newName) {
		if(oldName==null || oldName.equals(SphereMember.NO_TYPE)) {
			return false;
		}
		if(newName==null || newName.equals(SphereMember.NO_TYPE)) {
			return false;
		}
		ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
		configuration.getClubdealContactTypes().removeType(oldName);
		configuration.getClubdealContactTypes().addType(newName);
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
		
		boolean shouldRefresh = this.client.renameContactType(oldName, newName);
		if(!shouldRefresh) {
			return false;
		}
		setUp();
		return true;
	}

	/**
	 * @return
	 */
	public DialogsMainCli getClient() {
		return this.client;
	}
}