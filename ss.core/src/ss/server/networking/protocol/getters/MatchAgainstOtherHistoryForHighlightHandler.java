/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.MatchAgainstOtherHistoryForHighlightCommand;
import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;
import ss.common.ListUtils;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.processing.keywords.KeywordsOrganizer;

/**
 * @author zobo
 *
 */
public class MatchAgainstOtherHistoryForHighlightHandler extends AbstractGetterCommandHandler<MatchAgainstOtherHistoryForHighlightCommand, Hashtable<String,Object>> {

	private static final String ASSETS_WITH_KEYWORD_TAG = "assetsWithKeywordTag";

	private static final String HIGHLIGHT_KEYWORDS = "highlightKeywords";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MatchAgainstOtherHistoryForHighlightHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public MatchAgainstOtherHistoryForHighlightHandler(DialogsMainPeer peer) {
		super(MatchAgainstOtherHistoryForHighlightCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected Hashtable<String, Object> evaluate(MatchAgainstOtherHistoryForHighlightCommand command) throws CommandHandleException {
		if (logger.isDebugEnabled()) {
			logger.debug("MatchAgainstOtherHistoryForHighlightHandler evaluete started");
		}
		
		final Hashtable finalSession = command.getSessionArg();
		if ( finalSession == null ) {
			logger.error(" Hashtable finalSession is null ");
			return null;
		}
		
		final ResearchComponentDataContainer container = command.getData();
		if ( container == null ) {
			logger.error(" ResearchComponentDataContainer is null ");
			return null;
		}
		
		final String realName = (String) finalSession.get(SC.REAL_NAME);
		if ( realName == null ) {
			logger.error(" realName is null ");
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("realName is " + realName);
		}
		
		Hashtable<String, Object> matchForHighlightResults = new Hashtable<String,Object>();
		
		final KeywordsOrganizer organizer = new KeywordsOrganizer( container, realName, this.peer );
		
		if (organizer.getHighlightKeywords().isEmpty()) {
			logger.warn("No keys in spheres");
			return matchForHighlightResults;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Total result");
			logger.debug("keys: " + ListUtils.allValuesToString( organizer.getHighlightKeywords() ));
			logger.debug("docs count: " + organizer.getAssetsWithKeywordTag());
		}
		
		matchForHighlightResults.put(HIGHLIGHT_KEYWORDS, organizer.getHighlightKeywords());
		matchForHighlightResults.put(ASSETS_WITH_KEYWORD_TAG, organizer.getAssetsWithKeywordTag());
		matchForHighlightResults.put("datacontainer", organizer.getContainer());
		return matchForHighlightResults;
	}
}
