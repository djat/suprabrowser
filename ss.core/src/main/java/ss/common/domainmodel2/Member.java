package ss.common.domainmodel2;

import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.ReferenceField;
import ss.framework.domainmodel2.StringField;
import ss.framework.domainmodel2.StringFieldDescriptor;
import ss.framework.domainmodel2.XmlEntityField;
import ss.domainmodel.preferences.UserPersonalPreferences;

public class Member extends DomainObject {

	public static class LoginDescriptor extends StringFieldDescriptor {
		public LoginDescriptor() {
			super(Member.class,"login");
		}
	}
	
	private final ReferenceField<Sphere> coreSphere = createFieldReferenceField( Sphere.class, "core_sphere_id" );
	
	private final StringField login = createField( LoginDescriptor.class );
	
	private final StringField firstName = createField( StringField.class, "first_name" );
	
	private final XmlEntityField<UserPersonalPreferences> preferences = createFieldXmlEntityField(UserPersonalPreferences.class, "preferences_xml" );
		
	private MemberSphereCollection spheres = new MemberSphereCollection( this );
	

	/**
	 * @param space
	 */
	public Member(AbstractDomainSpace space) {
		super(space);
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName );
	}

	public String getFirstName() {
		return this.firstName.get();
	}
	
	public void setLogin(String login) {
		this.login.set( login );
	}
	
	public String getLogin() {
		return this.login.get();
	}

	/**
	 * @param sphere
	 */
	public void setCoreSphere(Sphere sphere) {
		this.coreSphere.set( sphere );
	}
	
	/**
	 * @return core sphere
	 */
	public Sphere getCoreSphere() {
		return this.coreSphere.get();
	}

	/**
	 * 
	 */
	public MemberSphereCollection getSpheres() {
		return this.spheres;
	}


	/**
	 * @return
	 */
	public static Member createNew( AbstractDomainSpace space, String login) {
		Member member = new Member( space );
		member.login.setSilently(login);
		member.markNew();
		return member;
	}

	/**
	 * @param spaceOwner
	 * @param login2
	 * @return
	 */
	public static Member getByLogin(AbstractDomainSpace spaceOwner, String login) {
		return spaceOwner.getSingleObject( CriteriaFactory.createEqual( Member.class, LoginDescriptor.class,login ) );
	}

	/**
	 * @return the preferences
	 */
	public UserPersonalPreferences getPreferences() {
		return this.preferences.get();
	}

	/**
	 * @param preferences the preferences to set
	 */
	public void setPreferences(UserPersonalPreferences preferences) {
		this.preferences.set( preferences );
	}

	
	
}
