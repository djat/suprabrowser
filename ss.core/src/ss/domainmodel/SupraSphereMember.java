package ss.domainmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ss.domainmodel.SphereItem.SphereType;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SupraSphereMember extends SphereMember {
	
	private final ISimpleEntityProperty sphereCoreDisplayName = super
		.createAttributeProperty( "sphere_core/@display_name");
	
	private final ISimpleEntityProperty sphereCoreSystemName = super
		.createAttributeProperty( "sphere_core/@system_name");
	
	private final ISimpleEntityProperty sphereCoreSphereType = super
		.createAttributeProperty( "sphere_core/@sphere_type");
	
	private final ISimpleEntityProperty loginDisplayName = super
		.createAttributeProperty( "login_sphere/@display_name");

	private final ISimpleEntityProperty loginSystemName = super
		.createAttributeProperty( "login_sphere/@system_name");

	private final ISimpleEntityProperty loginSphereType = super
		.createAttributeProperty( "login_sphere/@sphere_type");
	
	private final ISimpleEntityProperty perspectiveName = super
		.createAttributeProperty( "perspective/@name");
	
	private final ISimpleEntityProperty perspective = super
		.createAttributeProperty( "perspective/@value");
	
	private final ISimpleEntityProperty threadTypesMessage = super
		.createAttributeProperty( "perspective/thread_types/message/@value" );
	
	private final ISimpleEntityProperty threadTypesBookmark = super
		.createAttributeProperty( "perspective/thread_types/bookmark/@value" );
	
	private final ISimpleEntityProperty threadTypesFile = super
		.createAttributeProperty( "perspective/thread_types/file/@value" );
	
	private final ISimpleEntityProperty keyword = super
		.createAttributeProperty( "perspective/keyword/@value" );
	
	private final ISimpleEntityProperty recent = super
		.createAttributeProperty( "perspective/recent/@value" );
	
	private final ISimpleEntityProperty active = super
		.createAttributeProperty( "perspective/active/@value" );
	
	private final ISimpleEntityProperty mark = super
		.createAttributeProperty( "perspective/mark/@value" );
	
	private final SphereItemCollection spheres = super
					.bindListProperty( new SphereItemCollection() );
	
	public SupraSphereMember() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static SupraSphereMember wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SupraSphereMember.class);
	}

	
	public String getSphereCoreDisplayName() {
		return this.sphereCoreDisplayName.getValue();
	}
	
	public void setSphereCoreDisplayName(String value) {
		this.sphereCoreDisplayName.setValue(value);
	}
	
	public String getSphereCoreSystemName() {
		return this.sphereCoreSystemName.getValue();
	}
	
	public void setSphereCoreSystemName(String value) {
		this.sphereCoreSystemName.setValue(value);
	}
	
	public String getSphereCoreSphereType() {
		return this.sphereCoreSphereType.getValue();
	}
	
	public void setSphereCoreSphereType(String value) {
		this.sphereCoreSphereType.setValue(value);
	}
	
	public String getLoginDisplayName() {
		return this.loginDisplayName.getValue();
	}
	
	public void setLoginDisplayName(String value) {
		this.loginDisplayName.setValue(value);
	}
	
	public String getLoginSphereSystemName() {
		return this.loginSystemName.getValue();
	}
	
	public void setLoginSystemName(String value) {
		this.loginSystemName.setValue(value);
	}
	
	public String getLoginSphereType() {
		return this.loginSphereType.getValue();
	}
	
	public void setLoginSphereType(String value) {
		this.loginSphereType.setValue(value);
	}
	
	/// TODO incapsulate
	public int getItemCount() {
		return this.spheres.getCount();
	}

	/// TODO incapsulate
	public SphereItem getItem(int index) {
		return this.spheres.get(index);
	}
	
	/// TODO incapsulate
	public void addItem(SphereItem item) {
		this.spheres.add(item);
	}
	
	/// TODO incapsulate
	public void removeItem(SphereItem item) {
		this.spheres.remove(item);
	}
	
	/// TODO incapsulate
	public void removeItem(int index) {
		this.spheres.remove(getItem(index));
	}
	
	public final SphereItemCollection getSpheres() {
		return this.spheres;
	}

	public String getPerspectiveName() {
		return this.perspectiveName.getValue();
	}
	
	public void setPerspectiveName(String value) {
		this.perspectiveName.setValue(value);
	}
	
	public String getPerspective() {
		return this.perspective.getValue();
	}
	
	public void setPerspective(String value) {
		this.perspective.setValue(value);
	}
	
	public String getThreadTypesMessage() {
		return this.threadTypesMessage.getValue();
	}
	
	public void setThreadTypesMessage(String value) {
		this.threadTypesMessage.setValue(value);
	}
	
	public String getThreadTypesBookmark() {
		return this.threadTypesBookmark.getValue();
	}
	
	public void setThreadTypesBookmark(String value) {
		this.threadTypesBookmark.setValue(value);
	}
	
	public String getThreadTypesFile() {
		return this.threadTypesFile.getValue();
	}
	
	public void setThreadTypesFile(String value) {
		this.threadTypesFile.setValue(value);
	}
	
	public String getKeyword() {
		return this.keyword.getValue();
	}
	
	public void setKeyword(String value) {
		this.keyword.setValue(value);
	}
	
	public boolean getRecent() {
		return this.recent.getBooleanValue();
	}
	
	public void setRecent(boolean value) {
		this.recent.setBooleanValue(value);
	}
	
	public String getMark() {
		return this.mark.getValue();
	}
	
	public void setMark(String value) {
		this.mark.setValue(value);
	}
	
	public boolean getActive() {
		return this.active.getBooleanValue();
	}
	
	public void setActive(boolean value) {
		this.active.setBooleanValue(value);
	}

	/**
	 * @param string
	 * @return
	 */
	public SphereItem getSphereByDisplayName(String string) {
		for(SphereItem sphere : getSpheres()) {
			if(sphere.getDisplayName().equals(string)) {
				return sphere;
			}
		}
		return null;
	}

	/**
	 * @param sphereId
	 */
	public SphereItem getSphereBySystemName(String sphereId) {
		for(SphereItem sphere : getSpheres()) {
			if(sphere.getSystemName().equals(sphereId)) {
				return sphere;
			}
		}
		return null;
	}

	/**
	 * @param members
	 * @return
	 */
	public static Set<String> getContactNames(List<SupraSphereMember> members) {
		final Set<String> contactNames = new TreeSet<String>();
		for (SupraSphereMember member : members ) {
			contactNames.add(member.getContactName());
		}
		return contactNames;
	}

	/**
	 * @return
	 */
	public List<String> getMembersSpheresDisplayNamesWoOwn() {
		List<String> result = new ArrayList<String>();
		for( SphereItem sphereItem : getSpheres() ) {
			if ( sphereItem.isEnabled() &&
				sphereItem.getSphereType() == SphereType.MEMBER &&
				sphereItem.getDisplayName() != getContactName() ) {
				result.add( sphereItem.getDisplayName() );
			}	
		}
		return result;
	}
	
}
