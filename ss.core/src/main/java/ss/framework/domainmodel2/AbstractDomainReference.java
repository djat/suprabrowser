/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
public class AbstractDomainReference {

	private final DomainObject domainObjectOwner;

	/**
	 * @param domainObjectOwner
	 */
	public AbstractDomainReference(final DomainObject domainObjectOwner) {
		super();
		this.domainObjectOwner = domainObjectOwner;
	}

	/**
	 * @return
	 */
	public AbstractDomainSpace getSpaceOwner() {
		return this.domainObjectOwner.getSpaceOwner();
	}
	

	/**
	 * 
	 */
	protected final void markDirty() {
		this.domainObjectOwner.markDirty();		
	}

	/**
	 * @return the domainObjectOwner
	 */
	public final DomainObject getDomainObjectOwner() {
		return this.domainObjectOwner;
	}
	
	
}
