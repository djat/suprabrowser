package ss.graph;

public interface IGraphItem {

	/**
	 * @return item identity
	 */
	IItemIdentity getIdentity();

	/**
	 * @return item data
	 */
	Object getData();
	
	/**
	 * @return the in links
	 */
	AbstractGraphItemLinks getInLinks();
	
	/**
	 * @return the to links
	 */
	AbstractGraphItemLinks getOutLinks();

	/**
	 * Returns true if item is root
	 */
	boolean isRoot();
}
