/**
 * 
 */
package ss.framework.networking2.blob;

import ss.framework.identity.RuntimeIdGenerator;

/**
 *
 */
public class BlobIdGenerator {

	/**
	 * Singleton instance
	 */
	public final static BlobIdGenerator INSTANCE = new BlobIdGenerator();

	private RuntimeIdGenerator runtimeIdGenerator = new RuntimeIdGenerator();
	
	private BlobIdGenerator() {
	}

	/**
	 * @return
	 */
	public String nextId() {
		return String.valueOf( this.runtimeIdGenerator.nextId() );
	}
	
}
