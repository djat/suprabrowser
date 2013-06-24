/**
 * 
 */
package ss.smtp.responcetosphere;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.email.SendList;
import ss.smtp.sender.SendingElement;

/**
 * @author zobo
 *
 */
public class ResponceElement {
	private final List<SendList> tosendLists;
	
	private final List<SendList> notsendLists;

	private final String messageId;
	
	private final int totalCount;
	
	private int count;

	private final String sphereId;
	
	private boolean finalResponce = false;

	public ResponceElement(final List<SendList> tosendLists, final List<SendList> notsendLists, final String messageId, final String sphereId, final int totalCount) {
		super();
		this.tosendLists = tosendLists;
		this.notsendLists = notsendLists;
		this.messageId = messageId;
		this.sphereId = sphereId;
		this.totalCount = totalCount;
		this.count = 0;
	}

	/**
	 * 
	 * successfull element
	 */
	public ResponceElement(SendList sendList, String messageId, String sphereId, boolean succeded) {
		this.count = 0;
		this.totalCount = 0;
		this.tosendLists = new ArrayList<SendList>();
		this.tosendLists.add(sendList);
		this.notsendLists = new ArrayList<SendList>();
		if (!succeded){
			this.notsendLists.add(sendList);
		}
		this.messageId = messageId;
		this.sphereId = sphereId;
	}

	/**
	 * @param element
	 */
	public ResponceElement(SendingElement element, boolean succeded) {
		this(element.getSendList(), element.getMessageId(), element.getSphereId(), succeded);
	}

	public String getMessageId() {
		return this.messageId;
	}

	public List<SendList> getNotsendLists() {
		return this.notsendLists;
	}

	public List<SendList> getTosendLists() {
		return this.tosendLists;
	}
	
	public void add(ResponceElement element){
		if (element.getMessageId().equals(getMessageId())){
			getTosendLists().addAll(element.getTosendLists());
			getNotsendLists().addAll(element.getNotsendLists());
			this.count ++;
		}
	}
	
	public boolean isFull(){
		return (this.count >= this.totalCount);
	}

	/**
	 * @return
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	public boolean isFinalResponce() {
		return this.finalResponce;
	}

	public void setFinalResponce(boolean finalResponce) {
		this.finalResponce = finalResponce;
	}
}
