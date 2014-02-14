/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import ss.common.ListUtils;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class SphereHierarchyBuilder implements IManagedSphereOwner {

	protected final ISphereDefinitionProvider sphereDefinitionProvider;
	
	protected final Hashtable<String,ManagedSphere> systemNameToItem = new Hashtable<String,ManagedSphere>();
	
	protected ManagedSphere root;
	
	private final Set<ManagedSphere> mergedSpheres = new HashSet<ManagedSphere>();
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHierarchyBuilder.class);
	
	public SphereHierarchyBuilder(ISphereDefinitionProvider sphereDefinitionProvider) {
		super();
		this.sphereDefinitionProvider = sphereDefinitionProvider;
	}

	public final ManagedSphere getResult() {
		this.sphereDefinitionProvider.checkOutOfDate();
		addAllSpheres();
		buildFullHierarchy();
		mergeItems(getRoot());
		return getRoot();
	}
	
	/**
	 * 
	 */
	private final void addAllSpheres() {
		Collection<SphereStatement> allSpheres = this.sphereDefinitionProvider.getAllSpheres();
		if (logger.isDebugEnabled()) {
			logger.debug("Adding addAllSpheres" + ListUtils.valuesToString(allSpheres));
		}
		for( SphereStatement sphere : allSpheres ) {
			if ( !this.systemNameToItem.contains( sphere.getSystemName() ) ) {
				ManagedSphere managedSphere = new ManagedSphere( this, sphere );
				configureSphere(managedSphere);
				this.systemNameToItem.put( managedSphere.getId(), managedSphere );
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding addAllSpheres done. sysname to sphere size " + this.systemNameToItem.size() );
		}
	}
	
	public int compare(ManagedSphere x, ManagedSphere y) {
		if ( x != null && y != null ) {
			return x.toString().compareTo( y.toString() );
		}
		return -1;
	}

	/**
	 * @param managedSphere
	 */
	protected void configureSphere( ManagedSphere managedSphere) {
	}

	/**
	 * 
	 */
	protected final void buildFullHierarchy() {
		setUpRoot();
		for( ManagedSphere item : this.systemNameToItem.values() ) {
			String desiredParentId = item.getDesiredParentId();
			ManagedSphere desiredParent = desiredParentId != null ? this.systemNameToItem.get(desiredParentId) : null;
			if ( desiredParent == null &&
				 item != getRoot() ) {
				desiredParent = getRoot(); 
			}			
			if ( desiredParent != null ) {
				desiredParent.getChildren().add(item); 
			}
		}
	}

	/**
	 * 
	 */
	private void mergeItems( ManagedSphere parent, Iterable<ManagedSphere> desiredChildren ) {
		if (logger.isDebugEnabled()) {
			logger.debug("Mergin " + parent);
		}
		for( ManagedSphere child : desiredChildren ) {
			if ( this.mergedSpheres.contains( child ) ) {
				child.remove();
				continue;
			}
			else {
				this.mergedSpheres.add( child );
				if ( this.sphereDefinitionProvider.isSphereVisible( child.getStatement() ) ) {
					parent.getChildren().add(child);				
					mergeItems( child );
				}
				else {
					child.remove();
					mergeItems( parent, child.getChildren().duplicate() );				
				}
			}
		}
	}

	/**
	 * @param item
	 */
	private void mergeItems(ManagedSphere item) {
		List<ManagedSphere> children = item.getChildren().duplicate();
		item.getChildren().clear();
		this.mergedSpheres.add( item );
		mergeItems( item, children );
	}
	
	protected final ManagedSphere getRoot(){
		return this.root;
	}
	
	protected final void setUpRoot(){
		String rootSphereId = this.sphereDefinitionProvider.getRootId();
		if (logger.isDebugEnabled()){
			logger.debug("Root element sphereId: " + rootSphereId);
		}
		this.root = this.systemNameToItem.get( rootSphereId );
	}
	

}
