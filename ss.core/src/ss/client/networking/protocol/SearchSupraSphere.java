/**
 * Jul 4, 2006 : 6:13:06 PM
 */
package ss.client.networking.protocol;

import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.actions.SearchSupraSphereFreeAction;
import ss.client.networking.protocol.getters.SearchSupraSphereCommand;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tempComponents.SupraSearchControlPanel;
import ss.common.SearchSettings;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.IdItem;
import ss.domainmodel.SearchResultCollection;
import ss.domainmodel.SearchResultObject;
import ss.domainmodel.SupraSearchResultsObject;
import ss.framework.networking2.ReplyObjectHandler;
import ss.search.SupraSearchDataSource;
import ss.server.networking.SC;
import ss.suprabrowser.MozillaBrowserController;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 */
public class SearchSupraSphere {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(SearchSupraSphere.class);
	
	//private static final ResourceBundle bundle = ResourceBundle.getBundle("baseName");

	private static String BOTTOM = "</body></html>";

	private static String TOP = "<html><head></head><body bgcolor=\"white\">";

	private final DialogsMainCli cli;
	
	private final String sQuery;
	
	private final Query query;

	private final boolean isAnonymous;
	
	private final boolean inSameTab;
	

	public SearchSupraSphere(DialogsMainCli cli, String sQuery, Query query, boolean loadInSameTab, boolean isAnonymous) {
		this.cli = cli;
		this.sQuery = sQuery;
		this.query = query;
		this.inSameTab = loadInSameTab;
		this.isAnonymous = isAnonymous;
	}

	@SuppressWarnings("unchecked")
	public static String getHtml(final SupraSearchResultsObject resultsObject, final String query, final boolean isAnonymous) {
		String html = "<input type=hidden id=\"keyword\" value=\""+query+"\">";
		SearchResultCollection results = resultsObject.getResults();
		if (results.getCount() > 0) {
			html += "Showing <I>" + resultsObject.getResultsCount() + " of " + resultsObject.getTotalCount()
					+ "</I> results for: <I>"
					+ query
					+ "</I><br><HR><br> ";
			for (SearchResultObject result : results) {
				html = html + getresultView(result, resultsObject.getKeywordsQuery(), isAnonymous)
						+ "<HR ALIGN=LEFT WIDTH=\"50%\" SIZE=\"3\">" + "<br>";
			}
		} else {
			html = "No results found";
		}
		return TOP + html + BOTTOM;
	}

	private static String getresultView(SearchResultObject result, String keywordQuery, boolean isAnonymous) {
		String giver = result.getGiver();
		String subject = result.getSubject();
		String type = result.getType();
		String body = result.getBody();
		String content = result.getContent();
		String comment = result.getComment();
		String contact = result.getContact();
		String keywords = result.getKeywords();
		String address = result.getAddress();
		String role = result.getRole();
		
		if(body!=null && (body.endsWith("<br>") || body.endsWith("<br"))) {
			body = body.substring(0, body.length()-4);
		}

		IdItem firstIdItem = result.getIdCollection().get(0); 
		String html = getDivTag(type, firstIdItem.getSphereId(), firstIdItem.getMessageId());
		html += "<table>";
		if(type.equals("bookmark")) {
			if ((address != null) && (!address.equals(""))) {
				html += "<td>"
						+ getFontTag(getOpenURLAnchorTag(firstIdItem.getSphereId(), firstIdItem.getMessageId(),address, address),
								"search_address") + "</td></tr>";
			}
		} else {
			html += "<tr><td>"
				+ getFontTag(getAnchorTag(firstIdItem.getSphereId(), firstIdItem.getMessageId(), keywordQuery,
						subject), "search_sphereview") + "</td></tr>";
		}
		html += "</table>";
		
		html += getFormattedRow(subject, "Subject: ", "search_subj");
		html += isAnonymous ? "" : getFormattedRow(body, "Body: ", "search_body");
		html += isAnonymous ? "" : getFormattedRow(content, "Content: ", "search_body");
		html += isAnonymous ? "" : getFormattedRow(comment, "Comment: ", "search_body");
		html += isAnonymous ? "" : getFormattedRow(contact, "Contact: ", "search_body");
		if (StringUtils.isNotBlank(role)){
			html += isAnonymous ? "" : getFormattedRow(role, "Type: ", "search_body");
		}
		html += isAnonymous ? "" : getFormattedKeywordRow(keywords, "Tags: ", "search_body");

		
		String inSphereString = result.getIdCollection().getCount()>1 ? "In spheres " : "In sphere ";  
		html += isAnonymous ? "" : getFontTag(
				inSphereString, "search_type");
		
		for(IdItem item : result.getIdCollection()) {
			String displayName = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getDisplayName(item.getSphereId());
			html += isAnonymous ? "" : getFontTag(getAnchorTagWithSphereStyle(item.getSphereId(), item.getMessageId(), keywordQuery,
					displayName), "search_sphere");
			html += ", ";
		}
		html = html.substring(0, html.length()-2);
	
		html += isAnonymous ? "" : getFontTag(" by ", "search_type");
		html += isAnonymous ? "" : getFontTag(giver, "search_giver");
		html += isAnonymous ? "" : " , " + getFontTag(type, "search_type") + "</div>";
		
		return html;
	}

	/**
	 * @param keywords
	 * @param string
	 * @param string2
	 * @return
	 */
	private static String getFormattedKeywordRow(String keywords,
			String label, String style) {
		String html = "";
		if ((keywords != null) && (!keywords.equals(""))) {
			html = getFontTag(label, "search_label") + getFontKeywordTag(keywords, style)
					+ "<br>";
		}
		return html;
	}

	/**
	 * @param keywords
	 * @param style
	 * @return
	 */
	private static String getFontKeywordTag(String keywords, String style) {
		String[] splitKeyword = keywords.split(",");
		String toReturn = "";
		for(String str : splitKeyword) {
			String keyword = str;
			if(str.indexOf('>') > -1 && str.lastIndexOf('<')>-1) {
				keyword = str.substring(str.indexOf('>'), str.lastIndexOf('<'));
			}
			String function = "research_object.addClickMark('"+keyword+"')";
			toReturn += "<u style=\"cursor:pointer;\" onclick=\""+function+"\">"+str+"</u>, ";
		}
		return toReturn.substring(0, toReturn.length()-2);
	}

	private static String getFormattedRow(String content, String label, String style) {
		String html = "";
		if ((content != null) && (!content.equals(""))) {
			html = getFontTag(label, "search_label") + getFontTag(content, style)
					+ "<br>";
		}
		return html;
	}

	private static String getAnchorTag(String sphereID, String messageID,
			String keywords, String content) {
		String tag = "<a href=\"javascript:search_object.show('" + sphereID
				+ "','" + messageID + "','" + keywords + "');\">" + unboldText(content)
				+ "</a>";
		return tag;
	}
	
	private static String getAnchorTagWithSphereStyle(String sphereID, String messageID,
			String keywords, String content) {
		String tag = "<a style=\"color:green;\" href=\"javascript:search_object.show('" + sphereID
				+ "','" + messageID + "','" + keywords + "');\">"+content+ "</a>";
		return tag;
	}

	private static String getOpenURLAnchorTag(String sphereId, String messageId, String URL, String content) {
		String tag = "<a href=\"javascript:search_object.open('" + URL
				+"','"+sphereId+"','"+messageId+ "');\" align=right>" + content + "</a>";
		return tag;
	}

	/**
	 * @param content
	 * @param style
	 * @return
	 */
	private static String getFontTag(String content, String style) {
		String tag = "<font class=\"" + style + "\">" + content + "</font>";
		return tag;
	}

	/**
	 * @param messageID
	 * @return
	 */
	private static String getDivTag(String type, String sphereId, String messageID) {
		String string = "<div id=\""
				+ messageID
				+ "\" type=\""+type+"\" style=\"background-color:white\" onmousedown=\"scroller_object.on_result_click(event, '"+sphereId+"', '"
				+ messageID + "');\">";
		return string;
	}

	@SuppressWarnings("unchecked")
	public void searchSupraSphere() {
		SearchSupraSphereCommand command = new SearchSupraSphereCommand();
		SearchSettings settings = new SearchSettings(this.query, this.sQuery, this.isAnonymous, false);
		command.putArg(SessionConstants.SEARCH_SETTINGS, settings);
		logger.info("Putting keywords; "+sQuery);
		command.beginExecute(this.cli, new ReplyObjectHandler<AbstractDocument>( AbstractDocument.class ) {
			@Override
			protected void objectReturned(AbstractDocument reply) {
				processDoc(reply, null, null );
			}
		} );
	}
	
	private String getSQuery() {
		return this.sQuery;
	}

	@SuppressWarnings("unchecked")
	public void showPageofSearchSupraSphere(String queryId, String pageId,
			final SupraBrowser browser, final SupraSearchControlPanel panel) {
		if(browser==null || browser.isDisposed()) {
			logger.error("browser is null or disposed");
		}
		SearchSupraSphereCommand command = new SearchSupraSphereCommand();
		SearchSettings settings = new SearchSettings(null, this.sQuery, false, false);
		command.putArg(SessionConstants.QUERY_ID, queryId );
		command.putArg(SessionConstants.PAGE_ID, pageId);
		command.putArg(SessionConstants.SEARCH_SETTINGS, settings);
		command.beginExecute(this.cli, new ReplyObjectHandler<AbstractDocument>( AbstractDocument.class ) {
			@Override
			protected void objectReturned(AbstractDocument reply) {
				processDoc(reply, browser, panel );
			}
		} );
		
	}

	private void processDoc(final Document doc, final SupraBrowser browser,
			final SupraSearchControlPanel panel) {
		logger.info("Document processed.....:" + doc.asXML());
		final SupraSearchResultsObject resultsObject = SupraSearchResultsObject.wrap(doc);
		
		final String html = getAdvancedBlock()+getHtml(resultsObject, this.sQuery, this.isAnonymous);
		if(this.inSameTab) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					if(browser!=null) {
						browser.setText(html);
					} else {
						SupraSphereFrame.INSTANCE.getActiveBrowser().setText(html);
					}
				}
			});
			return;
		}
		
		final int queryId = resultsObject.getId();
		final int pageId = resultsObject.getPageId();
		final int pageCount = resultsObject.getPageCount();
		if (browser == null) {

			final SupraCTabItem item = SupraSphereFrame.INSTANCE.addSupraSearchTab(
					this.cli.session,
					"Search Result",
					new SupraSearchDataSource(browser, html, "", this.sQuery,
							queryId, pageId, pageCount), true);
			UiUtils.swtInvoke(new Runnable() {
				public void run() {
					item.addDisposeListener(new DisposeListener() {
						@SuppressWarnings("unchecked")
						public void widgetDisposed(DisposeEvent arg0) {
							SearchSupraSphereFreeAction command = new SearchSupraSphereFreeAction();
							String sId = String.valueOf(queryId);
							command.putArg(SC.FREE_QUERY_ID, sId);
							command.beginExecute( SearchSupraSphere.this.cli );
						}
					});
				}
			});
		} else {
			UiUtils.swtBeginInvoke( new Runnable() {
				public void run() {
					setUpBrowser(html, browser, panel, new SupraSearchDataSource(
							browser, html, "", getSQuery(), queryId, pageId, pageCount));
				}
			} );
		}
	}

	private void setUpBrowser(final String html, final SupraBrowser browser,
			final SupraSearchControlPanel panel,
			final SupraSearchDataSource source) {
		if(browser==null || browser.isDisposed()) {
			logger.error("Browser is null or disposed");
			return;
		}
		browser.setText(html);
		browser.addProgressListener(new ProgressListener() {

			public void changed(ProgressEvent e) {

			}

			public void completed(ProgressEvent e) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						if (panel != null) {
							panel.activate(browser, source);
						}
						MozillaBrowserController controller = browser
								.getMozillaBrowserController();
						controller.injectJS();
						browser.scrollToTop();
					}
				});
				browser.removeProgressListener(this);
			}
		});
	}
	
	private static String unboldText(String text) {
		return text.replaceAll("class=\"search_hightlightbody\"", "");
	}
	
	public final static String getAdvancedBlock() {
		String html = "<div style=\"margin-left:0px; border-style:solid; border-width:thin; border-color:rgb(80,80,80); background-color:rgb(230,232,230);\">";
		html += "<div style=\"margin:0px;\"><button id=\"spoiler_button\" onclick=\"advanced_object.performAdvance();\" style=\"margin:5px; width:22px;\" type=button>+</button><input style=\"margin:5px; position:relative; width:85%;\" type=text onkeydown=\"advanced_object.search_from_field(event);\" id=\"to_search\"><button style=\"margin:5px;\" onclick=\"advanced_object.search();\" type=button>Search</button></div>";
		html += "<div style=\"font-size:14;\" id=\"spoiler\"></div>";
		html += "</div><hr>";
		return html;
	}
}
