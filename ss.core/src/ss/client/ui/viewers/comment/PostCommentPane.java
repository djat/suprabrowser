package ss.client.ui.viewers.comment;

import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.util.SessionConstants;

public class PostCommentPane extends Composite {
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_COMMENT_POSTCOMMENTPANE);

	private static final String CLEAR = "POSTCOMMENTPANE.CLEAR";
	private static final String SAVE = "POSTCOMMENTPANE.SAVE";
	private static final String CANCEL = "POSTCOMMENTPANE.CANCEL";
	private static final String SUBJECT = "POSTCOMMENTPANE.SUBJECT";
	private static final String COMMENT = "POSTCOMMENTPANE.COMMENT";
	
	private Button save;
	private Button clear;
	private Button cancel;
	private Text inputField;
	private Text subjectField;
	private CommentTabFolder parentFolder;
	private Hashtable session;
	private MessagesPane mp;
	
	private static final Logger logger = SSLogger.getLogger(PostCommentPane.class);

	public PostCommentPane(CommentTabFolder parentFolder) {
		super(parentFolder, SWT.NONE);
		this.parentFolder = parentFolder;
		
		this.session = this.parentFolder.getApplWindow().getSession();
		this.mp = this.parentFolder.getApplWindow().getMP();
		setLayout(new GridLayout(1, false));
		createLabel(this.bundle.getString(SUBJECT));
		createSubjectField();
		createLabel(this.bundle.getString(COMMENT));
		createInputField();
		createButtonPane();
		this.layout();
		this.redraw();
	}

	private void createButtonPane() {
		Composite comp = new Composite(this, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.END;
		comp.setLayoutData(data);

		comp.setLayout(new GridLayout(3, true));

		this.save = new Button(comp, SWT.PUSH | SWT.RIGHT);
		this.save.setText(this.bundle.getString(SAVE));
		this.save.setVisible(true);
		this.save.setEnabled(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.END;
		data.verticalAlignment = GridData.FILL;
		this.save.setLayoutData(data);
		this.save.setEnabled(false);
		this.save.addSelectionListener(new SelectionListener() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				CommentStatement comment = PostCommentPane.this.createNewComment(); 
			    
				comment.setConfirmed(true);
				
				comment.setVotingModelDesc("Absolute without qualification");
				comment.setVotingModelType("absolute");
		        comment.setTallyNumber("0.0");
		        comment.setTallyValue("0.0");
		    
		        PostCommentPane.this.getSession().put("delivery_type", "normal");
		    
		        PostCommentPane.this.getMP().client.publishTerse(PostCommentPane.this.getSession(),
		                comment.getBindedDocument());
		  
				getParent().getParent().dispose();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.clear = new Button(comp, SWT.PUSH | SWT.RIGHT);
		this.clear.setText(this.bundle.getString(CLEAR));
		this.clear.setVisible(true);
		this.clear.setEnabled(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.END;
		data.verticalAlignment = GridData.FILL;
		this.clear.setLayoutData(data);
		this.clear.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getInputField().setText("");
				getInputField().setFocus();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.cancel = new Button(comp, SWT.PUSH | SWT.RIGHT);
		this.cancel.setText(this.bundle.getString(CANCEL));
		this.cancel.setVisible(true);
		this.cancel.setEnabled(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.END;
		data.verticalAlignment = GridData.FILL;
		this.cancel.setLayoutData(data);
		this.cancel.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getParent().getParent().dispose();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		comp.layout();
		comp.redraw();

	}

	private void createInputField() {
		this.inputField = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.inputField.setLayoutData(data);
		this.inputField.setEditable(true);
		
		this.inputField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				getSaveButton().setEnabled(!getInputField().getText().trim().equals(""));
					
			}
		});

	}

	private void createSubjectField() {

		this.subjectField = new Text(this, SWT.BORDER | SWT.WRAP | SWT.LEFT | SWT.MULTI | SWT.V_SCROLL);
		if(this.parentFolder.getApplWindow().getSelection()!=null)
			this.subjectField.setText(this.parentFolder.getApplWindow().getSelection());
		this.subjectField.setEditable(false);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		this.subjectField.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		this.subjectField.setLayoutData(data);

	}
	
	private void createLabel(String text) {
		Label label = new Label(this, SWT.LEFT);
		label.setText(text+" : ");
		
		label.setFont(new Font(Display.getDefault(),"Nimbus Mono L", 10, SWT.BOLD));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		label.setLayoutData(data);
	}

	public Text getInputField() {
		return this.inputField;
	}

	public CommentStatement createNewComment() {

		CommentStatement newComment = new CommentStatement();
		
		Statement statement = this.parentFolder.getApplWindow().getParentStatement();
		
		String selection = this.subjectField.getText();
		
		newComment.setNumber(this.parentFolder.getApplWindow().getNumber());
		
		if(statement.isComment()) {
			CommentStatement tempComment = CommentStatement.wrap(this.mp.getLastSelectedDoc());
			if(tempComment.getSelectedBody().equals(selection) && newComment.getNumber().equals(tempComment.getNumber())) {
				newComment.setResponseId(statement.getMessageId());
			} else if(findSameSelection(tempComment.getCommentId(), selection, newComment.getNumber())!=null){
				newComment.setResponseId(findSameSelection(tempComment.getCommentId(), selection, newComment.getNumber()));
			} else {
				newComment.setResponseId(tempComment.getCommentId());
			}
			newComment.setCommentId(tempComment.getCommentId());
			newComment.setCommentThread(tempComment.getCommentThread());
			newComment.setAddress(tempComment.getAddress());
		} else {
			newComment.setCommentId(statement.getMessageId());
			newComment.setCommentThread(statement.getType());
			
			if(this.mp.getSmallBrowser().getUrl()!=null) {
				newComment.setAddress(this.mp.getSmallBrowser().getUrl());
			}
			
			if(findSameSelection(newComment.getCommentId(), selection, newComment.getNumber())!=null) {
				newComment.setResponseId(findSameSelection(newComment.getCommentId(), selection, newComment.getNumber()));
			} else {
				newComment.setResponseId(statement.getMessageId());
			}
		}
		
		newComment.setGiver((String)getMP().client.session.get(SessionConstants.REAL_NAME));
		
		String subject = createSubjectString(newComment, selection);
		newComment.setSubject(subject);
		newComment.setLastUpdatedBy((String) this.parentFolder.getApplWindow().getMP().getRawSession().get("real_name"));
		newComment.setOriginalId(statement.getOriginalId());
		newComment.setType("comment");
		

		newComment.setComment(PreviewHtmlTextCreator.prepearText(this.inputField.getText()));
		newComment.setOrigBody(this.subjectField.getText());
		newComment.setBody(this.subjectField.getText());
		newComment.setSelectedBody(this.subjectField.getText());
		
		
		try {
			newComment.setThreadType(statement.getThreadType());
		} catch (NullPointerException npe) {
			logger.error("what you talkin about willis!! :");
		}

		return newComment;

	}

	private String createSubjectString(CommentStatement newComment, String selection) {
		int number = getCommentCount(newComment.getCommentId(), selection, newComment.getNumber());
		String extract = getInputField().getText().length()>10 ? getInputField().getText().substring(0, 10) : getInputField().getText();
		String subject = newComment.getGiver()+"("+number+"):"+extract+"..";
		return subject;
	}




	/**
	 * @param selection
	 * @param number
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int getCommentCount(String commentId, String selection, String number) {
		int i = 1;
		for(Statement statement : getMP().getTableStatements()) {
			if(statement.isComment()) {	
				CommentStatement comment = CommentStatement.wrap(statement.getBindedDocument());
				if(comment.getCommentId().equals(commentId) && 
						comment.getBody().equals(selection) &&
						comment.getNumber().equals(number)) {
					i++;
				}
			}
		}
		return i;
	}

	/**
	 * @return
	 */
	private String findSameSelection(String commentId, String selection, String number) {
		List<Document> docs = this.mp.getMessagesTree().getChildrenFor(commentId);
		for(Document doc : docs) {
			Statement statement = Statement.wrap(doc);
			if(statement.isComment()) {
				CommentStatement comment = CommentStatement.wrap(doc);
				if(comment.getSelectedBody().equals(selection) && comment.getNumber().equals(number)) {
					return comment.getMessageId();
				}
			}
		}
		return null;
	}
	
	public CommentTabFolder getParentFolder() {
		return this.parentFolder;
	}
	
	public Hashtable getSession() {
		return this.session;
	}
	
	public MessagesPane getMP() {
		return this.mp;
	}
	
	public void setFocusToInputField() {
		this.inputField.setFocus();
	}
	
	public Button getSaveButton() {
		return this.save;
	}
	
	public Text getSubjectField() {
		return this.subjectField;
	}
}
