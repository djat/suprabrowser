/**
 * 
 */
package ss.common.domain.model.clubdeals;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class TypeRecord extends DomainObject {

	private static final String EMPTY_TYPE_NAME = "none";
	
	private String name;

	public TypeRecord() {
		super();
	}
	
	public TypeRecord(final String name) {
		super();
		setName(name);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		if(this.name==null) {
			return EMPTY_TYPE_NAME;
		}
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		if(!obj.getClass().equals(TypeRecord.class)) { 
			return false;
		}
		return getName().equals(((TypeRecord)obj).getName());
	}

	/**
	 * @return
	 */
	public static TypeRecord createEmptyType() {
		return new TypeRecord(TypeRecord.EMPTY_TYPE_NAME);
	}
}
