package ss.lab.dm3.orm.simpleobjects;

import ss.lab.dm3.persist.DomainObject;

public class Bid extends DomainObject {

	private Item item;

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	
}
