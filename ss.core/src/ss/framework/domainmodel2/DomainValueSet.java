/**
 * 
 */
package ss.framework.domainmodel2;

import ss.common.ArgumentNullPointerException;


/**
 *
 */
public abstract class DomainValueSet {

	private final DomainObject domainObjectOwner;
	
	/**
	 * @param domainObjectOwner
	 */
	protected DomainValueSet(final DomainObject domainObjectOwner) {
		super();
		if ( domainObjectOwner == null ) {
			throw new ArgumentNullPointerException( "domainObjectOwner" );
		}
		this.domainObjectOwner = domainObjectOwner;
	}
	
	public synchronized void load( Record record ) {
	}
	
	public synchronized void save( Record record ) {
	}	

	protected final void markDirty() {
		this.domainObjectOwner.markDirty();
	}
	
}
