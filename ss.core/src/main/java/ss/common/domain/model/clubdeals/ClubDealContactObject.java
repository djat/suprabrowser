/**
 * 
 */
package ss.common.domain.model.clubdeals;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;

/**
 * @author roman
 *
 */
public class ClubDealContactObject extends DomainObject {

	private String contactName;
	
	private final DomainReference<TypeRecord> typeRef = DomainReference.create(TypeRecord.class);

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return this.contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	/**
	 * @return the typeRef
	 */
	public DomainReference<TypeRecord> getTypeRef() {
		return this.typeRef;
	}

	/**
	 * @return the type
	 */
	public TypeRecord getType() {
		if(this.typeRef.get()==null) {
			setType(TypeRecord.createEmptyType());
		}
		return this.typeRef.get();
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeRecord type) {
		this.typeRef.set(type);
	}
}
