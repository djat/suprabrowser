/**
 * 
 */
package ss.common.domain.model.collections;

import ss.common.domain.model.clubdeals.TypeRecord;

/**
 * To add new item in this collection should use the put method
 */
public class TypeCollection extends DomainObjectList<TypeRecord> {

	@Override
	public boolean contains(final TypeRecord item) {
		for(TypeRecord type : this) {
			if(type.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	
	public final void put(final TypeRecord item) {
		if(item==null) {
			return;
		}
		if(contains(item)) {
			return;
		}
		add(item);
	}


	/**
	 * @param name
	 * @return
	 */
	public boolean removeType(TypeRecord type) {
		if(!contains(type)) {
			return false;
		}
		remove(type);
		return true;
	}


	/**
	 * @param newName
	 * @return
	 */
	public boolean containsName(String newName) {
		return contains(new TypeRecord(newName));
	}
}
