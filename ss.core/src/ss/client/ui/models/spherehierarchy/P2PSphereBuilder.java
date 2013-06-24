package ss.client.ui.models.spherehierarchy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;

public class P2PSphereBuilder {

	@SuppressWarnings("unused")
	private Logger logger = SSLogger.getLogger( P2PSphereBuilder.class);
	
	private static final String ContactP2PSperePrefix = "to ";

	private Hashtable<String, SphereStatement> contactNameToSelfSphere = new Hashtable<String, SphereStatement>();
	
	private Hashtable<SphereStatement, String> sphereToOwnerContactName = new Hashtable<SphereStatement, String>();
	
	private ArrayList<SphereStatement> spheres = new ArrayList<SphereStatement>();  
	
	private final SphereStatement rootSphere;
	
	/**
	 * @param rootSphere
	 */
	public P2PSphereBuilder(SphereStatement rootSphere) {
		super();
		if ( rootSphere == null) {
			throw new NullPointerException( "rootSphere is null");
		}
		this.rootSphere = rootSphere;
	}

	/**
	 * Add sphere to P2P sphere collection 
	 * @param sphereReferenceElement
	 * @param contactNameOwner
	 */
	public void add(Element sphereReferenceElement, String contactNameOwner) {
		SphereStatement referenceSphere = SphereStatement.wrap(sphereReferenceElement);
		SphereStatement sphere = SphereStatement.createDefaultP2PSphere( referenceSphere.getSystemName() );		
		final String referencesSphereDisplayName = referenceSphere.getDisplayName();
		final boolean isSelfSphere = contactNameOwner.equals(referencesSphereDisplayName);
		String sphereDisplayName;
		if ( isSelfSphere ) {
			sphereDisplayName = contactNameOwner;
			this.contactNameToSelfSphere.put( contactNameOwner, sphere );
		}
		else {
			sphereDisplayName =  ContactP2PSperePrefix + referencesSphereDisplayName;
			this.sphereToOwnerContactName.put(sphere, contactNameOwner );
		}
		sphere.setDisplayName( sphereDisplayName );
		sphere.setSubject( sphereDisplayName );
		this.spheres.add( sphere );
	}
	
	/**
	 * Returns result of P2P Sphere builder work
	 * @return
	 */
	public Collection<Document> getResult() {
		final SortedSet<SphereStatement> sortedHierarchicalSpheres = constructHierarchy();
		ArrayList<Document> documents = new ArrayList<Document>( this.contactNameToSelfSphere.size() );
		for( SphereStatement sphere : sortedHierarchicalSpheres ) {			
			documents.add( sphere.getBindedDocument() );
		}
		return documents;
	}

	private SortedSet<SphereStatement> constructHierarchy() {
		final SortedSet<SphereStatement> sortedHierarchicalSpheres = createSortedSpereSet();
		for( SphereStatement sphere : this.spheres ) {
			SphereStatement parentSphere = findOwnerContactSelfSphere( sphere );
			if ( parentSphere != null ) 
			{
				sphere.setParent( parentSphere );
			}
			else 
			{
				sphere.setParent( this.rootSphere );
			}
			sortedHierarchicalSpheres.add( sphere );
		}
		return sortedHierarchicalSpheres;
	}

	private SortedSet<SphereStatement> createSortedSpereSet() {
		SortedSet<SphereStatement> sortedSpheres = new TreeSet<SphereStatement>(
				new Comparator<SphereStatement>() {

					/* (non-Javadoc)
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					public int compare(SphereStatement o1, SphereStatement o2) {
						return P2PSphereBuilder.this.compare( o1, o2 );
					}
				}
			);	
		
		return sortedSpheres;
	}

	protected int compare(SphereStatement x, SphereStatement y) {
		if ( isSpheresHasCommonParent(x, y) ) {
			// Siblines spheres with same display name with be collapsed 
			return x.getDisplayName().compareTo( y.getDisplayName() );			
		}
		else {
			if ( isChildShere( x ) )
			{
				return 1;
			}
			if ( isChildShere(y) ) {
				return -1;
			}
			// Sheres with different parent cannot be equal 
			return 1;
		}
	}

	/**
	 * Retruns true if sphere is child 
	 */
	private boolean isChildShere(SphereStatement sphere) {
		return findOwnerContactSelfSphere(sphere) != null;
	}

	private boolean isSpheresHasCommonParent(SphereStatement x, SphereStatement y) {
		return findOwnerContactSelfSphere(x) == findOwnerContactSelfSphere(y);
	}
	
	private SphereStatement findOwnerContactSelfSphere(SphereStatement sphere) {
		final String contactName = this.sphereToOwnerContactName.get(sphere);
		return contactName != null ? this.contactNameToSelfSphere.get( contactName ) : null;
	}

}
