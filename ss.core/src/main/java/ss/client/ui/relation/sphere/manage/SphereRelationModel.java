package ss.client.ui.relation.sphere.manage;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.ManagedSphereComparator;
import ss.client.ui.spheremanagement.ManagedSphereVisitor;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;
import ss.domainmodel.ObjectRelation;
import ss.domainmodel.ObjectRelationCollection;
import ss.framework.arbitrary.change.ArbitraryChangeSet;
import ss.framework.arbitrary.change.ArbitraryDifferenceBuilder;
import ss.framework.arbitrary.change.BackwardConvert;
import ss.framework.arbitrary.change.IObjectConverter;
import ss.framework.arbitrary.change.IObjectHandler;

public class SphereRelationModel {

	@SuppressWarnings("unused")
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final ManagedSphere root;
	
	private final ManagedSphere selectedShpere;

	/**
	 * @param root
	 * @param selectedShpere
	 */
	public SphereRelationModel(ManagedSphere root, ManagedSphere selectedShpere) {
		super();
		if ( root == null ) {
			throw new NullPointerException( "root" );
		}
		if ( selectedShpere == null ) {
			throw new NullPointerException( "selectedShpere" );
		} 
		this.root = root;
		this.selectedShpere = selectedShpere;
	}

	public ManagedSphere getRoot() {
		return this.root;
	}

	public ManagedSphere getSelectedShpere() {
		return this.selectedShpere;
	}
	
	public Collection<ManagedSphere> getRelatedSpheres() {
		final ObjectRelationCollection relations = this.selectedShpere.getStatement().getRelations();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Relations " + relations );
		}
		final Set<ManagedSphere> relatedSpheres = new TreeSet<ManagedSphere>( new ManagedSphereComparator());
		this.root.traverse( new ManagedSphereVisitor() {
			@Override
			public void beginNode(ManagedSphere sphere) {
				if ( relations.contains( sphere.getId() ) ) {
					relatedSpheres.add( sphere );
				}				
			}
		});
		return relatedSpheres;
	}

	/**
	 * @param provider
	 * @param string
	 * @return
	 */
	public static SphereRelationModel create(ISphereDefinitionProvider provider, String sphereId ) {
		if ( sphereId == null ) {
			throw new NullPointerException( "sphereId" );
		}
		SphereHierarchyBuilder builder = new SphereHierarchyBuilder( provider );
		final ManagedSphere root = builder.getResult();
		final ManagedSphere selectedSphere = root.find(sphereId);
		return new SphereRelationModel( root, selectedSphere );
	}

	/**
	 * @param checkedElements
	 * @return change set - set of added/removed spheres ids.
	 */
	public ArbitraryChangeSet<String> setRelatedSpheres( Iterable<ManagedSphere> relatedSpheres ) {
		final ObjectRelationCollection relations = this.selectedShpere.getStatement().getRelations();
		final ManagedSphere selectedSphere = SphereRelationModel.this.selectedShpere;
		final BackwardConvert<ManagedSphere,String> toManagedSphere = new BackwardConvert<ManagedSphere, String>();
		// Build difference between start and end state of relations 
		ArbitraryChangeSet<String> difference = getRelationsDifference(relations, relatedSpheres, toManagedSphere);
		// Apply difference to edited sphere and backward references
		difference.getCreated().foreach( new IObjectHandler<ManagedSphere>() {
			public void handle(ManagedSphere item) {
				// Forward reference 
				relations.add( item.getStatement() );
				// Backward reference
				item.getStatement().getRelations().add( selectedSphere.getStatement() );
			}
		}, toManagedSphere ); 
		difference.getDeleted().foreach( new IObjectHandler<String>() {
			public void handle(String item) {
				// Forward reference
				relations.removeBySphereId( item );
				// Backward reference
				final ManagedSphere target = toManagedSphere.convertOrNull( item );
				if ( target != null ) {
					target.getStatement().getRelations().removeBySphereId( selectedSphere.getId() );
				}
				else {
					SphereRelationModel.this.log.warn( "Can't remove backward reference " + selectedSphere.getId() + " -> " + item );
				}			
			}
		} );
		return difference;
	}

	/**
	 * @param endState
	 * @param startState
	 * @param toManagedSphere
	 * @return
	 */
	private ArbitraryChangeSet<String> getRelationsDifference(final ObjectRelationCollection startState, Iterable<ManagedSphere> endState,
			final BackwardConvert<ManagedSphere, String> toManagedSphere) {
		ArbitraryDifferenceBuilder<String> diffBuilder = new ArbitraryDifferenceBuilder<String>();
		diffBuilder.getFrom().add( startState, new IObjectConverter<String, ObjectRelation>() {
			public String convert(ObjectRelation obj) {
				return obj.getSphereId();
			}
		} );
		diffBuilder.getTo().add( endState, new IObjectConverter<String, ManagedSphere>() {
			public String convert(ManagedSphere obj) {
				return obj.getId();
			}
		}, toManagedSphere );	
		ArbitraryChangeSet<String> difference = diffBuilder.getDifference();
		return difference;
	}
			
}
