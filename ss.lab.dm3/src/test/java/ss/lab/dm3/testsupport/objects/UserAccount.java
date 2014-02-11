package ss.lab.dm3.testsupport.objects;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.lucene.document.Document;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.annotations.Index;

import ss.lab.dm3.annotation.SearchableField;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;
import ss.lab.dm3.persist.backend.search.SearchHelper;
import ss.lab.dm3.persist.backend.search.SecureLockCollector;
import ss.lab.dm3.persist.search.ISearchableSecure;
@Entity
public class UserAccount extends DomainObject implements ISearchableSecure {

	@SearchableField
	private String contactName;

	@OneToMany(mappedBy = "userAccount")
	private final ChildrenDomainObjectList<UserInSphere> spheres = new ChildrenDomainObjectList<UserInSphere>();

	private String contactCardId;

	@SearchableField
	private String login;

	private Sphere homeSphere;

	@Column(nullable = false)
	public String getContactName() {
		 return this.contactName;
	}

	public void setContactName( String contactName ) {
		 this.contactName = contactName;
	}

	@Transient
	public ChildrenDomainObjectList<UserInSphere> getSpheres() {
		 return this.spheres;
	}

	@Column(nullable = false)
	public String getContactCardId() {
		 return this.contactCardId;
	}

	public void setContactCardId( String contactCardId ) {
		 this.contactCardId = contactCardId;
	}

	@Column(nullable = false, unique = true)
	@Index(name = "idx_login")
	public String getLogin() {
		 return this.login;
	}

	public void setLogin( String login ) {
		 this.login = login;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	public Sphere getHomeSphere() {
		 return this.homeSphere;
	}

	public void setHomeSphere( Sphere homeSphere ) {
		 this.homeSphere = homeSphere;
	}

	/**
	 * @return
	 */
	public static TypedQuery<UserAccount> createEqByClass() {
		return QueryHelper.eq( UserAccount.class );
	}

	public void collectFields(Document collector) {
		SearchHelper.collectByDefault(this, collector);
	}

	public void collectSecureLocks(SecureLockCollector collector) {
		Session currentSession = HibernateUtils.getCurrentSession();
		SQLQuery query = currentSession.createSQLQuery( 
				"SELECT s.id FROM user_in_sphere AS uis JOIN sphere AS s ON s.id=uis.sphere_id where uis.user_account_id = :id" );
		query.setLong( "id", getId() );
		for( Object obj : query.list() ) {
			collector.add( Sphere.class, ((Number) obj).longValue() );
		}
	}

	@Transient	
	public boolean isPublicForSearch() {
		return false;
	}

}