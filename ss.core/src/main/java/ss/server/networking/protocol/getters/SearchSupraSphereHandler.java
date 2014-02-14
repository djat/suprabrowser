package ss.server.networking.protocol.getters;

import java.util.List;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.SearchSupraSphereCommand;
import ss.common.SearchSettings;
import ss.common.VerifyAuth;
import ss.domainmodel.SupraSearchResultsObject;
import ss.framework.networking2.CommandHandleException;
import ss.search.ISearchResult;
import ss.search.LuceneSearch;
import ss.search.SearchResults;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.AdministrationUtils;
import ss.util.SessionConstants;

public class SearchSupraSphereHandler
		extends
		AbstractGetterCommandHandler<SearchSupraSphereCommand, AbstractDocument> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchSupraSphereHandler.class);

	/**
	 * @param peer
	 */
	public SearchSupraSphereHandler(DialogsMainPeer peer) {
		super(SearchSupraSphereCommand.class, peer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(SearchSupraSphereCommand command)
			throws CommandHandleException {

		logger.warn("GOT HANDLE SEARCH SUPRASPHERE");
		Helper helper = getResult(command);
		ISearchResult[] sResults = null;
		try {
			sResults = helper.results.getPage(helper.pageId);
		} catch (Exception e) {
			logger.error("Get page failed", e);
		}

//		logger.info("Keyword query here: " + keywordQuery + ": "
//				+ helper.queryId);
		
		SupraSearchResultsObject supraSearchresultObject = new SupraSearchResultsObject();
		supraSearchresultObject.setKeywordsQuery(((SearchSettings)command.getObjectArg(SessionConstants.SEARCH_SETTINGS)).getStringQuery());
		supraSearchresultObject.setTotalCount(helper.results.getResultsCount());
		supraSearchresultObject.setResultsCount(Math.min(sResults.length, helper.results.getResultsCount()));
		supraSearchresultObject.setPageCount(helper.results.getPagesCount());
		supraSearchresultObject.setPageId(helper.pageId);
		supraSearchresultObject.setId(helper.queryId);
		if (sResults != null) {
			for (int i = 0; i < sResults.length; i++) {
				ISearchResult result = sResults[i];
				if (result != null) {
					supraSearchresultObject.getResults().add(result.getSearchResultObject());
				}
			}
		}
		return (AbstractDocument) supraSearchresultObject.getBindedDocument();
	}

	private Helper getResult(SearchSupraSphereCommand command) {
		VerifyAuth verify = this.peer.getVerifyAuth();
		String real_name = (String)verify.getSession().get("real_name");
		SearchSettings settings = (SearchSettings)command.getObjectArg(SessionConstants.SEARCH_SETTINGS);
		List<String> enabledSpheres = settings.isAnonymous() ? verify.getAllSpheres().toStringVector() : verify.getEnabledSpheres(real_name);
	
		if (logger.isDebugEnabled()) {
			logger.debug("QUery: " + ((settings.getQuery() != null) ? settings.getQuery().toString() : ""));
		}

		if (settings.getQuery() != null) {

			int resultId = LuceneSearch.search(real_name, settings, enabledSpheres);
			SearchResults results = LuceneSearch.getResults(resultId);
			int pageId = 0;
			return new Helper(resultId, pageId, results);
		} else {
			String sQueryId = command.getStringArg(SC.QUERY_ID);
			String sPageId = command.getStringArg(SC.PAGE_ID);
			int resultId = Integer.parseInt(sQueryId);
			int pageId = Integer.parseInt(sPageId);
			SearchResults results = LuceneSearch.getResults(resultId);
			return new Helper(resultId, pageId, results);
		}
	}
	
	private static class Helper {
		int queryId;

		int pageId;

		SearchResults results;

		public Helper(int queryId, int pageId, SearchResults results) {
			logger.info("Constructing: " + queryId + " : " + pageId);
			this.queryId = queryId;
			this.pageId = pageId;
			this.results = results;

		}

	}

}
