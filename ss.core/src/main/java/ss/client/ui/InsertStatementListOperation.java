package ss.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.event.tagging.TagManager;
import ss.client.ui.tree.AbstractMessageTreeOperation;
import ss.client.ui.tree.ThreadSystemMessagesController;
import ss.common.StringUtils;
import ss.common.operations.OperationBreakException;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.util.DateTimeParser;

public class InsertStatementListOperation extends AbstractMessageTreeOperation {

	public static final String INSERT_ALL = "insertAll";
	
	public static final String INSERT_ONE = "insertOne";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InsertStatementListOperation.class);

	private final String highligth;
	
	private final boolean insertAll;
	
	private final Document[] docsInOrder;
	
	private Hashtable all = null;

	/**
	 * @param pane
	 * @param order
	 * @param all
	 * @param allOrOne
	 */
	@SuppressWarnings("unchecked")
	public InsertStatementListOperation(MessagesPane pane, Hashtable allMessages,
			boolean insertAll, String highligth) {
		super(pane);
		this.all = allMessages;
		this.docsInOrder = (Document[])this.all.get("docs_in_order");
		this.insertAll = insertAll;
		this.highligth = highligth;
		if (logger.isDebugEnabled()) {
			logger.debug("docs in order size:"+docsInOrder.length);
		}
	}
	
	@SuppressWarnings("unchecked")
	public InsertStatementListOperation(MessagesPane pane, Document[] docsInOrder,
			boolean insertAll, String highligth) {
		super(pane);
		this.docsInOrder = docsInOrder;
		this.insertAll = insertAll;
		this.highligth = highligth;
		if (logger.isDebugEnabled()) {
			logger.debug("docs in order size:"+docsInOrder.length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.operations.AbstractOperation#performRun()
	 */
	@Override
	protected void performRun() throws OperationBreakException {
		boolean doTable = false;
		List<Document> toRemove = new ArrayList<Document>(); 
		String memberLogin = SupraSphereFrame.INSTANCE.client.getLogin();
		
		this.messagesPaneOwner.setSystemMessagesController(new ThreadSystemMessagesController());
		
		for (int i = 0; i< this.docsInOrder.length; i++) {
			Statement st = Statement.wrap(this.docsInOrder[i]);
			if (st.isSphere()){
				String sphereId = SphereStatement.wrap(st.getBindedDocument()).getSystemName();
				if (SupraSphereFrame.INSTANCE.client.getVerifyAuth().isSphereEnabledForMember(sphereId, memberLogin)){
					toRemove.add(this.docsInOrder[i]);
				}
			} else {
				toRemove.add(this.docsInOrder[i]);
			}
		}
		for(Document doc : toRemove) {
			Statement statement = Statement.wrap(doc);
			String responseId = statement.getResponseId();
			if(statement.isSystemMessageToHide()) {
				insertSystemMessage(statement);
			} else if(responseId!=null && !responseId.trim().equals("") && 
					!this.messagesPaneOwner.getAllMessages().containsKey(responseId)) {
				getEntireThread(statement.getThreadId());
			} else {
				simpleInsertInTree(statement);
			}
			doTable = true;
		}

		if (!this.insertAll) {
			doTable = false;
		}

		if (doTable) {
			doTableInsert();			
		}

		refreshMessagesTree();
	}

	/**
	 * @param threadId
	 */
	
	@SuppressWarnings("unchecked")
	private void getEntireThread(final String threadId) {
		if (StringUtils.isBlank(threadId)) {
			logger.error("ThreadId is blank");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("insert entire thread:" + threadId);
		}
		try {
			final Vector docs = this.messagesPaneOwner.client.getEntireThread(
					this.messagesPaneOwner.getRawSession(), threadId);
			for (Object o : docs) {
				Statement statement = Statement.wrap((Document) o);
				simpleInsertInTree(statement);
			}
		} catch (Throwable ex) {
			logger.error("Error recieving entire thread", ex);
		}
	}

	/**
	 * @param statement
	 */
	private void insertSystemMessage(Statement statement) {
		this.messagesPaneOwner.getSystemMessages().add(statement);
	}

	private void simpleInsertInTree(Statement statement) {
		if(this.messagesPaneOwner.getAllMessages().containsKey(statement.getMessageId())) {
			return;
		}
		this.messagesPaneOwner.addToAllMessages(statement.getMessageId(), statement);
		this.messagesPaneOwner.getMessagesTree().insertMessage(statement.getBindedDocument());
	}

	private void doTableInsert() {
		List<Statement> statements = new ArrayList<Statement>();
		Collection<Statement> statementCollection = this.messagesPaneOwner.getAllMessages().values();
		
		statements.addAll(statementCollection);
		
		Collections.sort(statements, new Comparator<Statement>() {
			public int compare(Statement o1, Statement o2) {
				long firstMoment = DateTimeParser.INSTANCE.parseToDate(o1.getMoment()).getTime();
				long secondMoment = DateTimeParser.INSTANCE.parseToDate(o2.getMoment()).getTime();
				if(secondMoment>firstMoment) {
					return 1;
				}
				return -1;
			}
		});
		synchronized (this.messagesPaneOwner.getTableStatements()) {
			this.messagesPaneOwner.setTableStatements(statements);
			this.messagesPaneOwner.getMessagesTable().setInput(this.messagesPaneOwner.getTableStatements());
			this.messagesPaneOwner.getMessagesTable().refresh();
		}
	}

	private void refreshMessagesTree() {
		
		if (this.highligth != null) {
			this.messagesPaneOwner.setCashedSelected(this.highligth);
		}
		this.messagesPaneOwner.getMessagesTree().update();
		
		if(this.messagesPaneOwner.getCashedId()==null) {
			this.messagesPaneOwner.loadWindow(null);
		}
	}

}
