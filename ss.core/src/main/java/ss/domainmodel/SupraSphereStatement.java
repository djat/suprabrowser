package ss.domainmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Element;

import ss.common.IdentityUtils;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.admin.AdminsCollection;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SupraSphereStatement extends Statement {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereStatement.class);
	
	private final ISimpleEntityProperty name = super
		.createAttributeProperty( "@name" );
	
	private final ISimpleEntityProperty displayName = super
		.createAttributeProperty( "inherit/data/sphere/@display_name" );
	
	private final ISimpleEntityProperty systemName = super
		.createAttributeProperty( "inherit/data/sphere/@system_name" );
	
	private final ISimpleEntityProperty middleChat = super
		.createAttributeProperty( "ui/middle_chat/@value" );

	private final ISimpleEntityProperty treeOrder = super
		.createAttributeProperty( "ui/tree_order/@value" );
	
	private final ISimpleEntityProperty sphereDomain = super
		.createAttributeProperty( "email/sphere_domain/@value" );
	
//	private final ISimpleEntityProperty adminContactName = super
//		.createAttributeProperty( "admin/supra/@contact_name" );
//	
//	private final ISimpleEntityProperty adminLoginName = super
//		.createAttributeProperty( "admin/supra/@login_name" );
	
	private final ISimpleEntityProperty privilegeName = super
		.createAttributeProperty( "admin/privileges/privilege/@name" );
	
	private final UserStatementCollection userCollection = super
		.bindListProperty( new UserStatementCollection(), "admin/privileges/privilege" );
	
	private final SupraSphereMemberCollection supraMembers = super
					.bindListProperty(new SupraSphereMemberCollection());

	private final SphereEmailCollection spheresEmails = super
		.bindListProperty(new SphereEmailCollection(), "spheres-emails" );
	
	private final AdminsCollection admins = super
		.bindListProperty(new AdminsCollection(), "admin" );
 
	
	public SupraSphereStatement() {
		super("suprasphere");
	}
	
	public static SupraSphereStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SupraSphereStatement.class);
	}
	
	public static SupraSphereStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SupraSphereStatement.class);
	}
	
	public AdminsCollection getAdmins(){
		return this.admins;
	}
	
	public String getDisplayName() {
		return this.displayName.getValue();
	}
	
	public void setDisplayName(String value) {
		this.displayName.setValue(value);
	}
	
	public String getSystemName() {
		return this.systemName.getValue();
	}
	
	public void setSystemName(String value) {
		this.systemName.setValue(value);
	}
	
	public boolean getMiddleChat() {
		return this.middleChat.getBooleanValue();
	}
	
	public void setMiddleChat(boolean value) {
		this.middleChat.setBooleanValue(value);
	}
	
	public String getTreeOrder() {
		return this.treeOrder.getValue();
	}
	
	public void setTreeOrder(String value) {
		this.treeOrder.setValue(value);
	}
	
	public String getSphereDomain() {
		return this.sphereDomain.getValue();
	}
	
	public void setSphereDomain(String value) {
		this.sphereDomain.setValue(value);
	}
	
	public String getPrivilegeName() {
		return this.privilegeName.getValue();
	}
	
	public void setPrivilegeName(String value) {
		this.privilegeName.setValue(value);
	}
	
	public UserStatement getUser(int index) {
		return this.userCollection.get(index);
	}
	
	public int getUserCount() {
		return this.userCollection.getCount();
	}
	
	public UserStatementCollection getUserCollection() {
		return this.userCollection;
	}
	
	public void addUser(UserStatement user) {
		this.userCollection.add(user);
	}
	
	public void removeUser(UserStatement user) {
		this.userCollection.remove(user);
	}
	
	public void removeUser(int index) {
		if(!(index<0 || index>=getUserCount()))
			this.userCollection.remove(getUser(index));
	}
	
	public SupraSphereMemberCollection getSupraMembers() {
		return this.supraMembers;
	}
	 
	/**
	 * @return the spheresEmails
	 */
	public final SphereEmailCollection getSpheresEmails() {
		return this.spheresEmails;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name.getValue();
	}

	public void enableSphereForMember(String sphereSystemName, String loginName) {
		final String xpath = "//suprasphere/member[@login_name=\"" + loginName
    		+ "\"]/sphere[@system_name=\"" + sphereSystemName + "\"]";
		Element elem = XmlDocumentUtils.selectElementByXPath(getBindedDocument(), xpath);
		if ( elem != null ) {
			elem.addAttribute("enabled", "true");
		}
	}

	/**
	 * @param userName
	 */
	public SupraSphereMember getSupraMemberByLoginName(String userName) {
		for(SupraSphereMember member : getSupraMembers()) {
			if(member.getLoginName().equals(userName)) {
				return member;
			}
		}
		return null;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public List<SupraSphereMember> getMembersForSphere(String sphereId) {
		final List<SupraSphereMember> members = new ArrayList<SupraSphereMember>();
		for( SupraSphereMember member : this.getSupraMembers() ) {
			if ( member.getSpheres().isEnabled(sphereId) ) {
				members.add(member);
			}
		}
		return members;
	}
	
	/**
	 * @param targetSphere
	 * @return
	 */
	public Collection<String> getMembersContantNamesFor(String sphereId) {
		return SupraSphereMember.getContactNames( getMembersForSphere( sphereId ) );
	}
	
	public String generateFreeSphereId() {
		for(;;) {
			final String sphereId = String.valueOf( IdentityUtils.generateLongId() );
			if ( getMembersForSphere(sphereId).size() > 0 ) {
				logger.warn( "generateFreeSphereId cautch duplicate " + sphereId );
			}
			else {
				return sphereId;
			}
		}			
	}
	
	public Hashtable<MemberRelation,PrivateSphereReference> getExistedPrivateSpheres( List<SupraSphereMember> members ) {
		final Hashtable<MemberRelation,PrivateSphereReference> relationsToSpheres = new Hashtable<MemberRelation,PrivateSphereReference>();
		for ( SupraSphereMember member : members ) {
			for (SphereItem sphere : member.getSpheres()) {
				if ( sphere.getSphereType() == SphereType.MEMBER ) {
					final MemberRelation relation = new MemberRelation( member.getContactName(), sphere.getDisplayName() );
					PrivateSphereReference sphereReference = relationsToSpheres.get(relation);
					if ( sphereReference == null ) {
						sphereReference = new PrivateSphereReference( sphere.getSystemName(), relation, false );
					}
					else {
						sphereReference.setBackwarkRelation(relation);
					}
					relationsToSpheres.put( relation, sphereReference );					
				}
			}
		}
		return relationsToSpheres;
	}
	
	/**
	 * @param members
	 * @return
	 */
	public List<PrivateSphereReference> findMissedPrivateSpheres(List<SupraSphereMember> members) {
		final Set<String> privateSpheres = SupraSphereMember.getContactNames( members );
		final List<PrivateSphereReference> missedSpheres = new ArrayList<PrivateSphereReference>();
		final Set<MemberRelation> missedRelations = new HashSet<MemberRelation>();	
		for ( SupraSphereMember member : members ) {
			final Set<String> missedPrivateSpheres = new TreeSet<String>(
					privateSpheres);
			for (SphereItem sphere : member.getSpheres()) {
				missedPrivateSpheres.remove(sphere.getDisplayName());
			}
			if (missedPrivateSpheres.size() > 0 ) {
				for (String missedPrivateSphere : missedPrivateSpheres) {
					MemberRelation missedRelation = new MemberRelation(
							member.getContactName(), missedPrivateSphere );
					if (!missedRelations.contains(missedRelation)) {
						final String sphereId = generateFreeSphereId(); 
						PrivateSphereReference personalSphere = new PrivateSphereReference(
								sphereId, missedRelation, true );
						missedRelations.add(personalSphere.getForwardRelation());
						missedRelations.add(personalSphere.getBackwarkRelation());
						missedSpheres.add(personalSphere);
					}
				}
			}
		}
		return missedSpheres;
	}
	
	public List<PrivateSphereReference> createMissedPrivateSpheres(List<SupraSphereMember> members) { 
		final List<PrivateSphereReference> missedPrivateSpheres = findMissedPrivateSpheres(members);
		for( PrivateSphereReference sphere : missedPrivateSpheres ) {
			sphere.createSphere(this);
		}
		return missedPrivateSpheres;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public List<PrivateSphereReference> createMissedPrivateSpheres(String sphereId ) {
		return createMissedPrivateSpheres( getMembersForSphere(sphereId) );
	}

	/**
	 * @param firstContactName
	 */
	public String getLoginByContactName(String contactName) {
		final SupraSphereMember memberByContactName = getSupraMembers().findMemberByContactName(contactName);
		if ( memberByContactName == null ) {
			logger.warn( "No member was found by contactName: " + contactName );
			return null;
		}
		else {
			return memberByContactName.getLoginName();
		}		
	}

	
}
