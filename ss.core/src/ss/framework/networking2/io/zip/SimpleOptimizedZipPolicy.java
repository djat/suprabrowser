/**
 * 
 */
package ss.framework.networking2.io.zip;

import ss.framework.networking2.io.Packet;

/**
 * @author dankosedin
 * 
 */
public class SimpleOptimizedZipPolicy implements IZipPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.networking2.io.zip.IZipPolicy#isMustBeZipped(ss.framework.networking2.io.Packet)
	 */
	public boolean isMustBeZipped(Packet packet) {
		if (packet.getDataLength() > 512) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.networking2.io.zip.IZipPolicy#isZippingEnabled()
	 */
	public boolean isZippingEnabled() {
		return true;
	}

}
