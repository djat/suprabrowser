package ss.lab.dm3.persist.space;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.changeset.CrudSet;

public class SpaceRegistry {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Space space;
	
	private final HashMap<QualifiedObjectId<?>, DomainObject> idToObject = new HashMap<QualifiedObjectId<?>, DomainObject>();
	
	/**
	 * @param space
	 */
	public SpaceRegistry(Space space) {
		super();
		if ( space == null ) {
			throw new NullPointerException( "space" ); 
		}
		this.space = space;
	}

	public Space getSpace() {
		return this.space;
	}
	
	public void clear() {
		this.idToObject.clear();
	}
	
	public void debugTo(ToStringBuilder tsb) {
		ToStringBuilder thisTsb = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );
		thisTsb.append( "space",  this.space );
		thisTsb.append( "size",  this.idToObject.size() );
		thisTsb.append( "objects", this.idToObject );
		tsb.append( thisTsb.toString() );
	}

	public boolean contains(QualifiedObjectId<?> objectId) {
		return this.idToObject.containsKey(objectId);
	}

	/**
	 * @param externalObject
	 * @return
	 */
	public boolean expand(DomainObject object) {
		if ( this.space.shouldExpandBy( object ) ) {			
			if (this.log.isDebugEnabled()) {
				this.log.debug( this + " expanded by " + object );
			}
			add(object);
			return true;
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Object ignored " + object + " for " + this);
			}
			return false;
		}
	}

	/**
	 * @param object
	 */
	public void add(DomainObject object) {
		this.idToObject.put( object.getQualifiedId(), object );
	}

	/**
	 * @param object
	 */
	public boolean remove(DomainObject object) {
		final boolean removed = this.idToObject.remove(object.getQualifiedId()) != null;
		if (this.log.isDebugEnabled()) {
			if ( removed ) {
				this.log.debug("Object removed " + object + " from " + this );
			}
			else {
				this.log.debug("Object not found " + object + " in " + this );
			}
		}
		return removed;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "conversation",  this.space );
		tsb.append( "size",  this.idToObject.size() );
		return tsb.toString();
	}

	/**
	 * @return
	 */
	public Iterable<DomainObject> getObjects() {
		return this.idToObject.values();
	}

	/**
	 * @param changeSet
	 */
	public void apply(CrudSet changeSet) {
		// TODO [dg] Apply should used only for data loading 
		// not sure about use it for other purpose
		for (DomainObject created : changeSet.getCreated()) {
			add(created);
		}
		for (DomainObject retrieved : changeSet.getRetrieved()) {
			add(retrieved);
		}
		for (DomainObject updated : changeSet.getUpdated()) {
			add(updated);
		}
		for (DomainObject deleted : changeSet.getDeleted()) {
			remove(deleted);			
		}
		this.space.afterChangeSetApplied( this, changeSet );
	}
	
	public void expand(CrudSet changeSet, Set<DomainObject> expanded ) {
		for (DomainObject created : changeSet.getCreated()) {
			if ( expand( created ) && expanded != null ) {
				expanded.add(created);
			}
		}
		for (DomainObject retrieved : changeSet.getRetrieved()) {
			if (expand(retrieved) && expanded != null ) {
				expanded.add(retrieved);
			}
		}
		for (DomainObject updated : changeSet.getUpdated()) {
			// Remove then add
			remove(updated);
			if ( expand(updated) && expanded != null ) {
				expanded.add(updated);
			}
		}
		// See caller: Deleted is always expanded
		for (DomainObject deleted : changeSet.getDeleted()) {
			remove(deleted);			
		}
		this.space.afterChangeSetApplied( this, changeSet );
	}	
	
}
