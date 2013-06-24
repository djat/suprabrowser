package ss.search;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import ss.common.StringUtils;
import ss.global.SSLogger;
import ss.global.LoggerConfiguration;
import ss.util.VariousUtils;

public class URLParser {

	private static final String HTTP_PREFIX = "http://";

	// private static final String HTTP_PATTERN = "http:\\/\\/\\S*";
	private static final String HTTP_PATTERN = "http:\\/\\/([\\w-]+\\.)+[\\w-]+(\\/[\\w-.\\/?%=].*)?";

	private static final String DOMAIN_PATTERN = "http:\\/\\/([\\w-]+\\.)+[\\w-]+(\\/[\\w-.\\/?%=]./)?";

	// private static final String WWW_PATTERN = "www\\.\\S*";
	private static final String WWW_PATTERN = "www\\.([\\w-]+\\.)+[\\w-]+(\\/[\\w- .\\/?%=].*)?";

	private static final String XML_PATTERN = "http:\\/\\/([\\w-]+\\.)+[\\w-]+(\\/[\\w-.\\/?%=].*?\")?";
		
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(URLParser.class);

	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.DEFAULT);
		Logger logger = SSLogger.getLogger(URLParser.class);
		logger.info("starting");
		// parser.getURLSInsideFilesInDirectory();
		String xml = "</body><address value=\"http://mybroadband.co.za/podcast/?feed=rss2\"/><current_sphere value=\"6418709682795642154\"/><threa";
		
		System.out.println("Starting..: "+getURLFromXML(xml));
		
	}
	
	public static String getURLFromXML(String xml) {
		
		PatternMatcher matcher = new Perl5Matcher();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcherInput input = new PatternMatcherInput(xml);
		Pattern pattern = null;

		try {
			pattern = compiler.compile(XML_PATTERN);
		} catch (MalformedPatternException e) {

		}

		while (matcher.contains(input, pattern)) {
			MatchResult result = matcher.getMatch();
			String sResult = result.toString();
			sResult = cutOffTail(sResult , "\"");
			
			return sResult;
		}

		return null;
		
		
	}

	public static String getLeadingDomain(String fullURL) {
		PatternMatcher matcher = new Perl5Matcher();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcherInput input = new PatternMatcherInput(fullURL);
		Pattern pattern = null;

		try {
			pattern = compiler.compile(DOMAIN_PATTERN);
		} catch (MalformedPatternException e) {

		}

		while (matcher.contains(input, pattern)) {
			MatchResult result = matcher.getMatch();
			String sResult = result.toString();

			return sResult;
		}

		return null;

	}

	private URLParser() {

	}

	public static Vector getURLSInsideString( final String body ) {
		Vector<String> urls = new Vector<String>();
		if (logger.isDebugEnabled()) {
			logger.debug("macthing string: " + body);
		}
		try {
			urls.addAll(getUrls(body, HTTP_PATTERN, null));

			Vector secondCheck = getUrls(body, WWW_PATTERN, HTTP_PREFIX);

			for (int i = 0; i < secondCheck.size(); i++) {
				String check = (String) secondCheck.get(i);
				if (!VariousUtils.vectorContains(check, urls)) {
					urls.add(check);
				}
			}
		} catch (Throwable e) {
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Parsed urls size: " + urls.size());
			int i = 0;
			for (String u : urls) {
				logger.debug("" + (++i) + ": " + u);
			}
		}
		return urls;
	}

	private static Vector<String> getUrls(String body, String patternString,
			String prefix) throws MalformedPatternException {
		Vector<String> urls = new Vector<String>();
		PatternMatcher matcher = new Perl5Matcher();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcherInput input = new PatternMatcherInput(body);
		Pattern pattern;

		//logger.warn("pattern string: " + patternString);
		try {
			pattern = compiler.compile(patternString);
		} catch (MalformedPatternException e) {
			logger.error("Incorrect pattern: " + patternString, e);
			throw e;
		}
		while (matcher.contains(input, pattern)) {
			MatchResult result = matcher.getMatch();
			String sResult = result.toString();
			if (logger.isDebugEnabled()) {
				logger.warn("NEXT url match: " + sResult);
			}
			if (StringUtils.isBlank(sResult)){
				if (logger.isDebugEnabled()) {
					logger.debug("Result is blank, continue");
				}
				continue;
			}
			sResult = (prefix != null) ? (prefix + sResult) : sResult;
			sResult = cutOffTail(sResult , " ");
			final String secondSResult = getCleanedURL(sResult);
			if (logger.isDebugEnabled()) {
				logger.debug("Raw result: " + sResult);
				logger.debug("Cli result: " + secondSResult);
			}
			if (!VariousUtils.vectorContains(sResult, urls)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Raw result added");
				}
				urls.add(sResult);
				logger.debug("Begin offset: " + result.beginOffset(0));
				logger.debug("End offset: " + result.endOffset(0));
			}
			if (!VariousUtils.vectorContains(secondSResult, urls)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cliened result added");
				}
				urls.add(secondSResult); 
			}
		}
		return urls;
	}

	/**
	 * @return
	 */
	private static String getCleanedURL( final String url ) {
		String result = url;
		result = cutOffTail(result , "<");
		result = cutOffTail(result , "\"");
		return result;
	}
	
	private static String cutOffTail( final String str, final String tailSeparator){
		final int index = str.indexOf(tailSeparator);
		if (index != -1){
			return str.substring(0, index);
		} else {
			return str;
		}
	}

	private static boolean isUrl(String url, String patternString)
			throws MalformedPatternException {
		PatternMatcher matcher = new Perl5Matcher();
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcherInput input = new PatternMatcherInput(url);
		Pattern pattern;

		logger.info("pattern string: " + patternString);
		try {
			pattern = compiler.compile(patternString);
		} catch (MalformedPatternException e) {
			logger.error("Incorrect pattern: " + patternString, e);
			throw e;
		}
		return matcher.matches(input, pattern);
	}

	public static boolean isUrl(String url) {
		boolean isUrl = false;
		try {
			isUrl |= isUrl(url, HTTP_PATTERN);
			isUrl |= isUrl(url, WWW_PATTERN);
		} catch (MalformedPatternException e) {
			return false;
		}
		return isUrl;
	}

	public static boolean isHttpUrl(String url) {
		boolean isUrl = false;
		try {
			isUrl |= isUrl(url, HTTP_PATTERN);
		} catch (MalformedPatternException e) {
			return false;
		}
		return isUrl;
	}

	public static boolean isWwwUrl(String url) {
		boolean isUrl = false;
		try {
			isUrl |= isUrl(url, WWW_PATTERN);
		} catch (MalformedPatternException e) {
			return false;
		}
		return isUrl;
	}

	public static String getFixedUrl(String url) {
		if (isUrl(url)) {
			if (isHttpUrl(url)) {
				return url;
			}
			if (isWwwUrl(url)) {
				return HTTP_PREFIX + url;
			}
		}
		return url;
	}

}
