/**
 * 
 */
package ss.client.ui.messagedeliver;

import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public abstract class AbstractDeliveringElement {
	enum DeliveringElementType {
		SIMPLE, REPLACE, LIST
	}
	
	private final DeliveringElementType type;
	
	private final String sphereId;

	private MessagesPane specificPane;
	
	protected AbstractDeliveringElement(DeliveringElementType type, String sphereId){
		this.type = type;
		this.sphereId = sphereId;
	}
	
	DeliveringElementType getType(){
		return this.type;
	}

	public String getSphereId() {
		return this.sphereId;
	}
	
	void setSpecific(MessagesPane pane){
		this.specificPane = pane;
	}
	
	MessagesPane getSpecific(){
		return this.specificPane;
	}
}
