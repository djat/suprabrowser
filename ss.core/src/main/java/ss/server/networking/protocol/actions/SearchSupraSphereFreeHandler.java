/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.SearchSupraSphereFreeAction;
import ss.search.LuceneSearch;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

/**
 *
 */
public class SearchSupraSphereFreeHandler extends AbstractActionHandler<SearchSupraSphereFreeAction> {
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(SearchSupraSphereFreeHandler.class);
	
	/**
	 * @param peer
	 */
	public SearchSupraSphereFreeHandler( DialogsMainPeer peer) {
		super(SearchSupraSphereFreeAction.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(SearchSupraSphereFreeAction action) {
		String sFreeQuery = action.getStringArg(SC.FREE_QUERY_ID);
		if (sFreeQuery != null) {
			logger.warn("It's a free query");
			LuceneSearch.free(Integer.parseInt(sFreeQuery));
		} 
		else {
			logger.error( "FREE_QUERY_ID is null" );
		}		
	}
}