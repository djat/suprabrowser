/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class AffectedDomainObjectList {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AffectedDomainObjectList.class);
	
	private final List<DomainObject> objects = new ArrayList<DomainObject>();
	
	private boolean containDirty = false;
	
	private boolean containRemoved = false;
	
	public void add( DomainObject object, ObjectMark mark ) {
		if ( object != null ) {
			if ( mark == ObjectMark.DIRTY ) {
				logger.debug( "Dirty object detected" );
				this.containDirty = true;
			}
			else if ( mark == ObjectMark.REMOVED ) {
				logger.debug( "Removed object detected" );
				this.containRemoved = true;
			}
			else {
				logger.debug( "Clean object detected" );
			}
			this.objects.add( object );
		}
		
	}
	
	public Iterable<DomainObject> iterator() {
		return Collections.unmodifiableList(this.objects); 
	}
	
	public boolean contains( DomainObject object ) {
		return this.objects.contains(object);
	}

	/**
	 * @return the containsDirty
	 */
	public boolean isContainDirty() {
		return this.containDirty;
	}

	/**
	 * @return the containsRemoved
	 */
	public boolean isContainRemoved() {
		return this.containRemoved;
	}

	/**
	 * @return
	 */
	public int size() {
		return this.objects.size();
	}
}
