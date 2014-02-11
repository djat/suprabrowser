package ss.lab.dm3.testsupport.objects;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.DomainObject;

@Entity
public class SupraSphere extends DomainObject {

	private Sphere sphere;

	private String domainNames;

	@OneToOne
	public Sphere getSphere() {
		return this.sphere;
	}

	public void setSphere( Sphere sphere ) {
		 this.sphere = sphere;
	}

	public String getDomainNames() {
		 return this.domainNames;
	}

	public void setDomainNames( String domainNames ) {
		 this.domainNames = domainNames;
	}

	/**
	 * @return
	 */
	public static TypedQuery<SupraSphere> createEqByClass() {
		return QueryHelper.eq( SupraSphere.class );
	}
	
}