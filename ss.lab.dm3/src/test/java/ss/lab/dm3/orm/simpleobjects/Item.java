package ss.lab.dm3.orm.simpleobjects;

import javax.persistence.OneToMany;

import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;

public class Item extends DomainObject {

	@OneToMany(mappedBy="item")
	private ChildrenDomainObjectList<Bid> bids = new ChildrenDomainObjectList<Bid>();

	@OneToMany(mappedBy="parent")
	private ChildrenDomainObjectList<Item> children = new ChildrenDomainObjectList<Item>();
	
	private Item parent;
		
	/**
	 * 
	 */
	public Item() {
		super();
	}

	public ChildrenDomainObjectList<Bid> getBids() {
		return this.bids;
	}

	public void setBids(ChildrenDomainObjectList<Bid> bids) {
		this.bids = bids;
	}

	public Item getParent() {
		return this.parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	public ChildrenDomainObjectList<Item> getChildren() {
		return this.children;
	}

	public void setChildren(ChildrenDomainObjectList<Item> children) {
		this.children = children;
	}
	
}
