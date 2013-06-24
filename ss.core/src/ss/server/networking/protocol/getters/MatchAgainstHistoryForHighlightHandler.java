package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.MatchAgainstHistoryForHighlightCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class MatchAgainstHistoryForHighlightHandler extends AbstractGetterCommandHandler<MatchAgainstHistoryForHighlightCommand, Hashtable<String,Object>> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(MatchAgainstHistoryForHighlightHandler.class);
	

	// TODO
	private static final String ASSETS_WITH_KEYWORD_TAG = "assetsWithKeywordTag";

	private static final String HIGHLIGHT_KEYWORDS = "highlightKeywords";

	/**
	 * @param peer
	 */
	public MatchAgainstHistoryForHighlightHandler( DialogsMainPeer peer) {
		super(MatchAgainstHistoryForHighlightCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String,Object> evaluate(MatchAgainstHistoryForHighlightCommand command) throws CommandHandleException {
		final Hashtable finalSession = command.getSessionArg();
		
		String realName = (String) finalSession.get(SC.REAL_NAME);
		String sphereId = (String) finalSession.get(SC.SPHERE_ID);

		Vector highlightKeywords = this.peer.getXmldb().getKeywords(
				this.peer.getVerifyAuth().getSystemName((realName)));

		Hashtable<String,Vector> assetsWithKeywordTag = new Hashtable<String,Vector>();

		for (int i = 0; i < highlightKeywords.size(); i++) {

			Document doc = (Document) highlightKeywords.get(i);

			String keyword = doc.getRootElement().element("subject")
					.attributeValue("value");

			String uniqueId = doc.getRootElement().element("unique_id")
					.attributeValue("value");

			// Document uniqueKeywordDoc = xmldb.getKeywordsWithUnique(
			// sphereId, uniqueId);

			Vector taggedItems = this.peer.getXmldb()
					.findAssetsInSameConceptSet(sphereId, uniqueId,
							new Vector());
			assetsWithKeywordTag.put(keyword, taggedItems);
		}
		Hashtable<String,Object> matchForHighlightResults = new Hashtable<String,Object>();
		matchForHighlightResults.put(HIGHLIGHT_KEYWORDS, highlightKeywords);
		matchForHighlightResults.put(ASSETS_WITH_KEYWORD_TAG,	assetsWithKeywordTag);
		return matchForHighlightResults;
	}

}
