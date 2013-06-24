/**
 * 
 */
package ss.framework.install.update.arrangement;

import java.io.File;

import ss.common.ArgumentNullPointerException;

abstract class AbstractFileArrangement {
	
	protected final File to;
	
	protected final File from;
	
	/**
	 * @param to
	 * @param from
	 */
	protected AbstractFileArrangement(final File to, final File from) {
		super();
		if (to == null) {
			throw new ArgumentNullPointerException("to");
		}
		if (from == null) {
			throw new ArgumentNullPointerException("from");
		}
		this.to = to;
		this.from = from;
	}
	
	/**
	 * @return the from
	 */
	public File getFrom() {
		return this.from;
	}

	/**
	 * @return the to
	 */
	public File getTo() {
		return this.to;
	}
	
	
}