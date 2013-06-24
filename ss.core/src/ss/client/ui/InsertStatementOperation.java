package ss.client.ui;

import java.util.Vector;

import javax.swing.text.BadLocationException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.custom.CTabItem;

import ss.client.ui.balloons.BalloonsController;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tree.AbstractMessageTreeOperation;
import ss.common.UiUtils;
import ss.common.operations.OperationBreakException;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

public class InsertStatementOperation extends AbstractMessageTreeOperation {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InsertStatementOperation.class);

	private boolean openTreeToMessageId;

	private boolean insertToSelectedOnly;
	
	private final Document emailDocument;

	private final Statement statement;

	private final Element emailRootElement;
	
	private final Statement emailStatement;

	private final String author;

	private final String type;

	private final String sphereId;

	private final String title;

	/**
	 * @param messagesPaneOwner
	 * @param statement
	 */
	public InsertStatementOperation(MessagesPane messagesPaneOwner,
			Statement statement) {
		super( messagesPaneOwner );
		this.statement = statement;
		this.emailDocument = this.statement.getBindedDocument();
		this.author = this.statement.getGiver();
		this.title = this.statement.getSubject() + ", by " + this.author;
		this.type = this.statement.getType();
		this.sphereId = (String) this.messagesPaneOwner.getRawSession().get("sphere_id");
		this.emailRootElement = this.emailDocument.getRootElement();
		this.emailStatement = statement;
	}
	
	
	/**
	 * @return the insertToSelectedOnly
	 */
	public boolean isInsertToSelectedOnly() {
		return this.insertToSelectedOnly;
	}

	/**
	 * @return the openTreeToMessageId
	 */
	public boolean isOpenTreeToMessageId() {
		return this.openTreeToMessageId;
	}

	/**
	 * @param insertToSelectedOnly
	 *            the insertToSelectedOnly to set
	 */
	public void setInsertToSelectedOnly(boolean insertToSelectedOnly) {
		this.insertToSelectedOnly = insertToSelectedOnly;
	}

	/**
	 * @param openTreeToMessageId
	 *            the openTreeToMessageId to set
	 */
	public void setOpenTreeToMessageId(boolean openTreeToMessageId) {
		this.openTreeToMessageId = openTreeToMessageId;
	}

	private boolean getConfirmed() {
		if ( this.statement.isConfirmedDefined() ) {
			return this.statement.getConfirmed();
		}
		else {
			return true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.operations.AbstractOperation#performRun()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void performRun() throws OperationBreakException {
		try {
			super.updateResponseIdForSphere( this.statement ); 	
			processMessagesThatHasTitleSameAsSphereId();
			boolean found;
			
			found = doInsert(this.emailDocument);
			
			if (!found){
				Vector docsToInsert = this.messagesPaneOwner.client.getEntireThread(
						this.messagesPaneOwner.getRawSession(),
						this.statement.getThreadId() );
				for(Object o : docsToInsert) {
					doInsert((Document)o);
				}
			} 
			
			updateTabbedPane();
			this.messagesPaneOwner.getMessagesTree().update();
			this.messagesPaneOwner.getMessagesTree().showMessage(this.emailStatement.getMessageId());
			this.messagesPaneOwner.repaintAll();
		} 
		catch (Exception ex) {
			logger.error( "Insert statement failed", ex );
		}
	}


	/**
	 * @return
	 * @throws BadLocationException 
	 */
	private boolean doInsert(final Document doc) throws BadLocationException {
		boolean found = true;
		Statement statement = Statement.wrap(doc);
		if (statement.getResponseId() != null && !statement.getResponseId().equals( "" ) ) {
			found = insertIfResponseIdNotNull(doc);
		} else { 
			insertIfResponseIdIsNull(doc);
			found = true;
		}
		if(found) {
			this.messagesPaneOwner.addToAllMessages(statement.getMessageId(), statement);
			updateSTable(doc);
			updateCommentPreviewAndSmallBrowser(doc);
		}
		return found;
	}

	private void updateTabbedPane() {
		SupraCTabItem item = this.messagesPaneOwner.getTabItem();
		if (item != null) {
			try {
				final String giver = this.statement.getGiver();
				if ( giver == null ||
					 !giver.equals( this.messagesPaneOwner.getVerbosedSession().getUserContactName() ) ) {
					item.mark();
				}
			} catch (Exception e) {
				logger.error("updateTabbedPane failed", e);
			}
		}
	}

	public void updateCommentPreviewAndSmallBrowser(final Document doc)
	throws BadLocationException {
		if(this.messagesPaneOwner.getSmallBrowser()==null) {
			return;
		}
		if(doc==null) {
			return;
		}
		if (this.messagesPaneOwner.isInsertable()) {
			if (!this.messagesPaneOwner.isThreadView()
					|| (this.messagesPaneOwner.isThreadView() && this.statement
							.getThreadId().equals(
									this.messagesPaneOwner.getCurrentThread()))) {
				if (logger.isDebugEnabled()) {
					logger.debug("-- start inserting new message in browser");
				}
				this.messagesPaneOwner.getSmallBrowser().insertNewMessageString(Statement.wrap(doc));
				if (this.messagesPaneOwner.getLastSelectedDoc() != null) {
					this.messagesPaneOwner.getSmallBrowser().highlightSelectedString(
							Statement.wrap(
									InsertStatementOperation.this.messagesPaneOwner
									.getLastSelectedDoc())
									.getMessageId());
				}
			}
		} else {
			if (this.emailStatement.getMessageId().equals(Statement.wrap(doc).getMessageId()) && this.emailStatement.isComment()) {
				final CommentStatement commentSt = CommentStatement
						.wrap(this.emailStatement.getBindedDocument());
				final String address = commentSt.getAddress();
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						String url = InsertStatementOperation.this.messagesPaneOwner
								.getSmallBrowser().getUrl();
						if (logger.isDebugEnabled()) {
							logger.debug("url : "+url);
							logger.debug("address : "+address);
						}
						if (url != null && url.equals(address)) {
							if (logger.isDebugEnabled()) {
								logger.debug("try highlight all commented places");
							}
							InsertStatementOperation.this.messagesPaneOwner
									.getSmallBrowser().highlightAllCommentedPlaces(
											commentSt.getCommentId());
						}
					}
				});

				CTabItem[] items = this.messagesPaneOwner.sF.tabbedPane.getItems();
				for (CTabItem item : items) {
					final SupraCTabItem supraItem = (SupraCTabItem) item;
					UiUtils.swtBeginInvoke(new Runnable() {
						public void run() {
							if (supraItem.getEmailPane() != null) {
								supraItem.getEmailPane().getControlPanel()
										.getContent().getDiscuss().setEnabled(
												supraItem.getEmailPane()
														.hasComment());
							} else if (supraItem.getBrowserPane() != null) {
								supraItem.getBrowserPane().getControlPanel()
										.getDiscuss().setEnabled(
												supraItem.getBrowserPane()
														.hasComment());
							}
						}
					});

				}

			}
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				InsertStatementOperation.this.messagesPaneOwner
				.getMessagesTable().scrollToTop();
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void updateSTable(final Document doc) {
		if(doc==null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				InsertStatementOperation.this.messagesPaneOwner
						.getMessagesTable().addStatement(
								Statement.wrap(doc));
			}
		});
	}

	private void insertIfResponseIdIsNull(final Document doc) {
		boolean second = false;
		boolean already = false;
		if (already == false || second == true) {
			this.messagesPaneOwner.getMessagesTree().insertMessage(doc, false, false, false);
		}
	}

	private boolean insertIfResponseIdNotNull(final Document doc) {
		// boolean didOnce = false;
		final boolean confirmed = getConfirmed();

		boolean found = this.messagesPaneOwner.getMessagesTree().insertMessage(doc, this.insertToSelectedOnly, this.openTreeToMessageId, false);

		//this.messagesPaneOwner.getMessagesTree().update();
		
		if (!found) {
			return false;
		}
		// for (Enumeration e = ((MessagesMutableTreeNode)
		// this.messagesPaneOwner
		// .getMainnode().getRoot()).preorderEnumeration(); e
		// .hasMoreElements();) {
		// MessagesMutableTreeNode temp = (MessagesMutableTreeNode) e
		// .nextElement();

		// if (!temp.isRoot()) {
		// String temp_message_id = temp.getMessageId();
		// if (temp_message_id.equals(this.getResponseId())) {
		// found = true;
		//
		// TreePath parentPath = new TreePath(this.messagesPaneOwner
		// .getTreeModel().getPathToRoot(temp));
		// if (this.insertToSelectedOnly == true && didOnce == false) {
		// didOnce = true;
		//
		// logger.warn("ONLY Insert into the selected one....");
		//
		// MessagesMutableTreeNode parentNode = null;
		// if (this.insertToSelectedOnly == true) {
		// parentNode = (MessagesMutableTreeNode) this.messagesPaneOwner
		// .getMessagesTree()
		// .getLastSelectedPathComponent();
		// }
		// MessagesMutableTreeNode newNode = new MessagesMutableTreeNode(
		// this.messagesPaneOwner, this.emailStatement
		// .getSubject(), this.emailStatement
		// .getMessageId(), null,
		// this.emailStatement.getType());
		// this.messagesPaneOwner.getTreeModel()
		// .insertNodeInto(newNode, parentNode,
		// parentNode.getChildCount());
		//
		// TreePath newPath = new TreePath(this.messagesPaneOwner
		// .getTreeModel().getPathToRoot(newNode));
		// this.messagesPaneOwner.getMessagesTree().getRowForPath(
		// newPath);
		//
		// this.messagesPaneOwner.getMessagesTree()
		// .setCellRenderer(
		// new MessagesTreeRenderer(
		// this.messagesPaneOwner));
		//
		// } else {
		// if (!this.insertToSelectedOnly) {
		// MessagesMutableTreeNode parentNode = (MessagesMutableTreeNode)
		// (parentPath
		// .getLastPathComponent());
		// MessagesMutableTreeNode newNode = new MessagesMutableTreeNode(
		// this.messagesPaneOwner, this.emailDocument
		// .getRootElement()
		// .element("subject").attributeValue(
		// "value"), this.emailDocument
		// .getRootElement().element(
		// "message_id")
		// .attributeValue("value"), null,
		// this.emailDocument.getRootElement().element("type")
		// .attributeValue("value"));
		// this.messagesPaneOwner.getTreeModel()
		// .insertNodeInto(newNode, parentNode,
		// parentNode.getChildCount());
		//							
		// TreePath newPath = new TreePath(
		// this.messagesPaneOwner.getTreeModel()
		// .getPathToRoot(newNode));
		// this.messagesPaneOwner.getMessagesTree()
		// .getRowForPath(newPath);
		//
		// this.messagesPaneOwner.getMessagesTree()
		// .setCellRenderer(
		// new MessagesTreeRenderer(
		// this.messagesPaneOwner));
		// if(Statement.wrap(this.emailDocument).isComment()) {
		// final CommentStatement commentSt =
		// CommentStatement.wrap(this.emailDocument);
		// final String address = commentSt.getAddress();
		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// String url =
		// InsertStatementOperation.this.messagesPaneOwner.getSmallBrowser().getUrl();
		// if(url != null && url.equals(address)) {
		// InsertStatementOperation.this.messagesPaneOwner.getSmallBrowser()
		// .highlightAllCommentedPlaces(commentSt.getCommentId());
		// }
		// }
		// });
		//								
		// CTabItem[] items = this.messagesPaneOwner.sF.tabbedPane.getItems();
		// for(CTabItem item : items) {
		// final SupraCTabItem supraItem = (SupraCTabItem)item;
		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// if(supraItem.getEmailPane()!=null) {
		// supraItem.getEmailPane().getControlPanel()
		// .getContent().getDiscuss().setEnabled(supraItem.getEmailPane().hasComment());
		// } else if(supraItem.getBrowserPane()!=null) {
		// supraItem.getBrowserPane().getControlPanel()
		// .getDiscuss().setEnabled(supraItem.getBrowserPane().hasComment());
		// }
		// }
		// });
		//									
		// }
		//								
		// }
		// }
		// }
		//
		// if (this.openTreeToMessageId) {
		// this.messagesPaneOwner.getMessagesTree().openSpecificNode(temp_message_id);
		//
		// }
		//
		if (this.type.equals("terse") || this.type.equals("comment")
				|| this.type.equals("message")) {
			if (this.messagesPaneOwner.isReplyToMine(this.emailDocument)) {

				this.messagesPaneOwner.setReplyNumber(this.messagesPaneOwner
						.getReplyNumber() + 1);

				Statement tempSt = this.messagesPaneOwner
						.getDocFromHash(this.statement.getResponseId());

				if (confirmed) {
					if (!this.author.equals((String) this.messagesPaneOwner
							.getRawSession().get("real_name"))) {

						if (this.messagesPaneOwner.sF.getTrayItem()
								.isBlinkPossible()) {
							if (this.messagesPaneOwner.sF.client
									.getPreferencesChecker()
									.isSystemTrayNotificationOfReply(
											this.sphereId)) {
								logger.error("NOMER 1");
								BalloonsController.INSTANCE.addBalloon(
										this.emailDocument, tempSt
												.getBindedDocument(),
										this.messagesPaneOwner);
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void processMessagesThatHasTitleSameAsSphereId() {
		if (this.type.equals("message") && this.title.startsWith(this.sphereId)) {

			this.messagesPaneOwner.setPreviewHtmlText(new PreviewHtmlTextCreator(this.messagesPaneOwner));
			this.messagesPaneOwner.getPreviewHtmlText().addText(
					this.emailRootElement.element("body").getText());
			if (this.messagesPaneOwner.getLastSelectedDoc() == null
					|| this.messagesPaneOwner.getLastSelectedDoc()
							.getRootElement().element("type").attributeValue(
									"value").equals("terse")) {
				this.messagesPaneOwner.showSmallBrowser(
						this.messagesPaneOwner.getRawSession(), true, null,
						this.messagesPaneOwner.getPreviewHtmlText().getText(),null, null);
			}
		}
	}

	
	public MessagesPane getMessagesPane() {
		return this.messagesPaneOwner;
	}
}
