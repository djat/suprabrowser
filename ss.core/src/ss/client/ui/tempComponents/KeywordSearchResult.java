/**
 * 
 */
package ss.client.ui.tempComponents;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.dom4j.tree.AbstractDocument;
import org.mozilla.interfaces.nsIDOMHTMLImageElement;

import ss.client.networking.protocol.SearchSupraSphere;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.researchcomponent.ResearchInfoController;
import ss.common.FileUtils;
import ss.common.LocationUtils;
import ss.common.PathUtils;
import ss.common.SearchSettings;
import ss.domainmodel.SupraSearchResultsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class KeywordSearchResult {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(KeywordSearchResult.class);
	
	public static final void addFlyer(SupraBrowser browser, String keyword) {
		if(browser.getDomHtmlDocument().getElementById("flyer")!=null) {
			return;
		}
		
		Query query = createQuery(keyword);
		SearchSettings settings = new SearchSettings(query, keyword, false, false);
		AbstractDocument doc = SupraSphereFrame.INSTANCE.client.searchTagged(settings);
		SupraSearchResultsObject resultObject = SupraSearchResultsObject.wrap(doc);
		String html = SearchSupraSphere.getHtml(resultObject, keyword, false);
		html = html.replaceAll("\"", "&quot;");
		html = html.replaceAll("'", "&#39;");
		int index = html.indexOf("<body");
		int endIndex = html.indexOf("</body>");
		int indexStart = html.indexOf(">", index);
		browser.execute("research_object.stateChange('"+html.substring(indexStart+1, endIndex)+"')");
		String pathToImage = PathUtils.combinePath( LocationUtils.getMicroblogBase(), "microblog", "Error16.png" );
		pathToImage = FileUtils.toUri(pathToImage).toString();
		nsIDOMHTMLImageElement closerImage = (nsIDOMHTMLImageElement)browser.getDomHtmlDocument().getElementById("flyer_closer").queryInterface(nsIDOMHTMLImageElement.NS_IDOMHTMLIMAGEELEMENT_IID);
		closerImage.setSrc(pathToImage);
	}
	
	public static final void loadResultInBrowser(SupraBrowser browser, String keyword) {
		try {
		Query query = createQuery(keyword);
		AbstractDocument doc = SupraSphereFrame.INSTANCE.client.searchTagged(new SearchSettings(query, keyword, false, false));
		SupraSearchResultsObject resultObject = SupraSearchResultsObject.wrap(doc);
		String html = SearchSupraSphere.getHtml(resultObject, keyword, false);
		browser.setText(html);
		} catch (Throwable ex) {
			logger.error("Error",ex);
		}
	}
	
	
	

	/**
	 * @return
	 */
	private static Query createQuery(String keyword) {
		String rawQuery = getRawQuery( keyword );
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(rawQuery);
		} catch (ParseException ex) {
			System.out.println("error" + ex);
		}
		return query;
	}
	
	private static String getRawQuery( final String keyword ){
		String rawQuery = "+((+keywords:"+keyword+")) +(type:terse type:message type:comment type:bookmark type:keywords type:contact type:file type:externalemail)";
		if ( ResearchInfoController.INSTANCE.getDataProvider().isContactsAsKeywords() ) {
			if (ResearchInfoController.INSTANCE.getDataProvider().containsContactIgnoreCase(keyword)) {
				rawQuery = "+( subject:(+" + keyword + ") content:(+" + keyword + ") comment:(+" + keyword + ") body:(+" + keyword + ") contact:(+" + keyword + ") keywords:(+" + keyword + ")) +(type:( ||contact))";
			}
		}
		return rawQuery;
	}
}
