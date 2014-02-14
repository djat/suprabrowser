/**
 * 
 */
package ss.common.domain.model.message;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.collections.VotingModelMemberCollection;

/**
 * @author roman
 *
 */
public class VotingModelObject extends DomainObject {

	private final VotingModelMemberCollection memberCollection = new VotingModelMemberCollection();
	
	/**
	 * Maybe is not used
	 */
	private String tallyNumber;
	
	/**
	 * Maybe is not used
	 */
	private String tallyValue;

	/**
	 * Maybe is not used
	 */
	private String type;
	
	/**
	 * Maybe is not used
	 */
	private String desc;

	/**
	 * @return the tallyNumber
	 */
	public String getTallyNumber() {
		return this.tallyNumber;
	}

	/**
	 * @param tallyNumber the tallyNumber to set
	 */
	public void setTallyNumber(String tallyNumber) {
		this.tallyNumber = tallyNumber;
	}

	/**
	 * @return the tallyValue
	 */
	public String getTallyValue() {
		return this.tallyValue;
	}

	/**
	 * @param tallyValue the tallyValue to set
	 */
	public void setTallyValue(String tallyValue) {
		this.tallyValue = tallyValue;
	}

	/**
	 * @return the memberCollection
	 */
	public VotingModelMemberCollection getMemberCollection() {
		return this.memberCollection;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return this.desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
