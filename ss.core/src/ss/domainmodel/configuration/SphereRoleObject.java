/**
 * 
 */
package ss.domainmodel.configuration;

import ss.common.StringUtils;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class SphereRoleObject extends XmlEntityObject {

	private static final String NO_ROLE = "no type";
	
	public static final String ROOT_ELEMENT_NAME = "sphere";
	
	private final ISimpleEntityProperty role = super
	.createAttributeProperty("@role");
	
	public SphereRoleObject() {
		super(ROOT_ELEMENT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	public static SphereRoleObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SphereRoleObject.class);
	}

	@SuppressWarnings("unchecked")
	public static SphereRoleObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SphereRoleObject.class);
	}
	
	public String getRoleName() {
		return this.role.getValueOrDefault(SphereRoleObject.NO_ROLE);
	}
	
	public void setRole(final String value) {
		if(StringUtils.isBlank(value) || value.equals(getDefaultName())) {
			return;
		}
		this.role.setValue(value);
	}
	
	public static String getDefaultName(){
		return SphereRoleObject.NO_ROLE;
	}

	/**
	 * @param roleName
	 * @return
	 */
	public static boolean isValid(final String roleName) {
		return StringUtils.isNotBlank(roleName) && !roleName.equals(getDefaultName());
	}
	
	public static boolean isValid(final SphereRoleObject role) {
		return isValid(role.getRoleName());
	}
}
