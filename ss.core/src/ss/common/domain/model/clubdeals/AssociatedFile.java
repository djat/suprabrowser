/**
 * 
 */
package ss.common.domain.model.clubdeals;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class AssociatedFile extends DomainObject {

	private String name;
	
	private long id;

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
}
