package ss.common.domainmodel2;

import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.StringField;
import ss.framework.domainmodel2.StringFieldDescriptor;
import ss.framework.domainmodel2.XmlEntityField;
import ss.domainmodel.preferences.SphereOwnPreferences;

public class Sphere extends DomainObject {

	public static class SystemNameDescriptor extends StringFieldDescriptor {
		public SystemNameDescriptor() {
			super(Sphere.class, "system_name");
		}
	}

	private final StringField systemName = createField( SystemNameDescriptor.class );
	
	private final StringField title = createField( StringField.class, "title" );

	private final XmlEntityField<SphereOwnPreferences> preferences = createField(SphereOwnPreferences.class, "preferences_xml" );
	
	private SphereMemberCollection members = new SphereMemberCollection( this );	
	
	
	/**
	 * @param spaceOwner
	 */
	public Sphere(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}

	public final String getSystemName() {
		return this.systemName.get();
	}
	
	public final void setSystemName(String systemName) {
		this.systemName.set( systemName);
	}

	/**
	 * @return
	 */
	public SphereMemberCollection getMembers() {
		return this.members;
	}

	public final String getTitle() {
		return this.title.get();
	}

	public final void setTitle(String title) {
		this.title.set( title );
	}

	/**
	 * @param instance
	 * @param string
	 * @return
	 */
	public static Sphere createNew( AbstractDomainSpace space, String systemName) {
		Sphere sphere = new Sphere( space );
		sphere.systemName.setSilently(systemName);
		sphere.markNew();
		return sphere;
	}

	/**
	 * @param spaceOwner
	 * @param systemName
	 * @return
	 */
	public static Sphere getBySystemName(AbstractDomainSpace spaceOwner, String systemName) {
		return spaceOwner.getSingleObject( CriteriaFactory.createEqual( Sphere.class, SystemNameDescriptor.class, systemName ) );
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + ", systemName: " + this.systemName;
	}

	/**
	 * @return the preferences
	 */
	public SphereOwnPreferences getPreferences() {
		return this.preferences.get();
	}

	/**
	 * @param preferences the preferences to set
	 */
	public void setPreferences(SphereOwnPreferences preferences) {
		this.preferences.set(preferences);
	}
	
	
}
