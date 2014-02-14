/**
 * 
 */
package ss.framework.networking2.io.zip;

import ss.framework.networking2.io.Packet;

/**
 * @author dankosedin
 *
 */
public interface IZipPolicy {
	
	boolean isMustBeZipped(Packet packet);
	
	boolean isZippingEnabled();
	
	
}
