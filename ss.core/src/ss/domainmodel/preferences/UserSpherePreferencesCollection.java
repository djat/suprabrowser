/**
 * 
 */
package ss.domainmodel.preferences;

import ss.common.ArgumentNullPointerException;
import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;


/**
 * @author zobo
 *
 */
public class UserSpherePreferencesCollection extends XmlListEntityObject<UserSpherePreferences>{
	/**
	 * @param itemType
	 * @param itemName
	 */
	public UserSpherePreferencesCollection() {
		super(UserSpherePreferences.class, "spherePreferences");
	}

	public final UserSpherePreferences getSpherePreferencesBySphereId(final String sphereId){
		return findFirst( new IXmlEntityObjectFindCondition<UserSpherePreferences>() {
			public boolean macth(UserSpherePreferences entityObject) {
				return sphereId.equals( entityObject.getSphereId() );
			}			
		});	
	}
	
	public void put( final UserSpherePreferences spherePreferences ) {
		if ( spherePreferences == null ) {
			throw new ArgumentNullPointerException( "spherePreferences" );
		}
		final UserSpherePreferences existedSpherePreferencesBySphereId = getSpherePreferencesBySphereId( spherePreferences.getSphereId() );
		if ( existedSpherePreferencesBySphereId != null ) {
			remove(existedSpherePreferencesBySphereId);
		}
		super.internalAdd( spherePreferences );
	}
	
	public void remove( final UserSpherePreferences spherePreferences ) {
		super.internalRemove( spherePreferences );
	}
}
