/**
 * 
 */
package ss.common.domain.model.trash;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class UiObject extends DomainObject {

	private String middleChat;
	
	private String treeOrder;

	/**
	 * @return the middleChat
	 */
	public String getMiddleChat() {
		return this.middleChat;
	}

	/**
	 * @param middleChat the middleChat to set
	 */
	public void setMiddleChat(String middleChat) {
		this.middleChat = middleChat;
	}

	/**
	 * @return the treeOrder
	 */
	public String getTreeOrder() {
		return this.treeOrder;
	}

	/**
	 * @param treeOrder the treeOrder to set
	 */
	public void setTreeOrder(String treeOrder) {
		this.treeOrder = treeOrder;
	}
	
	
	
}
