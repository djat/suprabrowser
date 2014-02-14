/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;

/**
 *
 */
public class SphereManager extends AbstractOutOfDateable {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereManager.class);
	
	protected final ISphereDefinitionProvider sphereDefinitionProvider;
	
	private final ArrayList<SphereActionListener> listeners = new ArrayList<SphereActionListener>(); 
	
	private ManagedSphere rootSphere = null;
	
	private ManagedSphere selectedSphere = null;


	public SphereManager(final ISphereDefinitionProvider sphereDefinitionProvider ) {
		super();
		this.sphereDefinitionProvider = sphereDefinitionProvider;
	}

	/**
	 * @return the rootSphere
	 */
	public ManagedSphere getRootSphere() {
		checkOutOfDate();
		return this.rootSphere;
	}

	
	/**
	 * 
	 */
	@Override
	protected final void reload() {
		SphereHierarchyBuilder builder = createSphereHierarchyBuilder();
		this.rootSphere = builder.getResult();
	}

	/**
	 * @return
	 */
	protected SphereHierarchyBuilder createSphereHierarchyBuilder() {
		return new SphereHierarchyBuilder( this.sphereDefinitionProvider );
	}
	
	/**
	 * @param listener
	 */
	public final void addSelectedSphereChangedListener(SphereActionListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * @return
	 */
	public final ManagedSphere getSelectedSphere() {
		return this.selectedSphere;
	}
	
	/**
	 * @return
	 */
	public final void setSelectedSphere( ManagedSphere sphereItem ) {
		logger.debug( "Selected sphere changed " + sphereItem );
		this.selectedSphere = sphereItem;
		for( SphereActionListener listener : this.listeners ) {
			listener.selectedSphereChanged( this.selectedSphere  );
		}
	}

	/**
	 * @param item
	 */
	public void openSphere(ManagedSphere item){
		
	}
	
	/**
	 * 
	 */
	@Override
	protected void outOfDateing() {
		this.sphereDefinitionProvider.outOfDate();
	}

	/**
	 * 
	 */
	public final void fireShowContextMenu() {
		for( SphereActionListener listener : this.listeners ) {
			listener.showContextMenu( this.selectedSphere  );
		}
	}

	/**
	 * @param mangerSelectionListener
	 */
	public void removeSelectedSphereChangedListener(SphereActionListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * 
	 */
	public void clearListenersList() {
		this.listeners.clear();
	}
	
	public ISphereDefinitionProvider getDefinitionProvider() {
		return this.sphereDefinitionProvider;
	}
}
