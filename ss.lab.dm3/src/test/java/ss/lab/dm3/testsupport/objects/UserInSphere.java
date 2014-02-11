package ss.lab.dm3.testsupport.objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.DomainObject;

@Entity
public class UserInSphere extends DomainObject {

	private Sphere sphere;

	private String sphereDisplayName;

	private UserAccount userAccount;

	@ManyToOne(fetch=FetchType.LAZY)
	public Sphere getSphere() {
		 return this.sphere;
	}

	public void setSphere( Sphere sphere ) {
		 this.sphere = sphere;
	}

	public String getSphereDisplayName() {
		 return this.sphereDisplayName;
	}

	public void setSphereDisplayName( String sphereDisplayName ) {
		 this.sphereDisplayName = sphereDisplayName;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	public UserAccount getUserAccount() {
		 return this.userAccount;
	}

	public void setUserAccount( UserAccount userAccount ) {
		 this.userAccount = userAccount;
	}

	/**
	 * @return
	 */
	public static TypedQuery<UserInSphere> createEqByClass() {
		return QueryHelper.eq(UserInSphere.class);
	}

}