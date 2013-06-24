/*
 * Created on May 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.search;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.dom4j.Element;

import ss.global.SSLogger;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

public class SearchEngine {

	// First, get the and, or, not positions and see if they are followed by
	// any quotations

	private static final Logger logger = SSLogger.getLogger(SearchEngine.class);

	/**
	 * @deprecated
	 */
	public static void main(String[] args) {

		// PropertyConfigurator.configure("logger.conf");

		String keywords = args[0];
		SearchEngine se = new SearchEngine();
		se.prepareLikeStatement(keywords);

	}

	/**
	 * @deprecated
	 */
	public String prepareLikeStatement(String keywords) {
		String like = null;

		// "this that" or anda
		// this or "that anda"
		// this or that or anda
		// this and that or anda
		// this or that and anda
		// this not that or anda
		// this or that not anda
		// "this that" not anda
		// 

		// SELECT * FROM `1259639228679182417` where XMLDATA like ('%subject
		// value="%int%') AND XMLDATA not like ('%subject value="%hey now%')
		boolean stillHasResults = true;
		keywords = keywords.toLowerCase();
		logger.info("keywords before: " + keywords);
		String[] result = keywords.split("or\\b");
		for (int x = 0; x < result.length; x++)
			logger.info("TOKEN OR: " + result[x]);

		result = keywords.split("and\\b");
		for (int x = 0; x < result.length; x++)
			logger.info("TOKEN AND: " + result[x]);

		while (stillHasResults == true) {
			Perl5Util util = new Perl5Util();

			util.match("/\".*?\"/", keywords);

			MatchResult matchResult = null;

			try {

				matchResult = util.getMatch();

			} catch (Exception e) {

				logger.error(e.getMessage(), e);

			}
			if (matchResult != null) {

				for (int i = 0; i < matchResult.groups(); i++) {
					String one = (String) matchResult.group(i);

					logger.info("ONE MATCH: " + one);
					String newKeywords = keywords.replaceAll(one, "");
					String rep = strip(one);

					keywords = newKeywords;

				}
			} else {
				stillHasResults = false;

			}
		}

		return like;

	}

	public boolean searchForKeywords(org.dom4j.Document doc, String keywords,
			boolean isKeywordKeywordSearch) {
		return searchForKeywordsNewMultiPurpose(doc, keywords,
				isKeywordKeywordSearch, false);
	}

	public boolean searchForKeywordsInThreadId(org.dom4j.Document doc,
			String keywords, boolean isKeywordKeywordSearch) {
		return searchForKeywordsNewMultiPurpose(doc, keywords,
				isKeywordKeywordSearch, true);
	}

	public boolean searchForAuthor(org.dom4j.Document doc, String author) {

		boolean found = false;
		String type = doc.getRootElement().element("type").attributeValue(
				"value");

		String matcher = doc.getRootElement().element("giver").attributeValue(
				"value");

		if (matcher.lastIndexOf(author) != -1) {

			found = true;
		}

		if (!type.equals("terse")) {

		}

		return found;
	}

	public String strip(String inStr) {

		StringBuffer outBuf;

		outBuf = new StringBuffer(inStr.length());
		for (int i = 0; i < inStr.length(); ++i)
			switch (inStr.charAt(i)) {
			case '"':
				// case '\t':
				// case ' ' :

				// do nothing
				break;
			default:
				outBuf.append(inStr.charAt(i));

			}
		return outBuf.toString();
	}

	public boolean searchForKeywordsNewMultiPurpose(org.dom4j.Document doc,
			String keywords, boolean isKeywordKeywordSearch, boolean inThread) {

		keywords = keywords.toLowerCase();
		logger.info("keywords are :" + keywords);

		String type = doc.getRootElement().element("type").attributeValue(
				"value");

		SearchUtil myUtil = new SearchUtil(keywords);

		if ((type.equals("bookmark") || type.equals("file") || ((!inThread) ? (type
				.equals("rss") || type.equals("contact"))
				: (!inThread)))
				&& isKeywordKeywordSearch == false) {
			if (myUtil.searchInIndex(doc)) {
				return true;
			}
		}

		String commonMatcher = getCommonMatcher(doc, isKeywordKeywordSearch,
				type, inThread);

		if (myUtil.find(commonMatcher, false)) {
			return true;
		}

		if (!type.equals("terse")) {

			String notTerseMatcher = getNotTerseMatcher(doc);
			// TODO is skipNot=true needed? or it is a bug?
			if (myUtil.find(notTerseMatcher, true)) {
				return true;
			}

			String notTerseCommentMatcher = getNotTerseCommentMatcher(doc);
			if (myUtil.find(notTerseCommentMatcher, false)) {
				return true;
			}
		}
		return false;
	}

	private String getNotTerseMatcher(org.dom4j.Document doc) {
		Element body = doc.getRootElement().element("body");
		return (body != null) ? body.getText().toLowerCase() : null;
		// TODO toLowerCase needed or not?
		// return (body != null) ? body.getText() : null;
	}

	private String getNotTerseCommentMatcher(org.dom4j.Document doc) {
		Element comment = doc.getRootElement().element("body").element(
				"comment");
		return (comment != null) ? comment.getText().toLowerCase() : null;
	}

	private String getCommonMatcher(org.dom4j.Document doc,
			boolean isKeywordKeywordSearch, String type, boolean skipSearch) {
		logger.info("Get common matcher");
		String matcher = doc.getRootElement().element("subject")
				.attributeValue("value").toLowerCase();
		if (!skipSearch) {
			Element search = doc.getRootElement().element("search");
			if (search != null) {
				matcher = matcher + " "
						+ search.element("interest").asXML().toLowerCase();
				logger.info("macthing search here....: " + search.asXML());
			}
		}

		if (type.equals("bookmark") && isKeywordKeywordSearch == false) {
			matcher = matcher
					+ " "
					+ doc.getRootElement().element("address").attributeValue(
							"value");
		}
		if (type.equals("file") && isKeywordKeywordSearch == false) {
			matcher = matcher
					+ " " // TODO is this " " needed?
					+ doc.getRootElement().element("data_id").attributeValue(
							"value");
		}
		if (type.equals("reply") && isKeywordKeywordSearch == false) {

			matcher = matcher
					+ " "
					+ doc.getRootElement().element("body").element("comment")
							.getText().toLowerCase();

		}

		if (type.equals("comment") && isKeywordKeywordSearch == false) {

			matcher = matcher
					+ " "
					+ doc.getRootElement().element("body").element("comment")
							.getText().toLowerCase();

		}
		if (!type.equals("terse") && isKeywordKeywordSearch == false) {
			matcher = matcher
					+ " "
					+ doc.getRootElement().element("body").getText()
							.toLowerCase();

		}
		return matcher;
	}

	class SearchUtil {
		private static final String NOT = "-";

		private static final String OR = " OR ";

		private static final String PLUS = "+";

		private String keywords;

		private Vector<String> keys;

		private Vector<String> results;

		private Vector<String> notKeys;

		private Vector<String> orKeys;

		String line;

		public SearchUtil(String keywords) {
			this.keywords = keywords;
			this.keys = new Vector<String>();
			this.results = new Vector<String>();
			this.notKeys = new Vector<String>();
			this.orKeys = new Vector<String>();
			this.line = "";
			setUpResults();
			setUpKeys();
			setUpKeyFromResults();
			// logAll();
		}

		@SuppressWarnings("unused")
		private void logAll() {
			logger.info(" ");
			logger.info("keywords are:" + this.keywords);
			logger.info(" ");
			for (String key : this.keys) {
				logger.info("key :" + key);
			}
			logger.info(" ");
			for (String key : this.results) {
				logger.info("results :" + key);
			}
			logger.info(" ");
			for (String key : this.notKeys) {
				logger.info("notKeys :" + key);
			}
			logger.info(" ");
			for (String key : this.orKeys) {
				logger.info("orKey :" + key);
			}
			logger.info(" ");
			logger.info("line is:" + this.line);
			logger.info(" ");
		}

		private void setUpKeyFromResults() {
			for (String key : this.results) {
				addToLine("\"" + key + "\"", PLUS);
				this.keys.add(key);
			}
		}

		private void setUpKeys() {
			if (logger.isDebugEnabled()) {
				logger.debug("kewrods are :<" + this.keywords + ">");
			}
			StringTokenizer st = new StringTokenizer(this.keywords, " ");

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.toLowerCase().equals("not")) {
					String nextToken = null;
					try {
						nextToken = st.nextToken();
					} catch (NoSuchElementException e) {
						nextToken = null;
					}
					if (nextToken != null) {
						this.notKeys.add(nextToken);
						addToLine(nextToken, NOT);
					} else {
						//fix for cause when "not" appears as part of keyword
						this.keys.add(token);
					}
				} else if (token.toLowerCase().equals("or")) {
					String nextToken = null;
					try {
						nextToken = st.nextToken();
					} catch (NoSuchElementException e) {
						nextToken = null;
					}
					if (nextToken != null) {
						this.orKeys.add(nextToken);
						addToLine(nextToken, OR);
					} else {
						//fix for cause when "or" appears as part of keyword
						this.keys.add(token);
					}
				} else {
					this.keys.add(token);
					if (!token.startsWith("+")) {
						addToLine(token, PLUS);
					}
				}
			}
		}

		private void addToLine(String key, String prefix) {
			this.line = prefix + key + " " + this.line;
		}

		private void setUpResults() {
			Perl5Util util = new Perl5Util();
			util.match("/\"(.+?)\"/", this.keywords);
			MatchResult matchResult = null;
			try {
				matchResult = util.getMatch();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (matchResult != null) {
				for (int i = 0; i < matchResult.groups(); i++) {
					String one = (String) matchResult.group(i);
					String newKeywords = this.keywords.replaceAll(one, "");
					String rep = strip(one);
					this.results.add(rep);
					this.keywords = newKeywords;
				}
			}
		}

		boolean find(String matcher, boolean skipNot) {
			if (matcher == null) {
				return false;
			}

			boolean found = true;

			for (String key : this.keys) {
				if (matcher.lastIndexOf(key) == -1) {
					found = false;
				}
			}

			if (!skipNot) {
				for (String notKey : this.notKeys) {
					if (matcher.lastIndexOf(notKey) != -1) {
						found = false;
					}
				}
			}

			for (String orKey : this.orKeys) {
				if (matcher.lastIndexOf(orKey) != -1) {
					found = found & true;
				}
			}
			return found;
		}

		boolean searchInIndex(org.dom4j.Document doc) {
			logger.warn("Search in index");
			boolean bookmarkFound = false;
			String id = doc.getRootElement().element("message_id")
					.attributeValue("value");
			try {
				Analyzer analyzer = new StandardAnalyzer();
				Query query = new QueryParser("contents", analyzer)
						.parse(this.line);
				String bdir = System.getProperty("user.dir");
				String fsep = System.getProperty("file.separator");
				String threadId = doc.getRootElement().element("thread_id")
						.attributeValue("value");
				File indexFile = new File((bdir + fsep + "urls" + fsep
						+ threadId + fsep + "index"));
				Searcher searcher = null;
				if (indexFile.exists()) {
					searcher = new IndexSearcher(indexFile.getAbsolutePath());
				} else {
					searcher = new IndexSearcher("index");
				}
				Hits hits = searcher.search(query);
				for (int i = 0; i < hits.length(); i++) {
					Document document = hits.doc(i);
					String testID = document.get("id");
					if (testID.equals(id)) {
						bookmarkFound = true;
					}
				}
				searcher.close();
			} catch (Exception ioe) {
			}
			return bookmarkFound;
		}

	}

}
