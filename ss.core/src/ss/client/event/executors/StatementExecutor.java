/**
 * 
 */
package ss.client.event.executors;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.common.UiUtils;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;

/**
 * @author roman
 *
 */
public abstract class StatementExecutor {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StatementExecutor.class);
	
	protected MessagesPane mp;
	
	protected Statement statement;
	
	private boolean isClickInTree = true;
	
	public StatementExecutor(MessagesPane mp, Statement statement) {
		this.mp = mp;
		this.statement = statement;
	}
	
	public void performExecute( final boolean displayInPreview, final boolean isClickInTree ) {
		this.mp.setCurrentStatement(this.statement);
		
		this.isClickInTree = isClickInTree;
		
		if (isRepeat() && !this.statement.isComment() && !this.mp.isInsertable()) {
			// Still try to update presence renderer
			this.mp.updatePresenceRenderer(this.statement.getBindedDocument());
			return;
		}
		
		if(displayInPreview) {
			SupraSphereFrame.INSTANCE.getCommentWindowController().disposeCommentWindow();
			
			browserExecute();
		}
		
		voteDoc();
		
		this.mp.includeSystemMessageButton();
		
		this.mp.setCurrentThread(this.statement.getThreadId());
		
		if(displayInPreview) {
			this.mp.reorganizePreviewButtons(this.statement);
		}
		
		this.mp.setInsertable(this.statement != null
				&& (this.statement.isTerse() || !displayInPreview));
		this.mp.updatePresenceRenderer(this.statement.getBindedDocument());
		
		this.mp.setLastSelected(this.statement.getBindedDocument());
		
		selectMessageInMessagesPane();
	}

	public MessagesPane getMP() {
		return this.mp;
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable getSession() {
		return this.mp.getRawSession();
	}
	
	public SupraBrowser getBrowser() {
		logger.debug("call browser");
		return this.mp.getSmallBrowser();
	}
	
	public void selectMessageInMessagesPane() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if(getMP().isUiCreated()) {
					getMP().selectItemInTable(StatementExecutor.this.statement.getMessageId());
					tryHighlightInsideBrowser();
					if(!StatementExecutor.this.isClickInTree) {
						getMP().getMessagesTree().selectMessage(StatementExecutor.this.statement.getMessageId());
					}
				}
			}
		});
	}
	
	private void tryHighlightInsideBrowser() {
		Document lastDoc = getMP().getLastSelectedDoc();
		if(lastDoc==null) {
			return;
		}
		
		//Statement lastSt = Statement.wrap(lastDoc);
		
		//if(!lastSt.isComment() && !this.statement.isComment()) {
			getMP().highlightInsideBrowser(StatementExecutor.this.statement.getMessageId());
		//}
	}
	
	protected boolean doWebHighlight() {
		Element webHighlight = this.statement.getBindedDocument().getRootElement().element("web_highlight");
		if(webHighlight==null) {
			return false;
		}
			
		String address = this.statement.getAddress();
		if (address != null) {
			this.mp.showSmallBrowser(getSession(), true, address,
					null, null, this.statement.getBindedDocument());
		} else {
			this.mp.setPreviewHtmlText(new PreviewHtmlTextCreator(
					this.mp));
			this.mp.getPreviewHtmlText().addText(this.statement.getBody());
			this.mp.showSmallBrowser(getSession(), true, null,
					this.mp.getPreviewHtmlText().getText(), null,
					this.statement.getBindedDocument());
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void voteDoc() {
		if (getMP() == null) {
			return;
		}
		
		if (getMP().getSphereDefinition() != null) {
			Element inherit = getMP().getSphereDefinition()
					.getRootElement().element("inherit");
			if (inherit == null) {
				getSession().put(
						"sphere_id", getMP().getSystemName());
			}
		}

		try {
			if (!this.mp.hasVoted((String) getSession().get("real_name"),
					this.statement.getBindedDocument())) {
				final WorkflowConfiguration configuration = DeliveryFactory.INSTANCE.getWorkflowConfiguration(this.mp.getSystemName());
				if (!(PopUpController.INSTANCE.shouldPopupMessage(this.statement, configuration, this.mp))){
					this.mp.client.voteDocument(getSession(), this.statement
							.getMessageId(), this.statement.getBindedDocument());
				}
			}
		} catch (ArrayIndexOutOfBoundsException aioe) {
		}
	}
	
	public boolean isRepeat() {
		Statement lastDoc = null;
		
		if (getMP().getLastSelectedDoc() != null) {
			lastDoc = Statement.wrap(getMP().getLastSelectedDoc());
		}

		boolean isFromBalloon = false;

		if (this.mp.getLastSelectedDoc() != null) {
			if (this.mp.getLastSelectedDoc().getRootElement().element(
			"from_balloon") != null) {
				logger.warn("its from balloon");
				isFromBalloon = true;
			}
		}
		
		return (lastDoc != null && lastDoc.getMessageId().equals(this.statement.getMessageId())) || isFromBalloon;
	}
	
	abstract protected void browserExecute();
}
