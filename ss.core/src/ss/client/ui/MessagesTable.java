/**
 * 
 */
package ss.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.dom4j.Document;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ss.client.localization.LocalizationLinks;
import ss.common.UiUtils;
import ss.common.operations.IMessagesTable;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;

/**
 * @author roman
 *
 */
public class MessagesTable implements IMessagesTable {
	
	private static final ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_DOCKING_SUPRATABLEDOCKING);
	
	public static final String MESSAGE = "SUPRATABLEDOCKING.MESSAGE";
	public static final String SENDER = "SUPRATABLEDOCKING.SENDER";
	public static final String MOMENT = "SUPRATABLEDOCKING.MOMENT";
	
	private final static String[] PROPS = {bundle.getString(MESSAGE), bundle.getString(SENDER), bundle.getString(MOMENT)};
	
	private TableViewer tv;
	
	private MessagesPane mp;
	 
    @SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTable.class);
    
	public MessagesTable() {
		
	}
	
	public MessagesTable(Composite parent, MessagesPane mp) {
		this.tv = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION );
		
		logger.warn("viewer is not null");
		
		setMP(mp);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;

		Table table = this.tv.getTable();
		table.setLayoutData(data);

		for(String title : PROPS) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(title);
		}
		table.getColumn(0).setWidth(330);
		table.getColumn(1).setWidth(150);
		table.getColumn(2).setWidth(150);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		this.tv.setContentProvider(new TableContentProvider());
		this.tv.setLabelProvider(new TableLabelProvider(this.mp));


		
		synchronized (this.mp.getTableStatements()) {
			if(this.mp.getTableStatements()!=null) {
				logger.warn("have "+this.mp.getTableStatements().size()+" messages");
				this.mp.getTableDocking().setInfoToLabel();
				logger.warn("try to set input");
				setInput(this.mp.getTableStatements());
			}
		}			
	}

	private void setMP(MessagesPane mp) {
		this.mp = mp;
		this.mp.setMessagesTable(this);
	}

	
	@SuppressWarnings("unchecked")
	public void addStatement(Statement statement) {
		if(this.tv == null) {
			return;
		}
		
		List<Statement> input = null;
		input = (List<Statement>)this.tv.getInput();
		if(input == null) {
			input = new ArrayList<Statement>();
			this.mp.setTableStatements(input);
			this.tv.setInput(input);
		}
		input.add(0, statement);
		update();
	}

	
	public void removeStatement(Statement statement) {
		removeStatement(statement.getMessageId());
	}
	
	public void removeStatement(String id) {
		if(this.tv == null) {
			return;
		}
		List input = (List)this.tv.getInput();
		for(Object o : input) {
			Statement tempStatement = (Statement)o;
			if(tempStatement.getMessageId().equals(id)) {
				input.remove(o);
				break;
			}
		}
		update();
	}
	
	public void removeAllNonRootKeywords() {
		if(this.tv == null) {
			return;
		}
		List input = (List)this.tv.getInput();
		//Set<String> responseIds = new HashSet<String>();
		List toRemove = new ArrayList();
		for(Object o : input) {
			Statement tempStatement = (Statement)o;
			if(tempStatement.isKeywords() && (tempStatement.getResponseId() != null)) {
				toRemove.add(o);
				//responseIds.add(tempStatement.getResponseId());
				//responseIds.add(tempStatement.getMessageId());
			}
		}
//		for(Object o : input) {
//			Statement tempStatement = (Statement)o;
//			if(!tempStatement.isKeywords() && responseIds.contains(tempStatement.getResponseId())) {
//				toRemove.add(o);
//			}
//		}
		input.removeAll(toRemove);
		update();
	}
	
	public void updateKeyword( final KeywordStatement st ){
		final Document doc = (Document) st.getBindedDocument().clone();
		if(this.tv == null) {
			return;
		}
		List input = (List)this.tv.getInput();
		for(Object o : input) {
			Statement tempStatement = (Statement)o;
			if (tempStatement.getMessageId().equals(st.getMessageId())) {
				int i = input.indexOf(tempStatement);
				KeywordStatement statement = KeywordStatement.wrap((Document)doc.clone());
				if ( tempStatement.getResponseId() != null ) {
					statement.setResponseId(  tempStatement.getResponseId() );
				}
				input.set(i, statement);
			}
		}
		update();
	}
	
	public void hiddenRemoveStatement(String id) {
		if(this.tv == null) {
			return;
		}
		List input = (List)this.tv.getInput();
		for(Object o : input) {
			Statement tempStatement = (Statement)o;
			if(tempStatement.getMessageId().equals(id)) {
				input.remove(o);
				break;
			}
		}		
	}

	
	@SuppressWarnings("unchecked")
	public void replaceStatement(Statement statement) {
		if(this.tv == null) {
			return;
		}
		List input = (List)this.tv.getInput();
		for(Object o : input) {
			Statement tempStatement = (Statement)o;
			if(tempStatement.getMessageId().equals(statement.getMessageId())) {
				int i = input.indexOf(tempStatement);
				input.set(i, statement);
				break;
			}
		}
		update();
	}
	
	public void update() {
		if (MessagesTable.this.tv == null) {
			return;
		}
	
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				MessagesTable.this.tv.refresh();
				selectCurrentElement();
				MessagesTable.this.mp.getTableDocking().setInfoToLabel();
			}
		});
	}
	
	
	private void selectCurrentElement() {
		if(this.mp.getLastSelectedDoc() != null) {
			String messageId = Statement.wrap(this.mp.getLastSelectedDoc()).getMessageId();
			List input = (List)this.tv.getInput();
			for(Object o : input) {
				Statement st = (Statement)o;
				if(st.getMessageId().equals(messageId)) {
					int index = input.indexOf(o);
					this.tv.getTable().setSelection(index);
					break;
				}
			}
		}
	}
	
	
	public void findMessagesToPopup() {
		if(this.tv == null ) {
			return;
		}
		List input = (List)this.tv.getInput();
		WorkflowConfiguration configuration = DeliveryFactory.INSTANCE.getWorkflowConfiguration(this.mp.getSystemName());
		for(Object o : input) {
			Statement statement = (Statement)o;
			this.mp.checkMessagesOnPopup(statement, configuration);
		}
	}
	
	public Statement getSelectedElement() {
		List input = (List)this.tv.getInput();
		if ( input == null ) {
			return null;
		}
		int index = this.tv.getTable().getSelectionIndex();
		return (Statement)input.get(index);
	}

	
	public void selectElement(String id) {
		if(this.tv == null ){
			return;
		}
		final List input = (List)this.tv.getInput();
		for(Object o : input) {
			final Statement statement = (Statement)o;
			if(statement.getMessageId().equals(id)) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						MessagesTable.this.tv.getTable().select(input.indexOf(statement));
						MessagesTable.this.tv.getTable().showSelection();
					}
				});
			}
		}
	}
	
	
	public void clear() {
		this.tv.getTable().clearAll();
	}
	
	public void scrollToTop() {
		if(this.tv == null) {
			return;
		}
		this.tv.getTable().getVerticalBar().setSelection(0);
	}

	
	public int getRowCount() {
		return ((List)this.tv.getInput()).size();
	}

	
	public List getAllMessages() {
		if(this.tv != null) {
			List input = (List)this.tv.getInput();
			return input;
		}
		return null;
	}
	
	public void setInput(final List<Statement> input) {
		if(this.tv == null) {
			logger.error("can't set input cause viewer is null");
			return;
		}
		
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				MessagesTable.this.tv.setInput(input);
				findMessagesToPopup();
				update();
			}
		});
	}

	/**
	 * @return
	 */
	public Table asComponent() {
		return this.tv.getTable();
	}
	
	public void addMouseListener(MouseListener listener) {
		this.tv.getTable().addMouseListener(listener);
	}
	
	public void addControlListener(ControlListener listener) {
		this.tv.getTable().addControlListener(listener);
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.IMessagesTable#refresh()
	 */
	public void refresh() {
		update();
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.IMessagesTable#getInput()
	 */
	public Object getInput() {
		if(this.tv==null) {
			return null;
		}
		return this.tv.getInput();
	}
}

