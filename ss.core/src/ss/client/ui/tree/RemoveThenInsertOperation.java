/**
 * 
 */
package ss.client.ui.tree;

import org.dom4j.Document;

import ss.client.ui.MessagesPane;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.common.XmlDocumentUtils;
import ss.common.operations.OperationBreakException;
import ss.domainmodel.Statement;

/**
 * 
 */
public class RemoveThenInsertOperation extends AbstractMessageTreeOperation {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RemoveThenInsertOperation.class);

	private final Statement statement;

	private final boolean onlyIfExists;

	private final boolean replacingKeyword;

	/**
	 * @param messagesPane
	 * @param statementDocument
	 */
	public RemoveThenInsertOperation(MessagesPane messagesPane,
			Document statementDocument, boolean onlyIfExists) {
		super( messagesPane );
		if (logger.isDebugEnabled()) {
			logger
					.debug("Calling remove, insert for doc, that was part of ack: "
							+ XmlDocumentUtils
									.toPrettyString(statementDocument));
		}
		this.statement = Statement.wrap(statementDocument);
		this.onlyIfExists = onlyIfExists;
		this.replacingKeyword = processReplacingKeyword(statementDocument);
	}

	/**
	 * @param statementDocument
	 * @return
	 */
	private static boolean processReplacingKeyword(Document statementDocument) {
		boolean replacingKeywordTemp = false;
		if (statementDocument.getRootElement().element("replacingKeyword") != null) {
			statementDocument.getRootElement().element("replacingKeyword")
					.detach();
			replacingKeywordTemp = true;
		}
		return replacingKeywordTemp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.operations.AbstractOperation#performRun()
	 */
	@Override
	protected void performRun() throws OperationBreakException {
		if (logger.isDebugEnabled()) {
			logger.debug("Start of remove then insert operation");
		}
		updateResponseIdForSphere( this.statement );
		if(this.messagesPaneOwner.getLastSelectedDoc()!=null) {
			Statement lastSelected = Statement.wrap(this.messagesPaneOwner.getLastSelectedDoc());
			if(lastSelected.getMessageId().equals(this.statement.getMessageId())) {
				this.messagesPaneOwner.setLastSelected(this.statement.getBindedDocument());
			}
		}
		boolean replaced = replaceExistedNode();
		if ( !this.onlyIfExists && !replaced) {
			createAndInsert();
		}
		// Now do the same for the table
		updateStable();
		this.messagesPaneOwner.getMessagesTree().update();
		updatePresenceRenderer(this.statement.getDocumentCopy());
		this.messagesPaneOwner.repaintAll();
	}

	/**
	 * @param response_id
	 * @param message_id
	 * @param title
	 */
	private void createAndInsert() {
		final String responseId = this.statement.getResponseId();
		if (responseId == null) {
			this.messagesPaneOwner.getMessagesTree().insertNodeIfResponseIdIsNull(this.statement, false);
		} else {
			this.messagesPaneOwner.getMessagesTree().insertNodeIfResponseIdNotNull(this.statement, false, false);
		}
	}

	

	/**
	 * @param message_id
	 * @param author
	 * @param moment
	 * @param subject
	 * @param threshold
	 */
	private void updateStable() {
		
		
		final boolean threshold = this.statement.getConfirmed();
		final String author = this.statement.getGiver();

		this.messagesPaneOwner.getMessagesTable().replaceStatement(
				this.statement);
//		this.messagesPaneOwner.getMessagesTable().refreshColors();
		
		String sphereType = this.messagesPaneOwner.client
		.getVerifyAuth().getSphereType(
				(String) this.messagesPaneOwner.getRawSession()
						.get("sphere_id"));
		
		if (!sphereType.equals("group")) {
			if ((!this.statement.isConfirmedDefined()
					|| this.statement.getConfirmed()) && !author
					.equals((String) this.messagesPaneOwner.getRawSession()
							.get("real_name"))) {
				recallPopup(this.statement.getDocumentCopy());
			}
		} else {
			if (threshold == true) {
				if (hasVoted((String) this.messagesPaneOwner.getRawSession()
						.get("real_name"), this.statement
						.getDocumentCopy())) {
					recallPopup(this.statement.getDocumentCopy());
				}
			}
		}
	}

	/**
	 * @param message_id
	 * @param found
	 * @return
	 */
	private boolean replaceExistedNode() {
		boolean flag = this.messagesPaneOwner.getMessagesTree()
				.replaceExistedNode(this.statement, this.replacingKeyword);
		if (flag) {
			addToAllMessages(this.statement.getMessageId(), this.statement);
		}
		return flag;
	}

	/**
	 * @param doc
	 */
	private void updatePresenceRenderer(Document doc) {
		this.messagesPaneOwner.updatePresenceRenderer(doc);
	}

	/**
	 * @param contact_name
	 * @param document
	 * @return
	 */
	private boolean hasVoted(String contact_name, Document document) {
		return this.messagesPaneOwner.hasVoted(contact_name, document);
	}

	/**
	 * @param document
	 */
	private void recallPopup(Document document) {
		PopUpController.INSTANCE.recallPopup(document);
	}

	/**
	 * @param message_id
	 * @param statement
	 */
	private void addToAllMessages(String message_id, Statement statement) {
		this.messagesPaneOwner.addToAllMessages(message_id, statement);
	}

	

}
