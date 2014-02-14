/**
 * 
 */
package ss.common.domainmodel2;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.ReferenceField;
import ss.framework.domainmodel2.ReferenceFieldDescriptor;
import ss.framework.domainmodel2.XmlEntityField;
import ss.domainmodel.preferences.UserSpherePreferences;

/**
 *
 */

public class InvitedMember extends DomainObject {

	public static class MemberDescriptor extends ReferenceFieldDescriptor<Member> {
		public MemberDescriptor() {
			super(InvitedMember.class, "member_id", Member.class );
		}
	}
	
	public static class SphereDescriptor extends ReferenceFieldDescriptor<Sphere> {
		public SphereDescriptor() {
			super( InvitedMember.class, "sphere_id", Sphere.class );
		} 
	}
		  
	private final ReferenceField<Member> member = createField(MemberDescriptor.class);
	
	private final ReferenceField<Sphere> sphere = createField(SphereDescriptor.class);
	
	private final XmlEntityField<UserSpherePreferences> preferences = createFieldXmlEntityField( UserSpherePreferences.class, "preferences_xml" );  
	

	/**
	 * @param spaceOwner
	 */
	public InvitedMember(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}
	
	public static InvitedMember createNew( AbstractDomainSpace space, Member member, Sphere sphere) {
		if ( member == null ) {
			throw new ArgumentNullPointerException( "member" ); 
		}
		if ( sphere == null ) {
			throw new ArgumentNullPointerException( "sphere" );
		}
		InvitedMember memberInSphere = new InvitedMember( space );
		memberInSphere.member.setSilently( member );
		memberInSphere.sphere.setSilently( sphere );
		memberInSphere.markNew();
		return memberInSphere; 
	}
	/**
	 * @return the member
	 */
	public Member getMember() {
		return this.member.get();
	}
	/**
	 * @return the sphere
	 */
	public Sphere getSphere() {
		return this.sphere.get();
	}

	/**
	 * @return the preferences
	 */
	public UserSpherePreferences getPreferences() {
		return this.preferences.get();
	}

	/**
	 * set the preferences
	 */
	public void setPreferences( UserSpherePreferences preferences ) {
		this.preferences.set( preferences );
	}
	
	
	
	
}
