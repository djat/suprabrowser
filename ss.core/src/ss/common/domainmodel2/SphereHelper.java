package ss.common.domainmodel2;

import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.AbstractHelper;
import ss.framework.domainmodel2.EditingScope;

public class SphereHelper extends AbstractHelper {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHelper.class);
	
	/**
	 * @param spaceOwnerRef
	 */
	public SphereHelper(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}

	/**
	 * 
	 */
	public synchronized Sphere getSphereBySystemName( String systemName ) {
		return Sphere.getBySystemName( getSpaceOwner(), systemName );
	}

	/**
	 * @param sphere_system_name
	 * @return
	 */
	public synchronized Sphere createNew(String systemName) {
		return Sphere.createNew(getSpaceOwner(), systemName);
	}
	
	public synchronized Sphere getSphereOrCreate(String systemName) {
		Sphere sphere = getSphereBySystemName(systemName);
		if (sphere != null) {
			return sphere;
		}
		EditingScope scope = getSpaceOwner().createEditingScope();
		try {
			return createNew(systemName);
		} finally {
			scope.dispose();
		}
	}
	
	public synchronized void setSpherePreferences(String systemName, SphereOwnPreferences preferences) {
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			getSphereOrCreate( systemName ).setPreferences( preferences );
		}
		finally {
			editingScope.dispose();
		}
	}
	
	public synchronized SphereOwnPreferences getSpherePreferences(String systemName) {
		return getSphereOrCreate( systemName ).getPreferences();		
	}
	
	public synchronized ss.common.domain.model.preferences.SphereOwnPreferences getSpherePreferencesObject(String systemName) {
		return null;		
	}

}
