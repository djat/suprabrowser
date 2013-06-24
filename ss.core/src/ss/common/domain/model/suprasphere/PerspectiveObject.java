/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.message.MessageTypeCollection;

/**
 * @author roman
 *
 */
public class PerspectiveObject extends DomainObject {

	private boolean recent;
	
	private boolean active;
	
	private String mark;
	
	private String keyword;
	
	private String name;
	
	private String value;
	
	private final MessageTypeCollection threadTypes = new MessageTypeCollection();

	/**
	 * @return the recent
	 */
	public boolean isRecent() {
		return this.recent;
	}

	/**
	 * @param recent the recent to set
	 */
	public void setRecent(boolean recent) {
		this.recent = recent;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the mark
	 */
	public String getMark() {
		return this.mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

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
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the threadTypes
	 */
	public MessageTypeCollection getThreadTypes() {
		return this.threadTypes;
	}
}
