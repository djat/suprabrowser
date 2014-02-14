/**
 * 
 */
package ss.domainmodel.clubdeals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.configuration.ClubdealContactType;
import ss.domainmodel.configuration.ClubdealContactTypeCollection;
import ss.global.SSLogger;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author roman
 *
 */
public class ClubDealUtils {

	private final static Logger logger = SSLogger.getLogger(ClubDealUtils.class);
	
	public static ClubDealUtils INSTANCE = new ClubDealUtils();
	
	private ClubDealUtils() {
		super();
	}
	
	public ClubDeal createClubDeal( final DialogsMainCli client, final String prefName ) {
		final ClubDeal clubdeal = new ClubDeal();
		
		clubdeal.setSphereCoreId(client.getVerifyAuth().getSupraSphereName());
		
		clubdeal.setDefaultType("terse");
		clubdeal.setDefaultDelivery("normal");
		clubdeal.setDisplayName(prefName);
		clubdeal.setSystemName(VariousUtils.createMessageId());
		clubdeal.setSphereType(SphereType.GROUP);
		clubdeal.setSubject(prefName);
		clubdeal.setExpiration("all");
		
		clubdeal.setTerseEnabled(true);
		clubdeal.setTerseModify("own");
		clubdeal.setMessageEnabled(true);
		clubdeal.setMessageModify("own");
		clubdeal.setBookmarkEnabled(true);
		clubdeal.setBookmarkModify("own");
		clubdeal.setExternalEmailEnabled(true);
		clubdeal.setExternalEmailModify("own");
		clubdeal.setContactEnabled(true);
		clubdeal.setContactModify("own");
		clubdeal.setRssEnabled(true);
		clubdeal.setRssModify("own");
		clubdeal.setKeywordsEnabled(true);
		clubdeal.setKeywordsModify("own");
		clubdeal.setFileEnabled(true);
		clubdeal.setFileModify("own");
		clubdeal.setSphereEnabled(false);
		clubdeal.setSphereModify("own");
		
		clubdeal.setVotingModelDesc("Absolute without qualification");
		clubdeal.setVotingModelType("absolute");
		clubdeal.setSpecificMemberContactName("__NOBODY__");
		clubdeal.setTallyNumber("0.0");
		clubdeal.setTallyValue("0.0");
		clubdeal.setThreadType("sphere");
		clubdeal.setType("sphere");
		clubdeal.setGiver((String) client.session.get(SessionConstants.REAL_NAME));
		clubdeal.setGiverUsername((String) client.session.get(SessionConstants.USERNAME));
		
		return clubdeal;
	}
	
	
	/**
	 * 
	 * @param client
	 * @param accessResolver if null then all clubdeals will be avaliable
	 * @return
	 */
	public ClubdealCollection getAllClubdeals(final DialogsMainCli client, ClubdealAccessResolver accessResolver ) {
		List<ClubdealWithContactsObject> clubdeals = new ArrayList<ClubdealWithContactsObject>();
		Set<String> processedIds = new HashSet<String>();
		Vector<Document> sphereDocs = client.getAllSpheres();
		if ( sphereDocs == null ) {
			return new ClubdealCollection(clubdeals);
		}
		for(Document doc : sphereDocs) {
			if (SphereStatement.wrap(doc).isDeleted()){
				continue;
			}
			ClubDeal sphere = ClubDeal.wrap(doc);
			if(sphere.isClubDeal() && !processedIds.contains(sphere.getSystemName())) {
				ClubdealWithContactsObject cd = new ClubdealWithContactsObject(sphere);
				Vector<Document> contactsDocs = client.getAllContacts(client.session, cd.getClubdealSystemName());
				if ( (contactsDocs != null ) ) {
					for(Document contactDoc : contactsDocs) {
						cd.addContact(ContactStatement.wrap(contactDoc));
					}
				}
				if ( accessResolver == null || accessResolver.hasAccess( cd ) ) {
					clubdeals.add(cd);
				}
				processedIds.add(sphere.getSystemName());					
			}
		}
		return new ClubdealCollection(clubdeals);
	}

	public ClubdealCollection getRealyAllClubdeals(final DialogsMainCli client) {
		return getAllClubdeals(client, null );
	}
	
	/**
	 * @return
	 */
	public ClubdealCollection getAllAvaliableClubdealsForUser(final DialogsMainCli client) {
		return getAllClubdeals(client, new ClubdealAccessResolver(client) );
	}

	public ClubdealCollection getContactClubdealsForSearch(final DialogsMainCli client, final String contact ) {
		String login = client.getVerifyAuth().getLoginForContact(contact);
		if(client.getVerifyAuth().isAdmin(contact, login)) {
			return getAllAvaliableClubdealsForUser(client);
		}
		return getClubdealsForContact(client, contact);
	}
	
	public ClubdealCollection getClubdealsForContact(final DialogsMainCli client, final String contact ) {
		ClubdealCollection collection = getAllAvaliableClubdealsForUser(client);
		ClubdealCollection resultCollection = new ClubdealCollection();
		for(ClubdealWithContactsObject cd : collection) {
			if (cd.hasContact(contact)) {
				resultCollection.add(cd);
			}
		}
		return resultCollection;
	}
	
	public ClubdealCollection getFullClubdealsForContact(final DialogsMainCli client, final String contact ) {
		ClubdealCollection collection = getRealyAllClubdeals(client);
		ClubdealCollection resultCollection = new ClubdealCollection();
		for(ClubdealWithContactsObject cd : collection) {
			if (cd.hasContact(contact)) {
				resultCollection.add(cd);
			}
		}
		return resultCollection;
	}
	
	public Vector<Document> getAssociatedFiles(final DialogsMainCli client, final ClubDeal cd) {
		return client.getAssociatedFilesForClubdeal(cd);
	}

	/**
	 * @param id
	 * @return
	 */
	public ClubdealWithContactsObject getClubDealById(final DialogsMainCli client, String id) {
		if(StringUtils.isBlank(id)) {
			logger.error("Null argument! Can't return not null Club Deal!!!");
			return null;
		}
		ClubdealCollection collection = getAllAvaliableClubdealsForUser(client);
		return collection.getClubdealById(id);
	}

	/**
	 * @return
	 */
	public static List<String> getAllContactTypes() {
		final List<String> result = new ArrayList<String>();
		try {
			final ClubdealContactTypeCollection types = SsDomain.CONFIGURATION.getMainConfigurationValue().getClubdealContactTypes();
			if ( types != null ) {
				for ( ClubdealContactType type : types ) {
					result.add( type.getName() );
				}
			}
		} catch ( Throwable ex ) {
			logger.error("Error in determing contact type list, empty list could be returned", ex);
		}
		return result;
	}
}
