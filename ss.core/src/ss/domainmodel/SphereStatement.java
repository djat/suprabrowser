package ss.domainmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.SphereDefinitionCreator;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.framework.domainmodel2.StringConvertor;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SphereStatement extends Statement {

	private final ISimpleEntityProperty data = super
	.createAttributeProperty( "inherit/data/@value" );

	private final ISimpleEntityProperty middleChat = super
	.createAttributeProperty( "ui/middle_chat/@value" );

	private final ISimpleEntityProperty treeOrder = super
	.createAttributeProperty( "ui/tree_order/@value" );

	private final ISimpleEntityProperty systemName = super
	.createAttributeProperty("@system_name");

	private final ISimpleEntityProperty displayName = super
	.createAttributeProperty("@display_name");

	private final ISimpleEntityProperty sphereCoreId = super
	.createAttributeProperty( "sphere_core/@sphere_id" );

	private final ISimpleEntityProperty sphereType = super
	.createAttributeProperty( "@sphere_type" );

	private final ISimpleEntityProperty defaultType = super
	.createAttributeProperty( "default_type/@value" );

	private final ISimpleEntityProperty defaultDelivery = super
	.createAttributeProperty( "default_delivery/@value" );

	private final ISimpleEntityProperty expiration = super
	.createAttributeProperty( "expiration/@value" );

	private final ISimpleEntityProperty specificMemberContactName = super
	.createAttributeProperty( "voting_model/specific/member/@contact_name" );

	private final ISimpleEntityProperty threadTypeTerseModify = super
	.createAttributeProperty( "thread_types/terse/@modify" );

	private final ISimpleEntityProperty threadTypeTerseEnabled = super
	.createAttributeProperty( "thread_types/terse/@enabled" );

	private final ISimpleEntityProperty threadTypeResultModify = super
	.createAttributeProperty( "thread_types/result/@modify" );

	private final ISimpleEntityProperty threadTypeResultEnabled = super
	.createAttributeProperty( "thread_types/result/@enabled" );

	private final ISimpleEntityProperty threadTypeRssModify = super
	.createAttributeProperty( "thread_types/rss/@modify" );

	private final ISimpleEntityProperty threadTypeRssEnabled = super
	.createAttributeProperty( "thread_types/rss/@enabled" );

	private final ISimpleEntityProperty threadTypeMessageModify = super
	.createAttributeProperty( "thread_types/message/@modify" );

	private final ISimpleEntityProperty threadTypeMessageEnabled = super
	.createAttributeProperty( "thread_types/message/@enabled" );

	private final ISimpleEntityProperty threadTypeExternalEmailModify = super
	.createAttributeProperty( "thread_types/externalemail/@modify" );

	private final ISimpleEntityProperty threadTypeExternalEmailEnabled = super
	.createAttributeProperty( "thread_types/externalemail/@enabled" );

	private final ISimpleEntityProperty threadTypeBookmarkModify = super
	.createAttributeProperty( "thread_types/bookmark/@modify" );

	private final ISimpleEntityProperty threadTypeBookmarkEnabled = super
	.createAttributeProperty( "thread_types/bookmark/@enabled" );

	private final ISimpleEntityProperty threadTypeSphereModify = super
	.createAttributeProperty( "thread_types/sphere/@modify" );

	private final ISimpleEntityProperty threadTypeSphereEnabled = super
	.createAttributeProperty( "thread_types/sphere/@enabled" );

	private final ISimpleEntityProperty threadTypeKeywordsModify = super
	.createAttributeProperty( "thread_types/keywords/@modify" );

	private final ISimpleEntityProperty threadTypeKeywordsEnabled = super
	.createAttributeProperty( "thread_types/keywords/@enabled" );

	private final ISimpleEntityProperty threadTypeContactModify = super
	.createAttributeProperty( "thread_types/contact/@modify" );

	private final ISimpleEntityProperty threadTypeContactEnabled = super
	.createAttributeProperty( "thread_types/contact/@enabled" );

	private final ISimpleEntityProperty threadTypeFileModify = super
	.createAttributeProperty( "thread_types/file/@modify" );

	private final ISimpleEntityProperty threadTypeFileEnabled = super
	.createAttributeProperty( "thread_types/file/@enabled" );

	private final ISimpleEntityProperty deleted = super
	.createAttributeProperty( "deleted/@value" );
	
	private final SphereMemberCollection sphereMembers = super
	.bindListProperty( new SphereMemberCollection() );

	private final ISimpleEntityProperty role = super
		.createAttributeProperty( "role/@value" );
	
	private final ObjectRelationCollection relations = super
			.bindListProperty(new ObjectRelationCollection(), "relations" );
	
	private final IComplexEntityProperty<SpherePhisicalLocationItem> phisicalLocation = super
			.createComplexProperty(SpherePhisicalLocationItem.ITEM_ROOT_ELEMENT_NAME, 
					SpherePhisicalLocationItem.class);
	
	public SphereStatement() {
		super("sphere");
	}

	public SphereStatement(String root) {
		super(root);
	}

	public static List<SphereStatement> wrap(Collection<?> objects ) {
		List<SphereStatement> spheres = new ArrayList<SphereStatement>( objects.size() );
		for( Object object : objects ) {
			if ( object instanceof Document ) {
				spheres.add( SphereStatement.wrap( (Document) object ) );
			}
			else if ( object instanceof SphereStatement ) {
				spheres.add( (SphereStatement) object );
			}
			else {
				throw new IllegalArgumentException( "Unexpected object type " + object );
			}
		}
		return spheres;
	}
	
	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SphereStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SphereStatement.class);
	}

	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SphereStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SphereStatement.class);
	}
	
	public SpherePhisicalLocationItem getPhisicalLocation(){
		return this.phisicalLocation.getValue();
	}

	/**
	 * Gets system name
	 */
	public final String getSystemName() {
		return this.systemName.getValue();
	}

	/**
	 * Sets system name
	 */
	public final void setSystemName(String value) {
		this.systemName.setValue(value);
	}

	/**
	 * Gets system name
	 */
	public final String getDisplayName() {
		return this.displayName.getValueOrEmpty();
	}

	/**
	 * Sets system name
	 */
	public final void setDisplayName(String value) {
		this.displayName.setValue(value);
	}

	/**
	 * Gets sphere core id
	 * @return
	 */
	public final String getSphereCoreId() {
		return this.sphereCoreId.getValue();
	}

	/**
	 * sets sphere core id
	 * @return
	 */
	public final void setSphereCoreId(String value) {
		this.sphereCoreId.setValue(value);
	}

	/**
	 * Gets sphere type
	 * @return
	 */
	public final SphereType getSphereType() {
		return this.sphereType.getEnumValue(SphereType.class);
	}

	/**
	 * Sets sphere type
	 */
	public final void setSphereType(SphereType value) {
		this.sphereType.setEnumValue(value);
	}

	/**
	 * Gets the sphere default type 
	 * @return
	 */
	public final String getDefaultType() {
		return this.defaultType.getValue();
	}

	/**
	 * Sets the sphere default type 
	 */
	public final void setDefaultType(String value) {
		this.defaultType.setValue(value);
	}

	/**
	 * Gets the sphere default delivery 
	 * @return
	 */
	public final String getDefaultDelivery() {
		return this.defaultDelivery.getValue();
	}

	/**
	 * Sets the sphere default delivery 
	 */
	public final void setDefaultDelivery(String value) {
		this.defaultDelivery.setValue(value);
	}

	/**
	 * Gets the sphere expiration
	 * @return
	 */
	public final String getExpiration() {
		return this.expiration.getValue();
	}

	/**
	 * Sets the sphere expiration
	 */
	public final void setExpiration(String value) {
		this.expiration.setValue(value);
	}

	public String getSpecificMemberContactName() {
		return this.specificMemberContactName.getValue();
	}

	public void setSpecificMemberContactName(String value) {
		this.specificMemberContactName.setValue(value);
	}

	public String getresultModify() {
		return this.threadTypeResultModify.getValue();
	}

	public void setResultModify(String value) {
		this.threadTypeResultModify.setValue(value);
	}

	public boolean getTerseEnabled() {
		return this.threadTypeTerseEnabled.getBooleanValue();
	}

	public void setTerseEnabled(boolean value) {
		this.threadTypeTerseEnabled.setBooleanValue(value);
	}

	public boolean getResultEnabled() {
		return this.threadTypeResultEnabled.getBooleanValue();
	}

	public void setResultEnabled(boolean value) {
		this.threadTypeResultEnabled.setBooleanValue(value);
	}

	public String getTerseModify() {
		return this.threadTypeTerseModify.getValue();
	}

	public void setTerseModify(String value) {
		this.threadTypeTerseModify.setValue(value);
	}

	public String getMessageModify() {
		return this.threadTypeMessageModify.getValue();
	}

	public void setMessageModify(String value) {
		this.threadTypeMessageModify.setValue(value);
	}

	public boolean getMessageEnabled() {
		return this.threadTypeMessageEnabled.getBooleanValue();
	}

	public void setMessageEnabled(boolean value) {
		this.threadTypeMessageEnabled.setBooleanValue(value);
	}


	public String getExternalEmailModify() {
		return this.threadTypeExternalEmailModify.getValue();
	}

	public void setExternalEmailModify(String value) {
		this.threadTypeExternalEmailModify.setValue(value);
	}

	public boolean getExternalEmailEnabled() {
		return this.threadTypeExternalEmailEnabled.getBooleanValue();
	}

	public void setExternalEmailEnabled(boolean value) {
		this.threadTypeExternalEmailEnabled.setBooleanValue(value);
	}


	public String getBookmarkModify() {
		return this.threadTypeBookmarkModify.getValue();
	}

	public void setBookmarkModify(String value) {
		this.threadTypeBookmarkModify.setValue(value);
	}

	public boolean getBookmarkEnabled() {
		return this.threadTypeBookmarkEnabled.getBooleanValue();
	}

	public void setBookmarkEnabled(boolean value) {
		this.threadTypeBookmarkEnabled.setBooleanValue(value);
	}


	public String getRssModify() {
		return this.threadTypeRssModify.getValue();
	}

	public void setRssModify(String value) {
		this.threadTypeRssModify.setValue(value);
	}

	public boolean getRssEnabled() {
		return this.threadTypeRssEnabled.getBooleanValue();
	}

	public final void setRssEnabled(boolean value) {
		this.threadTypeRssEnabled.setBooleanValue(value);
	}


	public final String getContactModify() {
		return this.threadTypeContactModify.getValue();
	}

	public final void setContactModify(String value) {
		this.threadTypeContactModify.setValue(value);
	}

	public final boolean getContactEnabled() {
		return this.threadTypeContactEnabled.getBooleanValue();
	}

	public final void setContactEnabled(boolean value) {
		this.threadTypeContactEnabled.setBooleanValue(value);
	}

	public final String getSphereModify() {
		return this.threadTypeSphereModify.getValue();
	}

	public final void setSphereModify(String value) {
		this.threadTypeSphereModify.setValue(value);
	}

	public final boolean getSphereEnabled() {
		return this.threadTypeSphereEnabled.getBooleanValue();
	}

	public final void setSphereEnabled(boolean value) {
		this.threadTypeSphereEnabled.setBooleanValue(value);
	}

	public final String getKeywordsModify() {
		return this.threadTypeKeywordsModify.getValue();
	}

	public final void setKeywordsModify(String value) {
		this.threadTypeKeywordsModify.setValue(value);
	}

	public final boolean getKeywordsEnabled() {
		return this.threadTypeKeywordsEnabled.getBooleanValue();
	}

	public final void setKeywordsEnabled(boolean value) {
		this.threadTypeKeywordsEnabled.setBooleanValue(value);
	}

	public final String getFileModify() {
		return this.threadTypeFileModify.getValue();
	}

	public final void setFileModify(String value) {
		this.threadTypeFileModify.setValue(value);
	}

	public final boolean getFileEnabled() {
		return this.threadTypeFileEnabled.getBooleanValue();
	}

	public final void setFileEnabled(boolean value) {
		this.threadTypeFileEnabled.setBooleanValue(value);
	}

	public final SphereMemberCollection getSphereMembers() {
		return this.sphereMembers;
	}	

	public SphereMember getMember(int index) {
		return this.getSphereMembers().get(index);
	}

	public void addMember(SphereMember member) {
		this.getSphereMembers().add(member);
	}

	public void removeMember(SphereMember member) {
		this.getSphereMembers().remove(member);
	}

	public String getMemberLoginName(int index) {
		return this.getMember(index).getLoginName();
	}

	public void setLoginName(int index, String value) {
		this.getMember(index).setLoginName(value);
	}

	public String getData() {
		return this.data.getValue();
	}

	public void setData( String value ) {
		this.data.setValue(value);
	}

	public boolean getMiddleChat() {
		return this.middleChat.getBooleanValue();
	}

	public void setMiddleChat( boolean value ) {
		this.middleChat.setBooleanValue(value);
	}

	public String getTreeOrder() {
		return this.treeOrder.getValue();
	}

	public void setTreeOrder( String value ) {
		this.treeOrder.setValue(value);
	}

	/**
	 * Create P2P sphere 
	 * @param systemName system name
	 * @return P2P sphere
	 */

	 public static SphereStatement createDefaultP2PSphere(String systemName) {
		 final Document sphereDocument = SphereDefinitionCreator.createDefinition( "P2P Sphere", systemName );
		 SphereStatement sphere = SphereStatement.wrap(sphereDocument);
		 // sphereDefinition.setSphereType( "group" ) was /sphere/@sphere_type="group"
		 sphere.setOriginalId( systemName );
		 sphere.setMessageId( systemName );
		 sphere.setConfirmed( true );
		 sphere.setGiver( "" );
		 return sphere;
	 }

	 /**
	  * @return
	  */
	 public boolean isRoot() {
		 String sysmentName = getSystemName();
		 String displayName = getDisplayName();
		 return sysmentName != null && displayName != null && sysmentName.equals( displayName );
	 }

	 /* (non-Javadoc)
	  * @see ss.domainmodel.Statement#hasValidType()
	  */
	 @Override
	 public boolean hasValidType() {
		 return super.hasValidType() && isSphere();
	 }

	 /**
	  * @param displayName
	  * @return
	  */
	 public static boolean isDisplayNameLikeEmailBox(String displayName) {
		 return displayName != null && displayName.contains("Email Box" );
	 }

	 public boolean isEmailBox() {
		 return isDisplayNameLikeEmailBox(getDisplayName());
	 }
	 
	 public boolean isErrorReportingSphere() {
		 return isDisplayNameLikeErrorreporting(getDisplayName());
	 }

	 /**
	 * @param displayName
	 * @return
	 */
	private static boolean isDisplayNameLikeErrorreporting(String displayName) {
		return displayName != null && displayName.contains("Error reporting" );
	}

	/**
	  * @param login
	  * @return
	  */
	 public boolean containsMember(String name) {
		 for(SphereMember member : getSphereMembers()) {
			 if(member.getContactName().equals(name)) {
				 return true;
			 }
		 }
		 return false;
	 }

	 public boolean hasValidDisplayName() {
		 return isDisplayNameValid(getDisplayName());
	 }

	 public static boolean isDisplayNameValid( String dipslayName ) {
		 return dipslayName != null &&
		 dipslayName.length() > 0 && 
		 StringConvertor.stringToLong( dipslayName, -1 ) == -1;
	 }

	public void setDeleted(boolean value) {
		this.deleted.setBooleanValue(value);
	}
	
	public boolean isDeleted() {
		return this.deleted.getBooleanValue(false);
	}

	/**
	 * @return
	 */
	public boolean isClubDeal() {
		return isClubdealType(getSphereType().name()) && !isEmailBox();
	}

	/**
	 * @param type
	 * @return
	 */
	public static boolean isClubdealType(String type) {
		return type.equals(SphereType.CLUBDEAL.name()) || type.equals(SphereType.GROUP.name()) || type.equals("sphere");
	}

	public final String getRole() {
		return this.role.getValueOrDefault(SphereRoleObject.getDefaultName());
	}
	
	public final void setRole(String role) {
		this.role.setValue( role );
	}
	
	/**
	 * @return Returns list of items
	 */
	public final ObjectRelationCollection getRelations() {
		return this.relations;
	}	
}

