/**
 * 
 */
package ss.domainmodel.configuration;

import org.apache.log4j.Logger;

import ss.common.StringUtils;
import ss.framework.entities.xmlentities.XmlListEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SphereRoleList extends XmlListEntityObject<SphereRoleObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SphereRoleList.class);
	
	public SphereRoleList() {
		super(SphereRoleObject.class, SphereRoleObject.ROOT_ELEMENT_NAME );
	}
	
	public SphereRoleObject getRoleByName(final String roleName) {
		if(StringUtils.isBlank(roleName)) {
			return null;
		}
		for(SphereRoleObject roleObject : this) {
			if(roleObject.getRoleName().equals(roleName)) {
				return roleObject;
			}
		}
		return null;
	}
	
	public boolean addRole(final String roleName) {
		SphereRoleObject role = new SphereRoleObject();
		role.setRole(roleName);
		return addRole(role);
	}
	
	public boolean addRole(final SphereRoleObject role) {
		if(!SphereRoleObject.isValid(role)) {
			return false;
		}
		if(getRoleByName(role.getRoleName())!=null) {
			return false;
		}
		super.internalAdd(role);
		return true;
	}
	
	public void removeRole(final String roleName) {
		if(!SphereRoleObject.isValid(roleName)) {
			return;
		}
		SphereRoleObject role = getRoleByName(roleName);
		removeRole(role);
	}
	
	public void removeRole(final SphereRoleObject role) {
		if(role==null) {
			return;
		}
		super.internalRemove(role);
	}

	/**
	 * 
	 */
	public void clear() {
		super.internalClear();
	}

	/**
	 * @return
	 */
	public String[] toStringArray() {
		String[] objects = new String[getCount()];
		int i = 0;
		for(SphereRoleObject role : this) {
			objects[i] = role.getRoleName();
			i++;
		}
		return objects;
	}
	
	/**
	 * @return
	 */
	public SphereRoleObject[] toArray() {
		SphereRoleObject[] objects = new SphereRoleObject[getCount()];
		int i = 0;
		for(SphereRoleObject role : this) {
			objects[i] = role;
			i++;
		}
		return objects;
	}

	/**
	 * @param role
	 * @return
	 */
	public boolean contains(String roleName) {
		return getRoleByName(roleName)!=null;
	}

	/**
	 * @param role
	 * @return
	 */
	public Object indexOf(String roleName) {
		int i = -1;
		if(!SphereRoleObject.isValid(roleName)) {
			return i;
		}
		for(SphereRoleObject role : this) {
			i++;
			if(role.equals(role.getRoleName())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param index
	 * @return
	 */
	public SphereRoleObject getRole(int index) {
		int i = 0;
		if(index<0 || index>=getCount()) {
			throw new IndexOutOfBoundsException();
		}
		for(SphereRoleObject role : this) {
			if(i!=index) {
				i++;
				continue;
			}
			return role;
		}
		return null;
	}
	
	public String getRoleName(final int index) {
		return getRole(index).getRoleName();
	}

	/**
	 * @param roleList
	 */
	public void copyAll(final SphereRoleList roleList) {
		SphereRoleObject[] roles = roleList.toArray();
		
		clear();
		
		for(SphereRoleObject roleObject : roles) {
			addRole(roleObject);
		}
	}
}
