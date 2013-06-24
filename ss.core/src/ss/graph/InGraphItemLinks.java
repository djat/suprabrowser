package ss.graph;

final class InGraphItemLinks extends AbstractGraphItemLinks {

	
	/**
	 * @param itemOwner
	 */
	InGraphItemLinks(IGraphItem itemOwner) {
		super(itemOwner);
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.AbstractGraphItemLinks#linkOther(ss.messagemanagement.IGraphItem)
	 */
	@Override
	protected void crossLink(IGraphItem other) {
		other.getOutLinks().link( getItemOwner() );
	}

}
