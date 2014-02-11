package ss.lab.dm3.persist;

import java.io.Serializable;

/**
 * @author dmitry
 * 
 */
public class Order implements Serializable {

	private static final long serialVersionUID = 4661788680400828749L;
	
	private final boolean ascending;
	private final String propertyName;

	
	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

	public boolean isAscending() {
		return this.ascending;
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}

	@Override
	public String toString() {
		return this.propertyName + ' ' + (this.ascending ? "asc" : "desc");
	}

}
