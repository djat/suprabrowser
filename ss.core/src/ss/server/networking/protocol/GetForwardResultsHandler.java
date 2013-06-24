package ss.server.networking.protocol;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

/**
 * @deprecated
 * @author dankosedin
 *
 */
public class GetForwardResultsHandler implements ProtocolHandler {

	private static final String FINAL_END_MOMENT = "finalEndMoment";

	private static final String RELATIVE_START_MOMENT = "relativeStartMoment";

	private static final String CURRENT_PAGE = "currentPage";

	private Logger logger = SSLogger.getLogger(this.getClass());
	
	/**
	 * @deprecated
	 * @param peer
	 */
	public GetForwardResultsHandler() {
		
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_FORWARD_RESULTS;
	}

	public void handle(Hashtable update) {
		handleGetForwardResults(update);
	}

	public void handleGetForwardResults(final Hashtable update) {
		this.logger.info("getforward on server");
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document sphereDefinition = (Document) update.get(SC.SPHERE_DEFINITION);
		String currentPage = (String) update.get(SC.CURRENT_PAGE);

		String queryId = (String) session.get(SC.QUERY_ID);
		this.logger.info("QUERY ID: " + queryId);
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : filteredHandlers) {

			if (handler.currentQueries != null) {
				this.logger.info("GET CURRENT QUERIE: "
						+ handler.currentQueries.asXML());

				String apath = "//queries/query[@id=\"" + queryId + "\"]";
				String relativeStart = null;
				String finalEnd = null;
				this.logger.info("Currentpagechecking: " + currentPage + " : "
						+ handler.currentQueries.asXML());
				List currQueries = getCurrentQueries(apath, handler);
				for (Object ob : currQueries) {
					Element elem = (Element) ob;
					this.logger.info("elem.asxml: " + elem.asXML());
					String current = elem.attributeValue(CURRENT_PAGE);
					if (current.equals(currentPage)) {
						relativeStart = elem
								.attributeValue(RELATIVE_START_MOMENT);
						finalEnd = elem.attributeValue(FINAL_END_MOMENT);
					}
				}

				try {
					handler.sendExistingQuery(sphereDefinition, queryId,
							relativeStart, finalEnd, new Integer(currentPage)
									.intValue());
				} catch (NumberFormatException exc) {
					this.logger.error("Number Format Exception", exc);
				} catch (DocumentException exc) {
					this.logger.error("Document Exception", exc);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List getCurrentQueries(String apath, DialogsMainPeer handler) {
		List list = new ArrayList();
		;
		Object currQueries = handler.currentQueries.selectObject(apath);
		if (currQueries != null) {
			if (currQueries instanceof Element) {
				list.add(currQueries);
			} else {
				list = (List) currQueries;
			}
		}
		return list;
	}
}
