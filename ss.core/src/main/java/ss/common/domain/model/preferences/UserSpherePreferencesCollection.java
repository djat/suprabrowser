/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.collections.DomainObjectList;

/**
 * @author roman
 *
 */
public class UserSpherePreferencesCollection extends DomainObjectList<UserSpherePreferences> {

	public UserSpherePreferences getSpherePreferencesById(final String sphereId) {
		if(sphereId==null) {
			return null;
		}
		for(UserSpherePreferences preferences : this) {
			if(preferences.getSphereId().equals(sphereId)) {
				return preferences;
			}
		}
		return null;
	}
	
	public void put(final UserSpherePreferences preferences) {
		if(preferences==null) {
			return;
		}
		final UserSpherePreferences existedPreferences = getSpherePreferencesById(preferences.getSphereId());
		if(existedPreferences!=null) {
			remove(existedPreferences);
		}
		add(preferences);
	}
}
