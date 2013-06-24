package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetPrivateDomainNamesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.VariousUtils;

public class GetPrivateDomainNamesHandler extends AbstractGetterCommandHandler<GetPrivateDomainNamesCommand, Vector<String>> {
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetPrivateDomainNamesHandler.class);
	
	/**
	 * @param peer
	 */
	public GetPrivateDomainNamesHandler( DialogsMainPeer peer) {
		super(GetPrivateDomainNamesCommand.class, peer);
	}
	
	
	private static final String HTTP_PATTERN = "(http:\\/\\/([\\w-]+\\.)+[\\w-]+)(\\/[\\w- .\\/?%=]*)?";

	private static final String WWW_PATTERN = "(www\\.([\\w-]+\\.)+[\\w-]+)(\\/[\\w- .\\/?%=]*)?";

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<String> evaluate(GetPrivateDomainNamesCommand command) throws CommandHandleException {
		String filter = command.getStringArg(SC.FILTER);
		String loginName = (String) (command.getSessionArg()).get(SC.USERNAME);
		String homeSphereId = this.peer.getVerifyAuth()
				.getPersonalSphereFromLogin(loginName);
		logger.info("sphere=" + homeSphereId);
		logger.info("filter=" + filter);
		Vector<Document> filteredBookmarks = this.peer.getXmldb()
				.getBookmarksForHomeSphere(homeSphereId, filter.toLowerCase());

		Vector<String> domains = new Vector<String>();
		for (Document doc : filteredBookmarks) {
			String url = doc.getRootElement().element("address")
					.attributeValue("value");
			logger.info("url=" + url);
			String domain = getDomain(url);
			if ((domain != null)
					&& !VariousUtils.vectorContains(domain, domains)) {
				domains.add(domain);
			}
		}
		return domains;
	}

	private String getDomain(String url) {
		PatternMatcher matcher = new Perl5Matcher();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcherInput input = new PatternMatcherInput(url);
		Pattern pattern = null;

		String patternString = HTTP_PATTERN;
		try {
			pattern = compiler.compile(patternString);
		} catch (MalformedPatternException e) {
			logger.error("Incorrect pattern: " + patternString, e);
		}
		while (matcher.contains(input, pattern)) {
			MatchResult result = matcher.getMatch();
			String sResult = result.group(1);
			logger.info("Match: " + sResult);
			return sResult;
		}
		patternString = WWW_PATTERN;
		try {
			pattern = compiler.compile(patternString);
		} catch (MalformedPatternException e) {
			logger.error("Incorrect pattern: " + patternString, e);
		}
		while (matcher.contains(input, pattern)) {
			MatchResult result = matcher.getMatch();
			String sResult = result.group(1);
			logger.info("Match: " + sResult);
			return sResult;
		}
		return null;
	}

}
