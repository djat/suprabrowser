/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.SearchMostRecentMessagesInSphereCommand;
import ss.common.StringUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.IdItem;
import ss.domainmodel.SearchResultObject;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSearchResultsObject;
import ss.framework.networking2.CommandHandleException;
import ss.rss.XSLTransform;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SearchMostRecentMessagesInSphereHandler extends
		AbstractGetterCommandHandler<SearchMostRecentMessagesInSphereCommand, AbstractDocument> {

	private static final int DEFAULT_MESSAGES_COUNT = 10;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchMostRecentMessagesInSphereHandler.class);
	
	public SearchMostRecentMessagesInSphereHandler(DialogsMainPeer peer) {
		super(SearchMostRecentMessagesInSphereCommand.class, peer);
	}

	@Override
	protected AbstractDocument evaluate( final SearchMostRecentMessagesInSphereCommand command )
			throws CommandHandleException {
		final String sphereId = command.getSphereId();
		if ( StringUtils.isBlank( sphereId ) ) {
			logger.error("SphereId is blank");
			return null;
		}
		final Vector<Document> docs = this.peer.getXmldb().getMostRecentCreatedMessages(sphereId, DEFAULT_MESSAGES_COUNT);
		if (docs == null) {
			logger.error("Docs is null as recent messages");
			return null;
		}	
		return getResult( docs, sphereId );
	}
	
	private AbstractDocument getResult( final Vector<Document> docs, final String sphereId ){
		SupraSearchResultsObject supraSearchresultObject = new SupraSearchResultsObject();
		int count = 0;
		for ( Document doc : docs ) {
			if (doc != null) {
				try {
					supraSearchresultObject.getResults().add( getSearchResultObject( Statement.wrap(doc), sphereId ) );
					count++;
				} catch (Exception ex) {
					logger.error("Error in adding next result for doc : " + doc.asXML(), ex);
				}
			}
		}
		supraSearchresultObject.setKeywordsQuery("" + count + " most recent messages");
		supraSearchresultObject.setTotalCount( count );
		supraSearchresultObject.setResultsCount( count );
		supraSearchresultObject.setPageCount( 1 );
		supraSearchresultObject.setPageId( 1 );
		supraSearchresultObject.setId( 1 );

		return (AbstractDocument) supraSearchresultObject.getBindedDocument();
	}
	
	private SearchResultObject getSearchResultObject( final Statement st, final String sphereId ) {
		SearchResultObject resultObject = new SearchResultObject();
		resultObject.setSubject(st.getSubject());
		
		if (st.isBookmark()) {
			resultObject.setAddress(st.getAddress());
		}
		resultObject.setBody(st.getBody());
		
		if (st.isComment()) {
			CommentStatement statment = CommentStatement.wrap(st.getBindedDocument());
			String comment = statment.getComment();
			resultObject.setComment((comment != null) ? comment : "");
		}
		if (st.isContact()) {
			String contact = XSLTransform.transformContact(st.getBindedDocument());
			resultObject.setContact((contact != null) ? contact : "");
			resultObject.setRole(ContactStatement.wrap(st.getBindedDocument()).getRole());
		}
		if (st.isSphere()) {
			resultObject.setRole(SphereStatement.wrap(st.getBindedDocument()).getRole());
		}
		
		resultObject.setContent("");
		
		resultObject.setGiver(st.getGiver());
		resultObject.setKeywords( getKeywords( st.getBindedDocument() ) );
		resultObject.setType(st.getType());
		
		IdItem item = new IdItem();
		item.setMessageId( st.getMessageId() );
		item.setSphereId( sphereId );
		resultObject.getIdCollection().add(item);
		
		return resultObject;
	}
	
	private String getKeywords(org.dom4j.Document doc) {
		String keywords = "";
		Element root = doc.getRootElement();

		Element search = root.element("search");
		if (search != null) {
			Element interest = search.element("interest");
			if (interest != null) {
				List<Element> keywordsList = interest.elements("keywords");
				if ( keywordsList != null ) {
					boolean addComma = false;
					for (Element e : keywordsList) {
						if (addComma) {
							keywords += ", ";
						} else {
							addComma = true;
						}
						keywords += e.attributeValue("value");
					}
				}
			}
		}
		return keywords.trim();
	}
}
