/**
 * 
 */
package ss.client.event;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import ss.client.event.executors.StatementExecutor;
import ss.client.event.executors.StatementExecutorFactory;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.viewers.NewWeblink;
import ss.client.ui.viewers.ViewMessageSWT;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.global.SSLogger;
import ss.util.SupraXMLConstants;

/**
 * @author roman
 *
 */
public class TableSWTMouseListener implements MouseListener {

	private MessagesPane mp;

	private Hashtable session = new Hashtable();
	
	private static Logger logger = SSLogger.getLogger(TableSWTMouseListener.class);
	
	@SuppressWarnings("unused")
	private static final String COMMENT = "TABLEMOUSELISTENER.COMMENT";
	
	@SuppressWarnings("unused")
	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_EVENT_TABLEMOUSELISTENER);
	
	public TableSWTMouseListener(MessagesPane mp) {
		this.session = mp.getRawSession();
		this.mp = mp;
	}
	
	public void mouseDoubleClick(MouseEvent arg0) {
		Statement statement = this.mp.getMessagesTable().getSelectedElement();

		String type = statement.getType();

		if (type.equals("bookmark") || type.equals("rss")) {

			NewWeblink nw = new NewWeblink(
					TableSWTMouseListener.this.session,
					TableSWTMouseListener.this.mp, "normal", null);
			logger.info("CREATING WEBLINK: "
					+ (String) TableSWTMouseListener.this.session
							.get("sphere_id"));
			nw.fillDoc(statement.getBindedDocument());

		}

		else if (type.equals("message")
				|| (type
						.equals(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL))
				|| type.equals("contact") || type.equals("audio")
				|| type.equals("vote") || type.equals("bookmark")
				|| type.equals("file") || type.equals("rss")
				|| type.equals("sphere") || type.equals("terse")) {
			TableSWTMouseListener.this.mp
					.setPreviewHtmlText(new PreviewHtmlTextCreator(
							TableSWTMouseListener.this.mp));
			TableSWTMouseListener.this.mp.getPreviewHtmlText().addText(
					statement.getBody());

			this.mp.showSmallBrowserNoFocusSteal(this.session, true, null,
					this.mp.getPreviewHtmlText().getText(), null, null);
			this.mp.getSmallBrowser().scrollToTop();

			new ViewMessageSWT(this.session, statement.getBindedDocument(), this.mp);

		} else if (type.equals("sphere")) {

		} else if (type.equals("reply")) {
			TableSWTMouseListener.this.mp
					.setPreviewHtmlText(new PreviewHtmlTextCreator(
							TableSWTMouseListener.this.mp));
			TableSWTMouseListener.this.mp.getPreviewHtmlText().addText(
					statement.getBody());

			TableSWTMouseListener.this.mp.showSmallBrowserNoFocusSteal(
					TableSWTMouseListener.this.session, true, null, this.mp
							.getPreviewHtmlText().getText(), null, null);
			
			this.mp.getSmallBrowser().scrollToTop();

		} else if (type.equals("edit")) {
//			int shift = 0;
//			for (int j = 0; j < markups.size(); j++) {
//
//				Element change = (Element) markups.get(j);
//
//				Integer position = new Integer(change
//						.attributeValue("position"));
//				Integer position2 = new Integer(viewDoc.element(
//						"body").element("selected_body")
//						.attributeValue("selection_start"));
//
//				if ((change.getName()).equals("red")) {
//
//					try {
//
//						TableSWTMouseListener.this.commentText
//								.getDocument()
//								.remove(
//										(position.intValue()
//												- position2
//														.intValue() + shift),
//										((change.getText())
//												.length()));
//
//						StyleContext sc2 = new StyleContext();
//
//						Style style2 = sc2.addStyle(null, null);
//
//						StyleConstants.setForeground(style2,
//								Color.red);
//						StyleConstants.setBackground(style2,
//								new Color(255, 230, 230));
//
//						TableSWTMouseListener.this.commentText
//								.getDocument()
//								.insertString(
//										(position.intValue()
//												- position2
//														.intValue() + shift),
//										change.getText(), style2);
//
//					} catch (BadLocationException ble) {
//					}
//
//				} else if ((change.getName()).equals("green")) {
//
//					try {
//						StyleContext sc3 = new StyleContext();
//
//						Style style3 = sc3.addStyle(null, null);
//						Color d_green = new Color(0, 128, 19);
//						StyleConstants.setForeground(style3,
//								d_green);
//						StyleConstants.setBackground(style3,
//								new Color(255, 230, 230));
//
//						TableSWTMouseListener.this.commentText
//								.getDocument()
//								.insertString(
//										(position.intValue() - position2
//												.intValue()),
//										change.getText(), style3);
//
//					} catch (BadLocationException ble) {
//					}
//
//				}
//			}
//
//			if (com_elm.getText() != null) {
//
//				StyleContext sc3 = new StyleContext();
//
//				Style style3 = sc3.addStyle(null, null);
//
//				StyleConstants.setForeground(style3, Color.blue);
//
//				try {
//					TableSWTMouseListener.this.commentText
//							.getDocument()
//							.insertString(
//									TableSWTMouseListener.this.commentText
//											.getDocument()
//											.getLength(),
//									"\n\n-----Comment Below-----\n",
//									style3);
//
//					sc3 = new StyleContext();
//
//					style3 = sc3.addStyle(null, null);
//
//					StyleConstants.setForeground(style3,
//							Color.black);
//
//					TableSWTMouseListener.this.commentText
//							.getDocument()
//							.insertString(
//									TableSWTMouseListener.this.commentText
//											.getDocument()
//											.getLength(),
//									com_elm.getText(), style3);
//				} catch (BadLocationException ble) {
//				}
//
//			}

		} else if (type.equals("comment")
				|| type.endsWith("transcript")) {

//			Integer start = new Integer(viewDoc.element("body")
//					.element("selected_body").attributeValue(
//							"selection_start"));
//			Integer finish = new Integer(viewDoc.element("body")
//					.element("selected_body").attributeValue(
//							"selection_end"));
//			StyleContext sc4 = new StyleContext();
//			Style style4 = sc4.addStyle(null, null);
//			StyleConstants.setBackground(style4, new Color(182,
//					218, 222));
//			StyleConstants.setBackground(style4, new Color(200,
//					226, 230));
//
//			TableSWTMouseListener.this.mp.getPreview().setText(
//					viewDoc.element("body").element("orig_body")
//							.getText());

//			TableSWTMouseListener.this.mp
//					.setPreviewHtmlText(new PreviewHtmlTextCreator(
//							TableSWTMouseListener.this.mp));
//			TableSWTMouseListener.this.mp.getPreviewHtmlText()
//					.addText(statement.getOrigBody());
//
//			TableSWTMouseListener.this.mp
//					.showSmallBrowserNoFocusSteal(
//							TableSWTMouseListener.this.session,
//							true,
//							null,
//							TableSWTMouseListener.this.mp
//									.getPreviewHtmlText().getText(),
//							null, null);

//			DefaultStyledDocument bodyDoc = (DefaultStyledDocument) TableSWTMouseListener.this.mp
//					.getPreview().getStyledDocument();
//			bodyDoc.setCharacterAttributes(start.intValue(),
//					(finish.intValue() - start.intValue()), style4,
//					false);
//
//			TableSWTMouseListener.this.mp.getPreview().setDocument(
//					bodyDoc);
//
//			TableSWTMouseListener.this.mp.getPreview()
//					.setCaretPosition(start.intValue());
//
//			Element com_elm = viewDoc.element("body").element(
//					"comment");
//			Element tran_elm = viewDoc.element("body").element(
//					"transcript");
//
//			if (com_elm != null || tran_elm != null) {
//				JPanel comment = new JPanel();
//				this.commentText = new JTextPane();
//
//				this.commentText
//						.addMouseMotionListener(new CommentTextMouseMotionListener());
//
//				this.commentText
//						.addMouseListener(new CommentTextMouseAdapter());
//
//				TableSWTMouseListener.this.commentText.setText(com_elm
//						.getText());
//				TableSWTMouseListener.this.commentText
//						.setCaretPosition(0);
//
//				GridBagLayout cgbl = new GridBagLayout();
//
//				GridBagConstraints cgbc = new GridBagConstraints();
//
//				comment.setLayout(cgbl);
//
//				cgbc.gridx = 0;
//				cgbc.gridy = 0;
//				cgbc.anchor = GridBagConstraints.NORTH;
//				cgbc.fill = GridBagConstraints.BOTH;
//				cgbc.weightx = 1.0;
//				cgbc.weighty = 1.0;
//				JScrollPane c_scroll = new JScrollPane(
//						TableSWTMouseListener.this.commentText);
//
//				cgbl.setConstraints(c_scroll, cgbc);
//
//			}
		} else {
			new ViewMessageSWT(TableSWTMouseListener.this.session, statement
					.getBindedDocument(), TableSWTMouseListener.this.mp);
		}

		if (type.equals("file")) {

			NewBinarySWT nb = new NewBinarySWT(this.session, this.mp,
					null, false);
			FileStatement file = FileStatement.wrap(statement.getBindedDocument());
			nb.fillDoc(file);
			nb.addFillButtons();
		}
	}

	
	public void mouseDown(final MouseEvent me) {
		if (me.button != 3	&& me.stateMask!=SWT.CTRL) {
			final Statement statement = this.mp.getMessagesTable().getSelectedElement();
			Thread mainThread = new Thread() {
				public void run() {
					mousePressedBody(statement);
				}
			};
			mainThread.start();
		}
	}
	
	public void mouseUp(MouseEvent me) {
	}
	
	private void mousePressedBody(final Statement statement) {
		WorkflowConfiguration configuration = DeliveryFactory.INSTANCE.getWorkflowConfiguration(this.mp.getSystemName());
		if (PopUpController.INSTANCE.shouldPopupMessage(statement, configuration, this.mp)){
			PopUpController.INSTANCE.popupForced(statement.getBindedDocument());
		} else {
			StatementExecutor executor = StatementExecutorFactory.createExecutor(this.mp, statement);
			executor.performExecute(true, false);
		}
	}

//	private void executeStatementAction(final Statement statement) {
//		if (statement.isRss()) {
//			final String link = statement.getAddress();
//			Thread t = new Thread() {
//				public void run() {
//					String content = XSLTransform.transformRSS(link);
//					getMessagesPane().showSmallBrowserNoFocusSteal(
//							TableSWTMouseListener.this.session, true, link,
//							content, null, null);
//				}
//			};
//			t.start();
//
//		} else if (statement.isComment()) {
//			performCommentAction(statement);
//			
//		} else if (statement.isContact()) {
//			final AtomicReference<Statement> item = new AtomicReference<Statement>();
//			
//			item.set(statement);
//			Thread t = new Thread() {
//				public void run() {
//					logger.warn("Before: "+item.get().getDocumentCopy().asXML());
//					String content = XSLTransform.transformContact(item.get().getDocumentCopy());
//					
//				logger.warn("CONTENT: "+content);
//				getMessagesPane().showSmallBrowser(
//						getMessagesPane().getSession(), true, "contact",
//						content, item.get(), null);
//
//				}
//			};
//			t.start();
//			
//
//		} else if (statement.isMessage()
//				|| statement.isAudio() || statement.isMembership()
//				|| statement.isPersona()
//				|| statement.isTool() || statement.isFile()
//				|| statement.isReply() || statement.isSystemMessage()) {
//
//			Element webHighlight = statement.getBindedDocument().getRootElement().element(
//					"web_highlight");
//			if (webHighlight != null) {
//
//				Document rootDoc = getMessagesPane().getRootOfThread(statement.getBindedDocument());
//				String address = rootDoc.getRootElement()
//						.element("address").attributeValue("value");
//
//				getMessagesPane().showSmallBrowserNoFocusSteal(
//						TableSWTMouseListener.this.session, true, address,
//						null, null, null);
//
//			} else {
//				getMessagesPane().showMessagesBrowser(statement.getBindedDocument());
//			}
//
//		} else if(statement.isSphere()) {
//			String content = SphereDocTransform.getString(statement.getBindedDocument());
//			getMessagesPane().showSmallBrowserNoFocusSteal(
//					getMessagesPane().session, true, null, content, statement, null);
//		} else if (statement.isBookmark()) {
//			String address = statement.getAddress();
//			getMessagesPane().showSmallBrowserNoFocusSteal(
//					TableSWTMouseListener.this.session, true, address, null,
//					statement, null);
//
//		} else if(statement.isResult()) {
//			ResultStatement result = ResultStatement.wrap(statement.getBindedDocument());
//			String content = result.getHtmlText();
//			getMessagesPane().showSmallBrowserNoFocusSteal(
//					TableSWTMouseListener.this.session, true, null, content,
//					statement, null);
//		} else if (statement.isKeywords()) {
//
//			this.ata.processKeywordSelected(getMessagesPane(), statement.getBindedDocument());
//			getMessagesPane().showSmallBrowserNoFocusSteal(
//					TableSWTMouseListener.this.session, true, null,
//					getMessagesPane().getPreviewHtmlText().getText(), null,
//					null);
//		} else if (statement.isTerse()) {
//			if (!getMessagesPane().isInsertable()) {
//
//				getMessagesPane().loadWindow(statement);
//			} else {
//				getMessagesPane().getSmallBrowser()
//						.highlightSelectedString(statement.getMessageId());
//				getMessagesPane().getSmallBrowser()
//						.scrollToSelectedElement(statement.getMessageId());
//			}
//		}
//	}
	
	public MessagesPane getMessagesPane() {
		return this.mp;
	}
	
//	private void performCommentAction(Statement statement) {
//		this.mp.setLastSelectedDoc(statement.getBindedDocument());
//		final AtomicReference<Statement> item = new AtomicReference<Statement>();
//		final CommentStatement viewStatement = CommentStatement.wrap(statement
//				.getBindedDocument());
//		this.mp.setNeedOpenComment(true);
//		for (Enumeration enumer = ((MessagesMutableTreeNode) this.mp
//				.getMainnode().getRoot()).preorderEnumeration(); enumer
//				.hasMoreElements();) {
//			MessagesMutableTreeNode temp = (MessagesMutableTreeNode) enumer
//					.nextElement();
//			if (temp.getMessageId().equals(viewStatement.getCommentId()))
//				item.set(Statement.wrap(temp.returnDoc()));
//		}
//
//		this.mp.setViewComment(viewStatement);
//
//		Display.getDefault().asyncExec(new Runnable() {
//			TableSWTMouseListener listener = TableSWTMouseListener.this;
//
//			public void run() {
//				if (viewStatement.getCommentThread().equals("bookmark")) {
//					if (this.listener.mp.getSmallBrowser().getUrl() != null
//							&& !this.listener.mp.getSmallBrowser().getUrl()
//									.equals(viewStatement.getAddress())) {
//						this.listener.mp.showSmallBrowserNoFocusSteal(
//								this.listener.mp.session, true, viewStatement
//										.getAddress(), null, null, null);
//					} else if (this.listener.mp.getSmallBrowser().getUrl() == null) {
//						this.listener.mp.showSmallBrowserNoFocusSteal(
//								this.listener.mp.session, true, viewStatement
//										.getAddress(), null, null, null);
//					} else {
//						this.listener.mp.showCommentWindow();
//					}
//				} else {
//					executeStatementAction(item.get());
//				}
//			}
//		});
//	}

}
		
	
