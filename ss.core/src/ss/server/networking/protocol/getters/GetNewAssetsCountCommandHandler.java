/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import ss.client.networking.protocol.getters.GetNewAssetsCountCommand;
import ss.common.SearchSettings;
import ss.framework.networking2.CommandHandleException;
import ss.global.SSLogger;
import ss.search.LuceneSearch;
import ss.search.SearchResults;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetNewAssetsCountCommandHandler extends
		AbstractGetterCommandHandler<GetNewAssetsCountCommand, Integer> {

	private static final Logger logger = SSLogger.getLogger(GetNewAssetsCountCommandHandler.class);
	
	public GetNewAssetsCountCommandHandler(final DialogsMainPeer peer) {
		super(GetNewAssetsCountCommand.class, peer);
	}
	
	@Override
	protected Integer evaluate(GetNewAssetsCountCommand command)
			throws CommandHandleException {
		String sphereId = command.getStringArg(SessionConstants.SPHERE_ID2);
		String contactName = command.getStringArg(SessionConstants.REAL_NAME);

		String sQuery = "-(voted:(\"["
				+ contactName
				+ "]\")) +(type:( ||terse ||message ||externalemail ||bookmark ||file ||contact ||rss)) +(sphere_id:( ||"
				+ sphereId + "))";
		Query query = getQuery(sQuery);
		int resultId = LuceneSearch.search(contactName, new SearchSettings(
				query, sQuery, false, false), this.peer.getVerifyAuth()
				.getEnabledSpheres(contactName), false);
		SearchResults results = LuceneSearch.getResults(resultId);
		return results.getResultsCount();
	}
	
	private Query getQuery(final String sQuery) { 
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(sQuery);
		} catch (ParseException ex) {
			logger.error("", ex);
		}
		return query;
	}

}
