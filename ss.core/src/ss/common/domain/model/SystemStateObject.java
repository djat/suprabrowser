/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.collections.CheckSumCollection;
import ss.common.domain.model.collections.SystemPropertyCollection;

/**
 * @author roman
 *
 */
public class SystemStateObject extends DomainObject {

	private final CheckSumCollection checksumCollection = new CheckSumCollection();
	
	private final SystemPropertyCollection systemProperties = new SystemPropertyCollection();

	/**
	 * @return the checksumCollection
	 */
	public CheckSumCollection getChecksumCollection() {
		return this.checksumCollection;
	}

	/**
	 * @return the systemProperties
	 */
	public SystemPropertyCollection getSystemProperties() {
		return this.systemProperties;
	}
}
