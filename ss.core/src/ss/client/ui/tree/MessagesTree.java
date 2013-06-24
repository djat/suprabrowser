package ss.client.ui.tree;

/*

 A special kind of JTree that knows about asset types. Mostly, it includes the mouse listener
 that will load the appropriate message viewer depending on the type of message when a user
 double-clicks on any item in the tree.

 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.JComponent;

import org.dom4j.Document;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import ss.client.event.MessagesTreeMouseListenerSWT;
import ss.client.presence.ClientPresence;
import ss.client.ui.MessagesPane;
import ss.client.ui.tree.MessagesTreeFailuresContainer.MessagesTreeFailure;
import ss.common.StringUtils;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.UserSession;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;

public class MessagesTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2999648612890672591L;
	
	public static final String STATUS_ON = "on";
	
	public static final String STATUS_OFF = "off";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTree.class);

	
	private final MessagesTreeModel model;
	
	String orig_path = null;

	UserSession userSession = UserSession.NullSession;

	String path = null;

	MessagesPane messagesPane;

	MessagesTreeMouseListenerSWT messagesTreeMouseListener;

	private ChangingTree tree;
	
	private boolean isShaken = false;
	
	private MessagesTreeFailuresContainer failuresContainer = new MessagesTreeFailuresContainer();

	
	public MessagesTree(Composite parent, UserSession session, MessagesPane mP) {
		this.model = new MessagesTreeModel(mP);
		this.tree = new ChangingTree( parent, this.model );
		this.userSession = session;
		this.messagesPane = mP;
		if (logger.isDebugEnabled()) {
			logger.debug("tree creating");
		}
		this.messagesPane.setMessagesTree(this);
		this.tree.addTreeSelectionListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				afterMessageSelected();	
			}
		});
	}

	public void setMouseListener(
			MessagesTreeMouseListenerSWT messagesTreeMouseListener) {
		this.messagesTreeMouseListener = messagesTreeMouseListener;
		this.tree.addMouseListener(messagesTreeMouseListener);
	}

	public MessagesTreeMouseListenerSWT getListener() {
		return this.messagesTreeMouseListener;
	}

	private void afterMessageSelected() {
		Runnable runnable = new Runnable() {
			public void run() {
				ClientPresence clientPresence = MessagesTree.this.messagesPane.getClientPresence();
				if (clientPresence != null) {
					clientPresence.notifyMessageSelected();
				}
			}
		};
		ThreadUtils.start(runnable);
	}

	public UserSession getUserSession() {
		return this.userSession;
	}

	/**
	 * @return
	 */
	private MessagesMutableTreeNode getSelectedNode() {
		try {
			return (MessagesMutableTreeNode) this.tree.getSelectedElement().getData();
		} catch (NullPointerException ex) {
			logger.error("null selection", ex);
		}
		return null;
	}

	/**
	 * @return
	 */
	public List<Document> getSelectedSubtree() {
		try {
			return getSubtreeFor(getSelectedNode().getMessageId());
		} catch(Exception ex) {
			logger.error("no selection in tree", ex);
		}
		return new ArrayList<Document>();
	}

	/**
	 * @return
	 */
	public List<Document> getSelectedInvertedSubtree() {
		MessagesMutableTreeNode node = getSelectedNode();
		if(node == null) {
			return new ArrayList<Document>();
		}
		return getInvertedSubtreeForNode(node);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Document> getSelectedThreadDocsInverted() {
		List<Document> threadDocs = new ArrayList<Document>();
		try {
			MessagesMutableTreeNode node = getSelectedNode();
			MessagesMutableTreeNode rootOfThread = getRootOfThread(node);

			Enumeration enumer = rootOfThread.breadthFirstEnumeration();
			while (enumer.hasMoreElements()) {
				MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
				.nextElement();
				Statement tempSt = (Statement) tempNode.getUserObject();
				threadDocs.add(0, tempSt.getBindedDocument());
			}
		} catch (NullPointerException ex) {
			logger.error("null root message", ex);
		}
		return threadDocs;
	}

	private MessagesMutableTreeNode getRootOfThread(MessagesMutableTreeNode node) {
		if(node == null) {
			return null;
		}
		Statement statement = (Statement) node.getUserObject();
		return getNodeById(statement.getThreadId());
	}

	/**
	 * @return
	 */
	public List<Document> getInvertedSubtreeForDoc(Statement statement) {
		MessagesMutableTreeNode node = getNodeById(statement.getMessageId());
		if(node == null) {
			return new ArrayList<Document>();
		}
		return getInvertedSubtreeForNode(node);
	}

	@SuppressWarnings("unchecked")
	private List<Document> getInvertedSubtreeForNode(
			MessagesMutableTreeNode node) {
		List<Document> subtreeDocs = new ArrayList<Document>();

		if(node == null) {
			return subtreeDocs;
		}
		
		Enumeration subTree = node.breadthFirstEnumeration();
		while (subTree.hasMoreElements()) {
			MessagesMutableTreeNode nextNode = (MessagesMutableTreeNode) subTree
					.nextElement();
			Statement nextStatement = (Statement) (nextNode).getUserObject();
			subtreeDocs.add(0, nextStatement.getBindedDocument());
		}
		return subtreeDocs;
	}

	/**
	 * @param messageId
	 * @return
	 */
	MessagesMutableTreeNode getNodeById(String messageId) {
		return this.tree.getNodeById(messageId);
	}

	private MessagesMutableTreeNode getRoot() {
		return (MessagesMutableTreeNode) this.model.getRoot();
	}

	/**
	 * @return
	 */
	public Vector<String> getAllMessagesIdForSelectedThread() {
		return getAllMessagesIdInThreadForMessagesId( null );
	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> getAllMessagesIdInThreadForMessagesId( final String messageId ) {
		Vector<String> messageIds = new Vector<String>();

		try {
			final MessagesMutableTreeNode node = 
				StringUtils.isBlank(messageId) ? getSelectedNode() : getNodeById( messageId );
			final MessagesMutableTreeNode rootOfThread = getRootOfThread(node);

			Enumeration enumer = rootOfThread.breadthFirstEnumeration();
			while (enumer.hasMoreElements()) {
				MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
				.nextElement();
				Statement tempSt = (Statement) tempNode.getUserObject();
				if (!node.getMessageId().equals(tempSt.getMessageId())) {
					messageIds.add(tempSt.getMessageId());
				}
			}
		} catch (NullPointerException ex) {
			logger.error("null root message", ex);
		}
		return messageIds;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector getAllUniqueIdsForKeywordsInThread() {
		Vector<String> messageIds = new Vector<String>();

		try {
			MessagesMutableTreeNode node = getSelectedNode();
			MessagesMutableTreeNode rootOfThread = getRootOfThread(node);
			
			Enumeration enumer = rootOfThread.breadthFirstEnumeration();
			while (enumer.hasMoreElements()) {
				MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
				.nextElement();
				Statement tempSt = (Statement) tempNode.getUserObject();
				if (tempSt.getUniqueId() != null
						&& !node.getMessageId().equals(tempSt.getMessageId())) {
					messageIds.add(tempSt.getUniqueId());
				}
			}
		} catch (NullPointerException ex) {
			logger.error("null root message", ex);
		}
		return messageIds;
	}

	/**
	 * @param messageId
	 */
	public void selectMessage(String messageId) {
		MessagesMutableTreeNode temp = getNodeById(messageId);
		if(temp == null) {
			logger.warn("can't found required node with id: "+messageId);
			return;
		}
		this.tree.expandElement(temp);
		this.tree.setSelectionPath(temp);
		Statement statement = (Statement) temp.getUserObject();
		this.messagesPane.setLastSelectedDoc(statement.getBindedDocument());
	}

	public List<NodeDocumentsBundle> getSelectedDocumentsWithParents() {
		List<NodeDocumentsBundle> list = new ArrayList<NodeDocumentsBundle>();
		for (TreeItem treeItem : MessagesTree.this.tree.getSelectedElements()) {
			MessagesMutableTreeNode viewNode = (MessagesMutableTreeNode)treeItem.getData();

			MessagesMutableTreeNode parentNode = (MessagesMutableTreeNode) viewNode
			.getParent();
			Statement nodeStatement = (Statement) viewNode.getUserObject();
			Statement parentStatement = (parentNode == null || parentNode
					.getMessageId() == "") ? null : (Statement) parentNode
							.getUserObject();
			list.add(new NodeDocumentsBundle(nodeStatement, parentStatement));
		}
		return list;
	}

	/**
	 * 
	 */
	public Document getSelectedDoc() {
		return UiUtils.swtEvaluate(new Callable<Document>() {
			public Document call() throws Exception {
				final MessagesMutableTreeNode component = MessagesTree.this.tree
				.getLastSelectedPathComponent();
				if (component == null) {
					logger.warn("MessagesMutableTreeNode is null");

				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("MessagesMutableTreeNode is not null");
					}
				}
				return component != null ? component.returnDoc() : null;
			}
		});
	}

	/**
	 * @param viewDoc
	 * @return
	 */
	public Document getParentDocFor(Document viewDoc) {
		MessagesMutableTreeNode node = (MessagesMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		
		if(node == null) {
			return null;
		}
		
		MessagesMutableTreeNode parentNode = (MessagesMutableTreeNode) node
				.getParent();
		
		if (parentNode != null && parentNode.getUserObject() != null) {
			return ((Statement) parentNode.getUserObject()).getBindedDocument();
		}
		
		return null;
	}

	/**
	 * @return
	 */
	public int getViewNodeChildCount() {
		MessagesMutableTreeNode node = (MessagesMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		if(node == null) {
			return 0;
		}
		return node.getChildCount();
	}

	public int getChildCountForDoc(Statement statement) {
		try {
			return getNodeById(statement.getMessageId()).getChildCount();
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * @return
	 */
	public int getSelectedNodesCount() {
		try {
			final TreeItem[] elements = this.tree.getSelectedElements();
			if (elements == null){
				return 0;
			} else {
				return elements.length;
			}
		} catch (Exception ex) {
			logger.error("Exception getting number of selected nodes", ex);
			return 0;
		}
	}
	
	public boolean insertMessage(Document doc) {
		return insertMessage(doc, false, false, true);
	}
	
	public boolean insertMessage(Document doc, boolean insertToSelectedOnly, boolean openTree, boolean isInit) {
		boolean inserted = true;
		Statement statement = Statement.wrap(doc);
		if (statement.getResponseId() == null) {
			insertNodeIfResponseIdIsNull(statement,
					isInit);
		} else {
			inserted = insertNodeIfResponseIdNotNull(statement,
					insertToSelectedOnly, openTree);
		}
		return inserted;
	}

	public void insertNodeIfResponseIdIsNull(Statement statement,
			boolean isInitInsert) {
		if (logger.isDebugEnabled()) {
			logger.debug("is int insert:"+isInitInsert);
		}
		MessagesMutableTreeNode newNode = new MessagesMutableTreeNode(
				this.messagesPane, statement.getSubject(), statement
						.getMessageId(), null, statement.getType(), "off");
		try {
			getOwnModel().insertNodeInto(newNode, getRoot(), 0);
//			if (this.messagesPane.client.getVerifyAuth().getTreeOrder()
//					&& !isInitInsert) {
//				
//			} else {
//				getOwnModel().insertNodeInto(newNode, getRoot(),
//						getRoot().getChildCount());
//			}
			
			openSpecificNode(statement.getMessageId());
		} catch (NullPointerException npe) {
			logger.error("Insert node failed", npe);
		}
		//this.tree.redraw();
	}

	/**
	 * @param temp_message_id
	 */
	public void openSpecificNode(String messageId) {
		this.tree.scrollElementToVisible(getNodeById(messageId));
	}

	public boolean insertNodeIfResponseIdNotNull(final Statement statement,
			final boolean insertToSelectedOnly, final boolean openTreeToMessageId) {
		MessagesMutableTreeNode parentNode = getNodeById(statement
				.getResponseId());
		if (parentNode == null) {
			logger.error("parent with id : "+statement.getResponseId()+" node is null, subject: " + statement.getSubject());
			this.failuresContainer.put(statement, insertToSelectedOnly, openTreeToMessageId);
			return false;
		}

		MessagesMutableTreeNode newNode = null;

		int position = 0;//isInitInsert ? parentNode.getChildCount() : 0;
		
		if (insertToSelectedOnly && !statement.isKeywords()) {
			logger.error("ONLY Insert into the selected one....");
			parentNode = (MessagesMutableTreeNode) MessagesTree.this.tree
			.getLastSelectedPathComponent();
			if(parentNode != null) {
				newNode = new MessagesMutableTreeNode(
						MessagesTree.this.messagesPane, statement.getSubject(), statement
						.getMessageId(), null, statement.getType(), "off");
				getOwnModel().insertNodeInto(newNode, parentNode,
						position);
			}
		} else {
			newNode = new MessagesMutableTreeNode(
					MessagesTree.this.messagesPane, statement.getSubject(), statement
					.getMessageId(), null, statement.getType(), "off");
			getOwnModel().insertNodeInto(newNode, parentNode,
					position);
		}

		if (openTreeToMessageId) {
			openSpecificNode(statement.getMessageId());
		}
		List<MessagesTreeFailure> postponded = this.failuresContainer.get(statement.getMessageId());
		if (postponded != null){
			for (MessagesTreeFailure failure : postponded){
				insertNodeIfResponseIdNotNull(failure.getStatement(), failure.isInsertToSelectedOnly(), 
						failure.isOpenTreeToMessageId());
			}
		}
		return true;
	}

	public void openSpecificThread(final String messageId) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				MessagesMutableTreeNode selectedNode = (MessagesMutableTreeNode) MessagesTree.this.tree
				.getLastSelectedPathComponent();
				if(selectedNode == null) {
					return;
				}
				MessagesTree.this.tree.expandElement(selectedNode);
			}
		});
	}

	/**
	 * @param statement
	 */
	public boolean replaceExistedNode(Statement statement,
			boolean replacingKeyword) {
		if (logger.isDebugEnabled()) {
			logger.debug("----------- replacing keywords : "+replacingKeyword);
		}
		MessagesMutableTreeNode temp = getNodeById(statement.getMessageId());
		if (temp == null) {
			return false;
		}

		temp.setUserObject(statement.getDocumentCopy());
		if (replacingKeyword) {
			temp.setStatus(STATUS_ON);
		}
		update(temp);
		return true;
	}

	/**
	 * @param nodeStatement
	 */
	public void removeNodeFromParent(Statement nodeStatement) {
		getOwnModel().removeNodeFromParent(
				getNodeById(nodeStatement.getMessageId()));
		update();
	}

	/**
	 * 
	 */
	public void reload() {
		getOwnModel().setRoot(getRoot());
		getOwnModel().reload(getRoot());
	}

	/**
	 * @param messageId
	 */
	@SuppressWarnings("unchecked")
	public List<Document> getSubtreeFor(String messageId) {
		List<Document> subtreeDocs = new ArrayList<Document>();

		MessagesMutableTreeNode node = getNodeById(messageId);
		
		if(node == null) {
			return subtreeDocs;
		}
		
		Enumeration subTree = node.breadthFirstEnumeration();

		while (subTree.hasMoreElements()) {
			MessagesMutableTreeNode nextNode = (MessagesMutableTreeNode) subTree
					.nextElement();
			Statement nextStatement = (Statement) (nextNode).getUserObject();
			subtreeDocs.add(nextStatement.getBindedDocument());
		}
		return subtreeDocs;
	}

	/**
	 * @param messageId
	 * @return
	 */
	public List<Document> getChildrenFor( final String messageId ) {
		List<Document> subtreeDocs = new ArrayList<Document>();

		for (Statement st : getChildrenStatementsFor( messageId )) {
			subtreeDocs.add( st.getBindedDocument() );
		}

		return subtreeDocs;
	}
	
	@SuppressWarnings("unchecked")
	public List<Statement> getChildrenStatementsFor( final String messageId ) {
		List<Statement> subtreeStatements = new ArrayList<Statement>();

		MessagesMutableTreeNode node = getNodeById(messageId);

		if (node == null) {
			return subtreeStatements;
		}
		
		for (Enumeration enumer = node.children(); enumer.hasMoreElements();) {
			MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
					.nextElement();
			subtreeStatements.add( (Statement) tempNode.getUserObject() );
		}

		return subtreeStatements;
	}

	/**
	 * @param replaceDoc
	 * @param messageId
	 */
	public void replaceDoc(Document replaceDoc, String messageId) {
		MessagesMutableTreeNode node = getNodeById(messageId);

		if (node == null) {
			return;
		}

		node.replaceUserObject(replaceDoc);
		node.setTitle(Statement.wrap(replaceDoc).getSubject());

		getOwnModel().nodeStructureChanged(node);

		getOwnModel().reload();
		
		update(node);
	}

	/**
	 * @param node
	 */
	private void update(MessagesMutableTreeNode node) {
		this.tree.redraw(node);
	}

	/**
	 * @param responseId
	 * @return
	 */
	public Document getParentDocFromResponseId(String responseId) {
		MessagesMutableTreeNode node = getNodeById(responseId);
		if(node == null) {
			return null;
		}
		return ((Statement) node.getUserObject()).getBindedDocument();
	}

	/**
	 * @param messageId
	 */
	public void recallMessage(String messageId) {
		MessagesMutableTreeNode nodeToRemove = getNodeById(messageId);
		if(nodeToRemove==null) {
			return;
		}
		getOwnModel().removeNodeFromParent(nodeToRemove);
		update();
	}
	
	/**
	 * @param messageId
	 */
	public void hiddenRecallMessage(String messageId) {
		MessagesMutableTreeNode nodeToRemove = getNodeById(messageId);
		if(nodeToRemove==null) {
			return;
		}
		getOwnModel().removeNodeFromParent(nodeToRemove);		
	}

	public MessagesTreeModel getOwnModel() {
		return this.model;
	}

	public JComponent asComponent() {
		return this.tree.asComponent();	
	}

	/**
	 * 
	 */
	public void update() {
		this.tree.redraw();
	}

	/**
	 * @param messageId
	 */
	public void showMessage(String messageId) {
		this.tree.expandElement(getNodeById(messageId));
	}

	/**
	 * @param messageId
	 */
	public void resetNodeStatus(String messageId) {
		this.tree.resetNodeStatus(messageId);
	}

	/**
	 * @return
	 */
	public Vector<Document> getSelectedDocs() {
		return this.tree.getSelectedDocs();
	}

	/**
	 * 
	 */
	public void scrollToTop() {
		if(this.isShaken) {
			return;
		}
		this.tree.scrollToTop();
		this.isShaken = true;
	}

	/**
	 * @param email
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasComment(ExternalEmailStatement email) {
		MessagesMutableTreeNode node = getNodeById(email.getMessageId());
		Enumeration<MessagesMutableTreeNode> nodes = node.children();
		while(nodes.hasMoreElements()) {
			MessagesMutableTreeNode nextNode = nodes.nextElement();
			if(nextNode.getType().equals("comment")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public void removeAllNonRootKeywords() {
		List<MessagesMutableTreeNode> nodesToRemove = this.tree.collectAllNonRootKeywords();
		if( nodesToRemove == null ) {
			return;
		}
		for (MessagesMutableTreeNode nodeToRemove : nodesToRemove) {
			getOwnModel().removeNodeFromParent(nodeToRemove);
		}
		update();
	}
	
	public void updateAllInstancesOfKeywords( final KeywordStatement st ) {
		List<MessagesMutableTreeNode> nodes = this.tree.collectAllKeywordsInstances( st );
		if( (nodes == null) || (nodes.isEmpty())) {
			return;
		}
		for(MessagesMutableTreeNode node : nodes) {
			node.replaceUserObject(st.getBindedDocument());
		}
		update();
	}

	/**
	 * @param doc
	 * @return
	 */
	public Document getParentDocForKeyword( final Document doc ) {
		return this.tree.getNodeByDocument( doc ).returnDoc();
	}
}
