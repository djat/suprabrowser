/**
 * 
 */
package ss.framework.networking2.io.zip;

import ss.framework.networking2.io.Packet;

/**
 * @author dankosedin
 * 
 */
public class AllZipPolicy implements IZipPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.networking2.io.zip.IZipPolicy#isMustBeZipped(ss.framework.networking2.io.Packet)
	 */
	public boolean isMustBeZipped(Packet packet) {
		return true;
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
