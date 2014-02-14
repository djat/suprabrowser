/**
 * 
 */
package ss.framework.domainmodel2;

/**
 *
 */
final class DomainObjectCounter {

	/**
	 * Singleton instance
	 */
	public final static DomainObjectCounter INSTANCE = new DomainObjectCounter();

	private DomainObjectCounter() {
	}
	
	private long newObjectsCount = 0;

	/**
	 * @return
	 */
	public long nextId() {
		return -(++this.newObjectsCount );
	} 
}
