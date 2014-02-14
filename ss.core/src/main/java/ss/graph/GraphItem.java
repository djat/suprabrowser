package ss.graph;

public class GraphItem<T> implements IGraphItem {

	private AbstractGraphItemLinks toLinks = new OutGraphItemLinks( this );
	
	private AbstractGraphItemLinks fromLinks = new InGraphItemLinks( this );
	
	private final T data;
	
	private final IItemIdentity identity;

	/**
	 * @param converter
	 * @param data
	 * @param identity
	 */
	public GraphItem(final IGraphItemConverter<T> converter, final T data, final IItemIdentity identity) {
		super();
		this.data = data;
		this.identity = identity;
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.IDataItem#getIdentity()
	 */
	public IItemIdentity getIdentity() {
		return this.identity;
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.IDataItem#getData()
	 */
	public Object getData() {
		return this.data;
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.IGraphItem#getInLinks()
	 */
	public AbstractGraphItemLinks getInLinks() {
		return this.fromLinks;	
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.IGraphItem#getOutLinks()
	 */
	public AbstractGraphItemLinks getOutLinks() {
		return this.toLinks;
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.IGraphItem#isRoot()
	 */
	public boolean isRoot() {
		return this.fromLinks.size() <= 0;
	}		
	
	
}
