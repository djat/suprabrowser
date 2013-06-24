package ss.client.ui.viewers.comment;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
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
import ss.client.ui.browser.SupraBrowser;
import ss.domainmodel.CommentStatement;

public class BlockCommentsPane extends Composite {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BlockCommentsPane.class);
	
	private Text commentHistoryField;
	private CommentTabFolder parentFolder;
	private Label subjectLabel;
	private Text subjectText;
	//private JEditorPane textPane;
	private SupraBrowser browser;
	//private FrameContainer textContainer;
	
	private static final String COMMENTS = "BLOCKCOMMENTSPANE.COMMENTS";
	private static final String SUBJECT = "BLOCKCOMMENTSPANE.SUBJECT";
	private static final String EXIT = "BLOCKCOMMENTSPANE.EXIT";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_COMMENT_BLOCKCOMMENTSPANE);
	
	public BlockCommentsPane(CommentTabFolder parentFolder) {
		super(parentFolder, SWT.NONE);
		this.parentFolder = parentFolder;
		setLayout();
		
		createSubjectPane();
		createCommentLabel();
		createTextContainer();
		createExitButton();
	}

	/**
	 * 
	 */
	private void createSubjectPane() {
		
		this.subjectLabel = new Label(this, SWT.TOP);
		this.subjectLabel.setFont(new Font(Display.getDefault(),"Nimbus Mono L", 10, SWT.BOLD));
		this.subjectLabel.setText(this.bundle.getString(SUBJECT));
		GridData d = new GridData();
		d.verticalAlignment = GridData.BEGINNING;
		d.horizontalAlignment = GridData.BEGINNING;
		d.grabExcessHorizontalSpace = false;
		d.grabExcessVerticalSpace = false;
		this.subjectLabel.setLayoutData(d);
		
		this.subjectText = new Text(this, SWT.BORDER | SWT.WRAP | SWT.LEFT | SWT.MULTI | SWT.V_SCROLL);
		this.subjectText.setEditable(false);
		this.subjectText.setText(this.parentFolder.getApplWindow().getSelection());
		this.subjectText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData data = new GridData();
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		this.subjectText.setLayoutData(data);
		this.subjectText.setSize(370, 60);
	}

	private void setLayout() {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.setLayoutData(data);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		
		this.setLayout(layout);
	}


	private void createExitButton() {
		Composite comp = new Composite(this, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.END;
		comp.setLayoutData(data);
		comp.setLayout(new GridLayout(1, false));
		
		Button exit = new Button(comp, SWT.PUSH | SWT.RIGHT);
		exit.setText(this.bundle.getString(EXIT));
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		exit.setLayoutData(data);
		
		exit.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getParent().getParent().dispose();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}
	
	public CommentTabFolder getParentFolder() {
		return this.parentFolder;
	}
	
	public Text getCommentHistoryFied() {
		return this.commentHistoryField;
	}

	public Label getSubjectLabel() {
		return this.subjectLabel;
	}
	
	public Text getSubjectText() {
		return this.subjectText;
	}
	
	private void createCommentLabel() {
		Label comments = new Label(this, SWT.TOP);
		comments.setFont(new Font(Display.getDefault(),"Nimbus Mono L", 10, SWT.BOLD));
		comments.setText(this.bundle.getString(COMMENTS));
		GridData d = new GridData();
		d.verticalAlignment = GridData.BEGINNING;
		d.horizontalAlignment = GridData.BEGINNING;
		d.grabExcessHorizontalSpace = false;
		d.grabExcessVerticalSpace = false;
		comments.setLayoutData(d);
	}
	
	private void createTextContainer() {
		Composite comp = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		comp.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		comp.setLayoutData(data);
				
		this.browser = new SupraBrowser(comp, SWT.BORDER | SWT.MOZILLA);
		this.browser.setLayoutData(data);
		
		fillEditorPane(null);
	}
	
	public void fillEditorPane(String selection) {
		ListBuilder builder = new ListBuilder(this.parentFolder.getApplWindow());
		
		if(selection!=null){
			builder.setSelection(selection);
		}
		
		HTMLCommentListBuilder htmlBuilder = new HTMLCommentListBuilder(this.parentFolder.getApplWindow().getMP().client.getContact());
		for(CommentStatement comm: builder.returnCommentList()) {
			htmlBuilder.addComment(comm, this.parentFolder.getApplWindow().getSelectedComment().getMessageId());
		}
		//this.textPane.setText(htmlBuilder.getText());
		logger.info(htmlBuilder.getHtmlText());
		this.browser.resetText(htmlBuilder.getHtmlText());
	}
	
	public SupraBrowser getTextContainer() {
		logger.debug("browser call");
		return this.browser;
	}

	/**
	 * @param selection
	 */
	public void setTextToSubjectField(String selection) {
		this.subjectText.setText(selection);
	}
}


