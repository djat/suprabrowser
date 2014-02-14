/**
 * 
 */
package ss.framework.domainmodel2.network;

import ss.framework.domainmodel2.UpdateData;
import ss.framework.networking2.Command;

/**
 *
 */
public final class UpdateCommand extends Command  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -188598070780078249L;

	private final UpdateData data;
	
	/**
	 * @param data
	 */
	public UpdateCommand(final UpdateData data) {
		super();
		this.data = data;
	}

	/**
	 * @return the updateData
	 */
	public UpdateData getData() {
		return this.data;
	}
	
}
