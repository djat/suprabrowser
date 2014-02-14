package ss.domainmodel;

import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

public class SupraSphereMemberCollection extends XmlListEntityObject<SupraSphereMember> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereMemberCollection.class);
	
	public SupraSphereMemberCollection() {
		super( SupraSphereMember.class, SupraSphereMember.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public final void add( SupraSphereMember item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public final void remove(SupraSphereMember entity) {
		super.internalRemove(entity);
	}
	
	public final void remove(int index) {
		remove(get(index));
	}	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public final SupraSphereMember get(int index) {
		return super.internalGet(index);
	}

	/**
	 * @param loginName
	 * @return
	 */
	public String findCoreSphereFor(String loginName) {
		SupraSphereMember supraSphereMember = findMemberByLogin( loginName );
		return supraSphereMember != null ? supraSphereMember.getSphereCoreSystemName() : null;
	}

	/**
	 * @param loginName
	 * @return
	 */
	public SupraSphereMember findMemberByLogin(final String loginName) {
		if ( loginName == null ) {
			return null;
		}
		return super.findFirst( new IXmlEntityObjectFindCondition<SupraSphereMember>() {

			public boolean macth(SupraSphereMember entityObject) {
				return loginName.equals( entityObject.getLoginName() );
			}
			
		});
	}

	/**
	 * @param loginName
	 * @return
	 */
	public SupraSphereMember findMemberByContactName(final String contactName) {
		if ( contactName == null ) {
			logger.warn( "contactName is null" );
			return null;
		}
		return super.findFirst( new IXmlEntityObjectFindCondition<SupraSphereMember>() {

			public boolean macth(SupraSphereMember entityObject) {
				return contactName.equals( entityObject.getContactName() );
			}
			
		});
	}

	
	/**
	 * @param contactName
	 */
	public boolean isSphereEnabledForContact(String contactName, String sphereSystemId ) {
		SupraSphereMember member = findMemberByContactName(contactName);
		return member != null && member.getSpheres().isEnabled( sphereSystemId );
	}

	public boolean isSphereEnabledForLogin(String login, String sphereSystemId ) {
		SupraSphereMember member = findMemberByLogin(login);
		return member != null && member.getSpheres().isEnabled( sphereSystemId );
	}
}
