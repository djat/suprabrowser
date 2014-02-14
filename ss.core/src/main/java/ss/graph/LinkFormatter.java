package ss.graph;

public class LinkFormatter {

	/**
	 * @param graphItems
	 */
	public void formatHierarchy(GraphItemCollection graphItems, IGraphItemDependencyProvider relationProvider ) {
		for( IGraphItem item : graphItems ) {
			IItemIdentity parentId = relationProvider.getParentId( item );
			if ( parentId != null ) {
				final IGraphItem parentItem = graphItems.get( parentId );
				if ( parentItem != null ) {
					parentItem.getOutLinks().link( item );
				}
			}
		}	
//		IGraphItem first = graphItems.get( new ItemIdentity( "#1" ) );
//		IGraphItem second = graphItems.get( new ItemIdentity( "#3" ) );
//		first.getOutLinks().link( second );
	}

}

