/**
 * 
 */
package ss.domainmodel.spherehierarchy;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import ss.common.CompareUtils;
import ss.common.ListUtils;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class SphereHierarchyBuildResult {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHierarchyBuildResult.class);
	
	private final List<SphereStatement> rootSpheres = new ArrayList<SphereStatement>();
	
	private final SphereCollection spheres = new SphereCollection();
	/**
	 * @param rootSphere
	 * @param spheres
	 */
	public SphereHierarchyBuildResult(final List<SphereStatement> spheres ) {
		super();
		if ( spheres != null ) {
			this.spheres.addAll(spheres);
		}
		for (SphereStatement sphere : this.spheres) {
			String responseId = sphere.getResponseId();
			if (responseId == null ) {
				logger.debug( "adding to root " + sphere.getDisplayName() );
				this.rootSpheres.add(sphere);
			}
		}
	}

	/**
	 * 
	 */
	public SphereHierarchyBuildResult() {
		this( null );
	}

	/**
	 * @return the rootSphere
	 */
	public List<SphereStatement> getRootSpheres() {
		return this.rootSpheres;
	}

	/**
	 * @return the spheres
	 */
	public List<SphereStatement> getSpheres() {
		return this.spheres.asList();
	}
	
	/**
	 * @return the spheres
	 */
	public List<Document> getSpheresDocuments() {
		List<Document> resultAsDocuments = new ArrayList<Document>( this.spheres.getCount() );
		for( SphereStatement sphere : this.spheres ) {
			resultAsDocuments.add( sphere.getBindedDocument() );
		}
		return resultAsDocuments;
	}

	/**
	 * @param statement
	 * @param selecedRootSphere
	 * @return
	 */
	public boolean isFirstDescendantOfSecond(SphereStatement descendant, SphereStatement ancestor ) {
		final List<SphereStatement> ancestors = new ArrayList<SphereStatement>();
		ancestors.add( ancestor );
		return isFirstDescendantOfSecond( descendant, ancestors );
	}

	/**
	 * @param statement
	 * @param selecedRootSphere
	 * @return
	 */
	public boolean isFirstDescendantOfSecond(SphereStatement descendant, List<SphereStatement> ancestors ) {
		final List<String> ancestorsSystemsNames = new ArrayList<String>();
		for( SphereStatement ancestor : ancestors ) {
			ancestorsSystemsNames.add( ancestor.getSystemName() );
		}
		
		SphereStatement item = descendant;
		while ( item != null && !this.rootSpheres.contains( item ) ) {
			final String itemSphereCoreId = item.getSphereCoreId();
			logger.debug( "checking " + item );
			if ( itemSphereCoreId == null ||
				 CompareUtils.equals( itemSphereCoreId, item.getSystemName() ) ) {
				// item is root so returns
				logger.debug( "item is root so returns " + item );
				return false;
			}
			
			if ( ListUtils.containsByEqual( ancestorsSystemsNames, itemSphereCoreId ) )  {
				return true;
			}
			item = this.spheres.get(itemSphereCoreId);
			if ( logger.isDebugEnabled() && item == null ) {
				logger.debug( "Sphere not found " + itemSphereCoreId );
			}
		}		
		return false;
	}

	/**
	 * @param statement
	 * @param sphereHiearachy
	 * @return
	 */
	public boolean isFirstDescendantOfSecond(SphereStatement descendant, SphereHierarchyBuildResult sphereHiearachy) {
		return isFirstDescendantOfSecond(descendant, sphereHiearachy.getRootSpheres() );
	}

	/**
	 * @return
	 */
	public SphereStatement getSingleRoot() {
		return this.rootSpheres.size() == 1 ? this.rootSpheres.get( 0 ) : null;
	}

	/**
	 * 
	 */
	public void checkRootIsSingle() {
		if ( getSingleRoot() == null ) {
			throw new IllegalStateException( "Has no signle root " + ListUtils.valuesToString( this.rootSpheres ) );
		}		
	}
	
}
