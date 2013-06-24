package ss.client.ui;

import java.util.ArrayList;
import java.util.List;

import ss.domainmodel.Statement;

public class ConversationManager {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConversationManager.class);
	
	private MessagesPane mp;
	private List<Statement> statements;
	private Statement statement;
	
	@SuppressWarnings("unchecked")
	public ConversationManager(MessagesPane mp, Statement statement) {
		this.mp = mp;
		this.statements = new ArrayList<Statement>();
		this.statement = statement;
		
		String root_id = this.statement.getThreadId();
		
		for(Statement st : this.mp.getTableStatements()) {
			if(st.getThreadId().equals(root_id)) {
				this.statements.add(0, st);
			}
		}
	}
	
	public List<Statement> getConversationDocs() {
		if(this.statements.size()>0)
				return this.statements;
		return null;
	}
	
	public MessagesPane getMessagesPane() {
		return this.mp;
	}
	
	public Statement getRootStatement() {
		return this.statement;
	}
	
	public void showConversation() {
		this.mp.setPreviewHtmlText(new PreviewHtmlTextCreator(this.mp));
		for(Statement st : this.statements) {
			if (logger.isDebugEnabled()) {
				logger.debug(" moment : "+st.getMoment());
			}
			this.mp.getPreviewHtmlText().addDocText(st.getBindedDocument());
		}
		this.mp.showSmallBrowser(this.mp.getRawSession(), true, null, this.mp.getPreviewHtmlText().getText(), this.statement, null);
		this.mp.setInsertable(true);
	}
	
}
