package ss.lab.dm3.orm.objects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import ss.lab.dm3.persist.DomainObject;

public class Item extends DomainObject {

	@OneToMany(mappedBy="item")
	private List<Bid> bidsList = new ArrayList<Bid>();
	
	private Boolean visible;
	
	private Integer height;
	
	private Long itemId;

	/**
	 * @return the bidsList
	 */
	public List<Bid> getBidsList() {
		return this.bidsList;
	}

	/**
	 * @param bidsList the bidsList to set
	 */
	public void setBidsList(List<Bid> bidsList) {
		this.bidsList = bidsList;
	}

	/**
	 * @return the visible
	 */
	public Boolean getVisible() {
		return this.visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return this.height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * @return the itemId
	 */
	public Long getItemId() {
		return this.itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
}
