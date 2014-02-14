/**
 * 
 */
package ss.client.event.tagging;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import org.dom4j.Document;

import ss.client.event.tagging.gui.ListForTagWindow;
import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;
import ss.server.networking.SC;

/**
 * @author zobo
 *
 */
public class TagManager {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TagManager.class);
	
	private TagManager(){
		
	}
	
	public static final TagManager INSTANCE = new TagManager();
	
	public void open( final MessagesPane pane, final Document doc ){
		if (doc == null) {
			logger.error("Doc is null");
			return;
		}
		open( pane, Statement.wrap(doc) );
	}
	
	public void open( final MessagesPane pane, final Statement statement ){
		if (statement == null) {
			logger.error("statement is null");
			return;
		}
		if (pane == null) {
			logger.error("pane is null");
			return;
		}
		if (statement.isKeywords()) {
			return;
		}
		new TaggsDisplayer( pane ).showTagsForSingleItem( statement.getBindedDocument() );
	}

	/**
	 * @param messagesPaneOwner
	 * @param toRemove
	 */
	public void open(final MessagesPane pane, final List<Document> docs) {
		new TaggsDisplayer( pane ).showTagsForList( docs );
	}
	
	public void open( final MessagesPane pane ){
		open( pane, (List<Document>) null);
	}
	
	public void close( final MessagesPane pane ){
		pane.hideKeyWords();
	}

	/**
	 * @param attribute
	 */
	public void tagClicked( final String tagName ) {
		if ( StringUtils.isBlank( tagName ) ){
			logger.error("TagName is blank");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("tagName clicked: " + tagName);
		}
		final DataForTagObtainer obtainer = new DataForTagObtainer( tagName, SupraSphereFrame.INSTANCE.client );
		(new ListForTagWindow( obtainer )).open();
	}
	
	public void tagNewForPreviewClicked( final String name, final String sphereId, final MessagesPane pane ) {
		if ( StringUtils.isBlank( name ) ){
			logger.error("uniqe is blank");
			return;
		}
		if ( StringUtils.isBlank( sphereId ) ){
			logger.error("sphereId is blank");
			return;
		}
		if ( pane == null ) {
			logger.error("MessagesPane is null");
			return;			
		}
		if (logger.isDebugEnabled()) {
			logger.debug("tagName : " + name);
		}
		logger.info("Here! : name: " + name + ", sphereId: " + sphereId);
		Hashtable session2 = (Hashtable)pane.getRawSession().clone();
		session2.put(SC.SPHERE_ID, sphereId);
		final Document doc = SupraSphereFrame.INSTANCE.client.getExistingQuery(session2, name, sphereId);
		if ( doc == null ) {
			logger.error(" Could not get a keyword doc ");
			return;
		}
		final TagInPreviewDisplayer displayer = new TagInPreviewDisplayer(pane, Statement.wrap( doc ), pane.getRawSession());
		displayer.processKeywordSelected();
	}

	/**
	 * @param mp
	 */
	public void openForSelected( final MessagesPane pane ) {
		List<Document> docs = UiUtils.swtEvaluate(new Callable<List<Document>>(){
			public List<Document> call() throws Exception {
				return pane.getMessagesTree().getSelectedDocs();
			}
		});
		if ( docs == null ) {
			return;
		}
		new TaggsDisplayer( pane ).showTagsForList(docs );
	}
	
	private String lastUpdatedTag;
	
	public synchronized void tagUpdated( final KeywordStatement updatedKeyword ){
		if ( updatedKeyword == null) {
			logger.error(" KeywordStatement is null ");
			return;
		}
		String keywordLook = XmlDocumentUtils.toPrettyString(updatedKeyword.getBindedDocument());
		if ( ( this.lastUpdatedTag != null ) && 
				( this.lastUpdatedTag.equals( keywordLook ) ) ) {
			return;
		}
		this.lastUpdatedTag = keywordLook;
		if(SupraSphereFrame.INSTANCE!=null) {
			for (MessagesPane pane : SupraSphereFrame.INSTANCE.tabbedPane.getMessagesPanes()){
				pane.tagUpdated( updatedKeyword );
			}
		}
	}

	/**
	 * @param statement
	 */
	public void keywordUpdated( final Document doc, final int type ) {
		final KeywordStatement st = KeywordStatement.wrap( doc );
		logger.info("Keyword recieved");
		logger.info("Subject: " + st.getSubject());
		logger.info("Type: " + getType(type));
		logger.info("Body:    " + st.getBindedDocument().asXML());
	}

	/**
	 * @param type
	 * @return
	 */
	private String getType(int type) {
		if (type == 1) {
			return "UpdateHandler";
		} else if (type == 2) {
			return "UpdateDocumentHandler";
		} else if (type == 3) {
			return "VoteDocumentHandler";
		}
		return "default";
	}
}
