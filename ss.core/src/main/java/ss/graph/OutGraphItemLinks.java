package ss.graph;

final class OutGraphItemLinks extends AbstractGraphItemLinks {
	
	/**
	 * @param itemOwner
	 */
	OutGraphItemLinks(IGraphItem itemOwner) {
		super(itemOwner);
	}

	/* (non-Javadoc)
	 * @see ss.messagemanagement.AbstractGraphItemLinks#linkOther(ss.messagemanagement.IGraphItem)
	 */
	@Override
	protected void crossLink(IGraphItem other) {
		other.getInLinks().link( getItemOwner() );
	}

}
