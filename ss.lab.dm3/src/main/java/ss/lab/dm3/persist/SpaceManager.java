package ss.lab.dm3.persist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.changeset.CrudSet;
import ss.lab.dm3.persist.space.Space;
import ss.lab.dm3.persist.space.SpaceRegistry;

/**
 * 
 */
final class SpaceManager {
	
	/**
	 * 
	 */
	private static final Set<DomainObject> EMPTY_SET = new HashSet<DomainObject>();

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(SpaceManager.class);
	
	private final HashMap<Space,SpaceRegistry> spaceToRegistry = new HashMap<Space, SpaceRegistry>(); 
	
	private Iterable<SpaceRegistry> getRegistries() {
		return this.spaceToRegistry.values();
	}
		
	public Set<DomainObject> applyChanges(CrudSet changeSet) {
		final Space space = changeSet.getSpace();
		if ( space != null ) {
			if ( this.spaceToRegistry.containsKey( space ) ) {
				SpaceRegistry registry = this.spaceToRegistry.get( space );
				registry.apply( changeSet );
				// Expand other spaces by change set
				for (SpaceRegistry otherRegistry : getRegistries()) {
					if ( otherRegistry != registry ) {
						otherRegistry.expand(changeSet, null);
					}
				}
				return EMPTY_SET;
			}
			else {
				log.error( "Change set has space " + space + " but, space was not found " + changeSet );
				return expand(changeSet);
			}
		}
		else {
			return expand(changeSet);
		}
	}

	/**
	 * @param changeSet
	 * @return set of undistributed objects
	 */
	private Set<DomainObject> expand(CrudSet changeSet) {
		Set<DomainObject> distributed = new HashSet<DomainObject>();
		distributed.addAll( changeSet.getDeleted().getObjects() );
		for (SpaceRegistry registry : getRegistries()) {
			registry.expand(changeSet, distributed);
		}
		Set<DomainObject> undistributed = new HashSet<DomainObject>( changeSet.getAlive().getObjects() );
		undistributed.removeAll( distributed );
		return undistributed;
	}
	
	public void clear() {
		this.spaceToRegistry.clear();
	}

	public void debugTo(ToStringBuilder tsb) {
		tsb.append( "Spaces and weak objects state ", this );
		for( SpaceRegistry repository : getRegistries() ) {
			repository.debugTo(tsb);
		}
	}	
		
	public boolean add( Space space ) {
		if ( !this.spaceToRegistry.containsKey( space ) ) {   
			SpaceRegistry repository = new SpaceRegistry( space );
			this.spaceToRegistry.put( repository.getSpace(), repository );
			return true;
		}
		else {
			return false;
		}
	}
	
	public Set<DomainObject> remove( Space space ) {
		SpaceRegistry registry = this.spaceToRegistry.get( space );
		if ( registry != null ) {
			this.spaceToRegistry.remove( space );
			Set<DomainObject> releasedObjects = new HashSet<DomainObject>();
			for( DomainObject object : registry.getObjects() ) {
				if ( !this.contains( object.getQualifiedId() ) ) {
					releasedObjects.add( object );
				}
			}
			return releasedObjects;
		}
		else {
			return EMPTY_SET;
		}		
	}
	
	/**
	 * @param objectId
	 * @return
	 */
	private boolean contains(QualifiedObjectId<? extends DomainObject> objectId) {
		for (SpaceRegistry registry : getRegistries()) {
			if ( registry.contains(objectId) ) {
				return true;
			}
		}
		return false;
	}

	public SpaceRegistry get( Space space ) {
		return this.spaceToRegistry.get( space );
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "spaces count", this.spaceToRegistry.size() );
		return tsb.toString();
	}

	/**
	 * @param space
	 * @param item
	 */
	public void addObjectToSpace(Space space, DomainObject item) {
		if ( space == null ) {
			throw new NullPointerException( "space" );
		}
		SpaceRegistry registry = get( space );
		if ( registry != null ) {
			registry.add(item);
		}
	}

	/**
	 * @param space
	 * @param item
	 */
	public void removeObjectFromSpace(Space space, DomainObject item) {
		SpaceRegistry registry = get( space );
		if ( registry != null ) {
			registry.remove(item);
		}
	}

	/**
	 * @return
	 */
	public int size() {
		return this.spaceToRegistry.size();
	}	

	
}