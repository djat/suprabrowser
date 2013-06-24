/**
 * 
 */
package ss.framework.domainmodel2.network;

import ss.framework.domainmodel2.Criteria;
import ss.framework.networking2.Command;

/**
 *
 */
public final class SelectCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8966745068990403012L;
	
	private final SerializableCriteria serializableCriteria;

	/**
	 * @param criteria
	 */
	public SelectCommand(final Criteria criteria) {
		super();
		this.serializableCriteria = new SerializableCriteria( criteria );
	}

	/**
	 * @return the criteria
	 */
	public Criteria getCriteria() {
		return this.serializableCriteria.createCriteria();
	}
	
}
