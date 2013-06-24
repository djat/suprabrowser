/**
 * 
 */
package ss.server.networking.processing.keywords;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;
import ss.common.GenericXMLDocument;
import ss.common.ListUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.SphereReference;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSphereMember;
import ss.server.networking.DialogsMainPeer;
import ss.util.DateTimeParser;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class KeywordsOrganizer {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeywordsOrganizer.class);
	
	private final ResearchComponentDataContainer container;
	
	private final String realName;
	
	private final DialogsMainPeer peer;

	private Vector<Document> highlightKeywords;

	private Hashtable<String, Vector> assetsWithKeywordTag;

	private String login;

	public KeywordsOrganizer( final ResearchComponentDataContainer container, final String realName, final DialogsMainPeer peer ){
		this.container = container;
		this.realName = realName;
		this.peer = peer;
		perform();
	}

	
	private Vector<Document> filterKeywordsRecent( final Vector<Document> keys, final ResearchComponentDataContainer container, final String loginName ){
		if (!container.isUseRecent()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Use recent is not setted");
			}
			return keys;
		}
		if (container.getNumberRecentTags() <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("NumberRecentTags is 0, returning not modified keys");
			}
			return keys;
		}
		if (container.getNumberRecentSpheres() <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("No recent spheres specified");
			}
			return keys;
		}
	
		if (logger.isDebugEnabled()) {
			logger.debug("NumberRecentSpheres is " + container.getNumberRecentSpheres());
		}
		
		final Vector<Document> inRecentSpheres = throughLastOpenedSpheres( keys , container.getNumberRecentSpheres() );
		tryToAddNotFromThisSpheres( inRecentSpheres, keys );
		return inRecentSpheres;
 	}
	
	/**
	 * @param inRecentSpheres
	 * @param filteredKeys
	 */
	private void tryToAddNotFromThisSpheres(final Vector<Document> inRecentSpheres, final Vector<Document> keys ) {
		for ( Document doc : keys ) {
			if (!inRecentSpheres.contains(doc)) {
				inRecentSpheres.add( doc );
			}
		}
	}

	private Vector<Document> throughLastOpenedSpheres( final Vector<Document> keys, final int number ){
		final Vector<Document> filteredKeys = new Vector<Document>();
		final List<String> sphereIdsAll = ResearchWatcher.INSTANCE.getLastOpenedSpheres( this.login );
		if ( (sphereIdsAll == null) || (sphereIdsAll.isEmpty()) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("There is no SpheresIds in throughLastOpenedSpheres");
			}
			return keys;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("sphereIdsAll: " + ListUtils.valuesToString( sphereIdsAll ));
		}
		final List<String> sphereIds = new ArrayList<String>();
		int index = 0;
		for (String s : sphereIdsAll) {
			sphereIds.add( s );
			if ( (++index) >= number ){
				break;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getted ids: " + ListUtils.valuesToString( sphereIds ));
		}
		
		for ( Document doc : keys ) {
			if ( VariousUtils.checkElementAttributeValuesExists(doc, "multi_loc_sphere", sphereIds) ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Tag added: " + Statement.wrap(doc).getSubject());
				}
				filteredKeys.add( doc );
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No location in tag: " + Statement.wrap(doc).getSubject());
				}
			}
		}
		
		return filteredKeys;
	}
//	
//	private Vector<Document> cutoffRecent( final Vector<Document> keys, final int number ){
//		if ( keys == null ) {
//			logger.error("keys is null");
//			return null;
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("Entrence in cut of: " + keys.size());
//		}
//		final Vector<Document> filteredKeys = new Vector<Document>();
//		int i = 0;
//		for (Document doc : keys) {
//			filteredKeys.add( doc );
//			if ( (++i) >= number) {
//				break;
//			}
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("Result in cut of: " + filteredKeys.size());
//		}
//		return filteredKeys;
//	}
	/**
	 * @param keys
	 * @param container
	 * @return
	 */
	private Vector<Document> filterKeywordsNewFromLastResearch( final Vector<Document> keys, final ResearchComponentDataContainer container, final String loginName ) {
		if ((keys == null) || (keys.isEmpty())) {
			if (logger.isDebugEnabled()) {
				logger.debug("No keywords in filterKeywords recieved");
			}
			return keys;
		}
		if (container.isNewFromLastResearch()){
			final Vector<Document> toRet = new Vector<Document>();
			final Date date = ResearchWatcher.INSTANCE.getLastReseached( loginName );
			if (date == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No reseach was for " + loginName + " before");
				}
				return keys;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Previous research was on " + date.toString());
			}
			final List<String> messagesIds = new ArrayList<String>();
			for( Document doc : keys ) {
				KeywordStatement st = KeywordStatement.wrap( (Document) doc );
				messagesIds.add( st.getMessageId() );
			}
			final List<Date> tagUseDates = this.peer.getXmldb().getUseDates( messagesIds );
			if (tagUseDates == null) {
				return keys;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("TagDates: " + ListUtils.valuesToString( tagUseDates ));
			}
			for (int i = 0; i < tagUseDates.size(); i++) {
				if (tagUseDates.get(i).after(date)){
					if (logger.isDebugEnabled()) {
						logger.debug("this is after: TagDate: " + tagUseDates.get(i).toString());
					}
					if (keys.size() > i) {
						toRet.add( keys.get( i ) );
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("this is before: TagDate: " + tagUseDates.get(i).toString());
					}
				}
			}
//			for( Document doc : keys ) {
//				
//				if (logger.isDebugEnabled()) {
//					logger.debug("Tag is: " + st.getSubject());
//				}
//				Date tagDate = DateTimeParser.INSTANCE.parseToDate(
//						st.getMoment());
//				if (tagDate == null) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("Tag date is null, adding by default");
//					}
//					toRet.add( doc );
//				} else {
//					if (tagDate.after(date)) {
//						if (logger.isDebugEnabled()) {
//							logger.debug("this is after: TagDate: " + tagDate.toString());
//						}
//						toRet.add( doc );
//					} else {
//						if (logger.isDebugEnabled()) {
//							logger.debug("this is before: TagDate: " + tagDate.toString());
//						}						
//					}
//				}
//			}
			return toRet;
		}
		return keys;
	}
	
	private void fillKeywords(){
		final List<String> contactNames = new ArrayList<String>();
		if (this.container.isLookInOwn()) {
			contactNames.add( this.realName );
		}
		
		if (this.container.isLookInOthers()) {
			if (this.container.getAllowedUsersContactNames() != null) {
				if (!this.container.getAllowedUsersContactNames().isEmpty()) {
					contactNames.addAll( this.container.getAllowedUsersContactNames() );
				}
			} else {
				Vector<String> conts = this.peer.getVerifyAuth().getContactsForMembersEnabled1(this.realName);
				if (( conts!=null ) && ( !conts.isEmpty() )) {
					contactNames.addAll( conts );
				}
			}
		}
		
		final List<String> homeSpheres = new ArrayList<String>();
		for (String s : contactNames) {
			homeSpheres.add( this.peer.getVerifyAuth().getSystemName( s , s ) );
		}
		if (homeSpheres.isEmpty()) {
			logger.warn("No home spheres");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("homeSpheres: " + ListUtils.allValuesToString( homeSpheres ));
		}

		this.highlightKeywords = new Vector<Document>();

		this.assetsWithKeywordTag = new Hashtable<String,Vector>();
		
		if (!homeSpheres.isEmpty()) {

			Vector<Document> keys = this.peer.getXmldb().getKeywords( homeSpheres );
			if (logger.isDebugEnabled()) {
				logger.debug("For sphere: " + ListUtils.valuesToString( homeSpheres ));
			}
			if (keys != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("keys size: " + keys.size());
				}
				this.highlightKeywords.addAll( 
						filterKeywordsNewFromLastResearch ( filterKeywordsRecent (keys, this.container, this.login ),
								this.container, this.login ) );
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("no keys");
				}
			}
		}
	}
	
	private void fillContacts(){
		if (this.container.isContactsAsKeywords()) {
			List<String> spheresToLook = new ArrayList<String>();
			List<SphereReference> memberList = this.peer.getVerifyAuth().getAllAvailablePrivateSpheres(this.login);
			if (memberList != null) {
				for (SphereReference sphere : memberList) {
					spheresToLook.add(sphere.getSystemName());
				}
			}
			List<String> groupList = this.peer.getVerifyAuth().getAvailableGroupSpheresId();
			if (groupList != null) {
				for (String s : groupList) {
					spheresToLook.add( s );
				}
			}

			List<String> contacts = new ArrayList<String>();

			List<SupraSphereMember> members = this.peer.getVerifyAuth().getAllMembers();
			for (SupraSphereMember member : members) {
				if (member.getContactName() != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("member.getContactName(): " + member.getContactName());
					}
					contacts.add( member.getContactName() );
				}
			}
			
			for ( String s : spheresToLook ) {
				Vector<Document> contactDocs = this.peer.getXmldb().getAllContactsForMembers(s);
				if (contactDocs != null) {
					for (Document d : contactDocs) {
						String c = ContactStatement.wrap( d ).getContactNameByFirstAndLastNames();
						if (!contacts.contains(c)) {
							if (logger.isDebugEnabled()) {
								logger.debug("not contains, adding: " + c);
							}
							contacts.add( c );
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("already contains: " + c);
							}
						}
					}
				}
			}
			
			this.container.setContactStrings( contacts );
			
			final String moment = DialogsMainPeer.getCurrentMoment();
			for (String c : contacts) {
				String messageId = VariousUtils.createMessageId();
				String unique = VariousUtils.createMessageId(); // Fake unique
				Document doc = GenericXMLDocument.createKeywordsDocMockUp(messageId, c, c, moment, unique);
				this.highlightKeywords.add( 0, doc );
			}
		}
	}
	
	private void perform(){
		
		init();
		
		fillKeywords();
		
		fillContacts();
		
		ResearchWatcher.INSTANCE.reseached( this.login );
	}

	private void init() {
		check( this.container , " ResearchComponentDataContainer is null ");
		check( this.realName , " RealName is null ");
		check( this.peer , " DialogsMainPeer is null ");
		this.login = this.peer.getVerifyAuth().getLoginForContact( this.realName );
	}
	
	private void check( final Object o, final String message) {
		if ( o == null ) {
			throw new NullPointerException( message );
		}
	}

	public Hashtable<String, Vector> getAssetsWithKeywordTag() {
		return this.assetsWithKeywordTag;
	}

	public Vector getHighlightKeywords() {
		return this.highlightKeywords;
	}
	
	public ResearchComponentDataContainer getContainer(){
		return this.container;
	}
}
