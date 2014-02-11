package ss.lab.dm3.orm.objects;

import javax.persistence.Transient;

import ss.lab.dm3.persist.DomainObject;

public class Bid extends DomainObject {

	private Item item;
	
	private String state;
	
	private int size;
	
	private boolean enabled;
	
	private long bidId;
	
	private TypeEnum type;

	/**
	 * @return the item
	 */
	public Item getItem() {
		return this.item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * @return the state
	 */
	@Transient
	public String getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the bidId
	 */
	public long getBidId() {
		return this.bidId;
	}

	/**
	 * @param bidId the bidId to set
	 */
	public void setBidId(long bidId) {
		this.bidId = bidId;
	}

	/**
	 * @return the type
	 */
	public TypeEnum getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeEnum type) {
		this.type = type;
	}
	
}
