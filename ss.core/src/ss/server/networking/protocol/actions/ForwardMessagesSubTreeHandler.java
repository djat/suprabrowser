/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.ForwardMessagesSubTreeAction;
import ss.common.SSProtocolConstants;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.protocol.PublishForwardedMessagesHandler;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author roman
 *
 */
public class ForwardMessagesSubTreeHandler extends AbstractActionHandler<ForwardMessagesSubTreeAction> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForwardMessagesSubTreeHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public ForwardMessagesSubTreeHandler(DialogsMainPeer peer) {
		super(ForwardMessagesSubTreeAction.class, peer);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(ForwardMessagesSubTreeAction action) {
		Hashtable update = action.getSessionArg();
		handleForwarding(update);
	}
	
	
	public List<Document> getHandledDocumentsForSphere(Hashtable session, final List<Document> docs, final String newSphereId) {
		List<Document> newDocs = new ArrayList<Document>();
		
		List<Document> oldDocs = docs;
		Hashtable<String, String> identifiersTable = new Hashtable<String, String>();
		initIdentifiersTable(identifiersTable, oldDocs);
		
		String threadId = findThreadId(identifiersTable, oldDocs);
		String threadType = findThreadType(oldDocs);
		
		for(Document doc : oldDocs) {
			Statement newStatement = recreateDoc(identifiersTable, oldDocs, session, doc, threadId, threadType, newSphereId);
			newDocs.add(newStatement.getBindedDocument());
		}
		return newDocs;
	}

	private Statement recreateDoc(Hashtable<String, String> identifiers, List<Document> oldDocs, Hashtable session, Document doc, String threadId, String threadType, String newSphereId) {
		Statement statement = Statement.wrap(doc);
		Statement newStatement = Statement.wrap(statement.getDocumentCopy());
		
		String contactName = (String)session.get(SessionConstants.REAL_NAME);
		String login = (String)session.get(SessionConstants.USERNAME);
		
		newStatement.setMoment((String)null);
		newStatement.setLastUpdated((String)null);
		newStatement.getVotedMembers().clear();
		newStatement.setGiver(contactName);
		newStatement.setGiverUsername(login);
		newStatement.setVotingModelType("absolute");
		newStatement.setVotingModelDesc("Absolute without qualification");
		newStatement.setTallyNumber("0.0");
		newStatement.setTallyValue("0.0");
		
		if(statement.getResponseId()!=null) {
			Statement parent = getParent(oldDocs, statement.getResponseId());
			if(parent!=null) {
				newStatement.setResponseId(identifiers.get(parent.getMessageId()));
			} else {
				newStatement.setResponseId(null);
			}
		}
		
		newStatement.setConfirmed(true);
		newStatement.setPassed(true);
		newStatement.setWorkflowType("normal");
		newStatement.setOriginalId(identifiers.get(statement.getMessageId()));
		newStatement.setMessageId(identifiers.get(statement.getMessageId()));
		newStatement.setThreadId(threadId);
		newStatement.setThreadType(threadType);
		newStatement.setCurrentSphere(newSphereId);
		
		checkIsComment(identifiers, oldDocs, statement, newStatement);
		
		return newStatement;
	}

	private void checkIsComment(Hashtable<String, String> identifiers, List<Document> oldDocs, Statement statement, Statement newStatement) {
		if(statement.isComment()) {
			CommentStatement comment = CommentStatement.wrap(statement.getDocumentCopy());
			Statement parent = getParent(oldDocs, comment.getCommentId());
			if(parent != null) {
				CommentStatement newComment = CommentStatement.wrap(newStatement.getBindedDocument());
				newComment.setCommentId(identifiers.get(parent.getMessageId()));
			}
		}
	}

	/**
	 * @return
	 */
	private String findThreadId(Hashtable<String, String> identifiersTable, List<Document> oldDocs) {
		for(Document doc : oldDocs) {
			Statement statement = Statement.wrap(doc);
			if(statement.getResponseId()==null || getParent(oldDocs, statement.getResponseId())==null) {
				return identifiersTable.get(statement.getMessageId());
			}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	private String findThreadType(List<Document> oldDocs) {
		for(Document doc : oldDocs) {
			Statement statement = Statement.wrap(doc);
			if(statement.getResponseId()==null || getParent(oldDocs, statement.getResponseId())==null) {
				return statement.getType();
			}
		}
		return null;
	}

	/**
	 * @param responseId
	 * @return
	 */
	private Statement getParent(List<Document> oldDocs, String responseId) {
		for(Document doc : oldDocs) {
			Statement statement = Statement.wrap(doc);
			if(statement.getMessageId().equals(responseId)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private void initIdentifiersTable(Hashtable<String, String> identifiers, List<Document> oldDocs) {
		for(Document doc : oldDocs) {
			Statement statement = Statement.wrap(doc);
			
			String newId = VariousUtils.createMessageId();
			
			identifiers.put(statement.getMessageId(), newId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleForwarding(Hashtable update) {
		Hashtable session = (Hashtable)update.get(SessionConstants.SESSION);
		List<String> sphereIds = (List<String>)update.get("sphereList");
		List<Document> docs = (List<Document>)update.get("docList");
		
		for(String sphereId : sphereIds) {
			forwardDocumentsToSphere(session, docs, sphereId);
		}
	}

	@SuppressWarnings("unchecked")
	private void forwardDocumentsToSphere(Hashtable session, List<Document> docs, String sphereId) {
		List<Document> newDocs = getHandledDocumentsForSphere(session, docs, sphereId);
		
		session.put(SC.SPHERE_ID, sphereId);
		
		for(Document doc : newDocs) {
			Hashtable sendTable = new Hashtable();
			sendTable.put(SessionConstants.SESSION, (Hashtable)session.clone());
			sendTable.put(SessionConstants.DOCUMENT, doc);
			
			PublishForwardedMessagesHandler publisher = (PublishForwardedMessagesHandler)this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH_FORWARDED);
			publisher.handle(sendTable);
		}
	}

}
