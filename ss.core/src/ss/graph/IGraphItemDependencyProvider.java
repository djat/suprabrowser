package ss.graph;


public interface IGraphItemDependencyProvider {

	/**
	 * @param item
	 * @return
	 */
	IItemIdentity getParentId(IGraphItem item);

}
