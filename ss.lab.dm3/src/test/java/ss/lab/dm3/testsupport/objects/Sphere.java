package ss.lab.dm3.testsupport.objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.lucene.document.Document;

import ss.lab.dm3.annotation.CascadeFetch;
import ss.lab.dm3.annotation.SearchableField;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.backend.search.SearchHelper;
import ss.lab.dm3.persist.search.ISearchable;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;
import ss.lab.dm3.testsupport.objects.data.SphereType;

@Entity()
public class Sphere extends DomainObject implements ISearchable{

	private boolean emailAliasEnabled;

	private DeliveryType defaultDeliveryType;

	@OneToMany(mappedBy="parentSphere")
	private final ChildrenDomainObjectList<Sphere> childrenSpheres = new ChildrenDomainObjectList<Sphere>();

	private SphereType sphereType;

	private String emailAliasAddresses;

	@OneToMany(mappedBy="sphere")
	private final ChildrenDomainObjectList<UserInSphere> users = new SphereUserInSphereCollection();
	
	@SearchableField
	private String systemName;

	private Sphere parentSphere;

	@SearchableField
	private String displayName;
	
	/**
	 * extension_qualifier
	 * extension_id
	 */
	private QualifiedReference<SphereExtension> extension = new QualifiedReference<SphereExtension>();

	public boolean isEmailAliasEnabled() {
		return this.emailAliasEnabled;
	}

	public void setEmailAliasEnabled(boolean emailAliasEnabled) {
		this.emailAliasEnabled = emailAliasEnabled;
	}

	@Enumerated(EnumType.STRING)
	public DeliveryType getDefaultDeliveryType() {
		return this.defaultDeliveryType;
	}

	public void setDefaultDeliveryType(DeliveryType defaultDeliveryType) {
		this.defaultDeliveryType = defaultDeliveryType;
	}

	@Transient
	public ChildrenDomainObjectList<Sphere> getChildrenSpheres() {
		return this.childrenSpheres;
	}

	@Enumerated(EnumType.STRING)
	public SphereType getSphereType() {
		return this.sphereType;
	}

	public void setSphereType(SphereType sphereType) {
		this.sphereType = sphereType;
	}

	public String getEmailAliasAddresses() {
		return this.emailAliasAddresses;
	}

	public void setEmailAliasAddresses(String emailAliasAddresses) {
		this.emailAliasAddresses = emailAliasAddresses;
	}

	@CascadeFetch
	@Transient
	public ChildrenDomainObjectList<UserInSphere> getUsers() {
		return this.users;
	}

	@Column(nullable = false, unique = true)
	public String getSystemName() {
		return this.systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Sphere getParentSphere() {
		return this.parentSphere;
	}

	public void setParentSphere(Sphere parentSphere) {
		this.parentSphere = parentSphere;
	}

	@Column(nullable = false)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return
	 */
	public static TypedQuery<Sphere> createEqByClass() {
		return QueryHelper.eq( Sphere.class );
	}
	
	@Transient
	public SphereExtension getExtensionObject() {
		return this.extension != null ? this.extension.get() : null;
	}

	public void setExtensionObject( SphereExtension extension ) {
		setExtension(QualifiedReference.wrap(extension));
	}

	@Embedded
	@AttributeOverrides( { 
		@AttributeOverride(name="targetQualifier", column=@Column(name="extension_qualifier") ),
		@AttributeOverride(name="targetId", column=@Column(name="extension_id") )
	}) 
	public QualifiedReference<SphereExtension> getExtension() {
		return this.extension;
	}

	public void setExtension(QualifiedReference<SphereExtension> extension) {
		this.extension = extension;
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.search.ISearchable#collectFields(org.apache.lucene.document.Document)
	 */
	public void collectFields(Document collector) {
		SearchHelper.collectByDefault(this, collector);
	}
	
}