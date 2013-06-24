/**
 * 
 */
package ss.client.ui.viewers;

import java.io.File;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.event.InputTextPaneListener;
import ss.client.event.ViewersInputPaneListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.browser.SMessageBrowser;
import ss.client.ui.email.AttachFileComponent;
import ss.common.LocationUtils;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.MessageStatement;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.util.SessionConstants;
import ss.util.TextQuoter;

/**
 * @author roman
 *
 */
public class NewMessage extends ContentTypeViewerSWT {

	private static final int VIEWER_WIDTH = 800;
	
	private static final int VIEWER_HEIGHT = 600;

	private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWMESSAGE);
	
	private static final String AUTHOR = "NEWMESSAGE.AUTHOR";
	private static final String SAVE = "NEWMESSAGE.SAVE";
	private static final String TITLE = "NEWMESSAGE.TITLE";
	private static final String CANCEL = "NEWMESSAGE.CANCEL";
	private static final String NEW_MESSAGE = "NEWMESSAGE.NEW_MESSAGE";
	
	//private Text bodyEditor = null;
	
	private SMessageBrowser bodyEditor = null;

    private Statement viewStatement = null;

	private AbstractDelivery delivery;

	private Text subjectField;

	private Label giverLabel;

	private Composite mainComp;

	private AttachFileComponent attachFileComponent;
	
	private String prefSubject = null;
	
	private String prefBody = null;
	
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NewMessage.class);
    
    public NewMessage(Hashtable session, MessagesPane mP, Statement viewStatement,
            AbstractDelivery delivery, String prefSubject) {
        this(session, mP, viewStatement,
                delivery, prefSubject, null);
    }

    public NewMessage(Hashtable session, MessagesPane mP, Statement viewStatement,
            AbstractDelivery delivery, String prefSubject, String prefBody ) {

    	super();
    	
        this.session = session;
        this.viewStatement = viewStatement;

        this.delivery = delivery;
        this.mP = mP;
        this.model_path = "//message/generate";

        this.prefSubject = prefSubject;
        this.prefBody = prefBody;
        
        this.setFillMessage(false);
        createUI();
        addShellListener();
    }
    
    public NewMessage(Hashtable session, MessagesPane mP, Statement viewStatement,
            AbstractDelivery delivery) {
    	this(session, mP, viewStatement, delivery, null);
    }

    /**
	 * @param send_session
	 * @param mp
	 * @param delivery_type2
	 */
	public NewMessage(Hashtable send_session, MessagesPane mp, AbstractDelivery delivery ) {
		this(send_session, mp, null, delivery);
	}
	
	 /**
	 * @param send_session
	 * @param mp
	 * @param delivery_type2
	 */
	public NewMessage(Hashtable send_session, MessagesPane mp, String subject, String body ) {
		this(send_session, mp, null, DeliveryFactory.INSTANCE.create( "normal" ), subject, body);
	}

	public NewMessage getFrame() {
        return this;
    }

    public void disposeAll() {
        this.shell.dispose();
    }
    
    public void addKeyListener() {
		ViewersInputPaneListener listener = new ViewersInputPaneListener(this.mP.sF, this.mP);
		
		this.bodyEditor.addKeyListener(listener);
		this.subjectField.addKeyListener(listener);
	}

    public void createUI() {
    	UiUtils.swtBeginInvoke(new Runnable() {
    		public void run() {
    				NewMessage.this.mainComp = layoutShell();
        			center();

        			createHeaderPane();
        			createAttachFilesComposite();
        			
        			createBodyEditor();
        			createSaveButton();

        			center();
        			getShell().setVisible(true);
        			
        			addKeyListener();
        	}
    	});
    }

	private Composite layoutShell() {
		Composite mainComp;
		GridLayout shellLayout = new GridLayout();
		shellLayout.marginHeight = 0;
		shellLayout.marginWidth = 0;
		shellLayout.numColumns = 1;
		shellLayout.makeColumnsEqualWidth = false;
		this.shell.setLayout(shellLayout);

		this.shell.setText(this.bundle.getString(NEW_MESSAGE));

		this.shell.setImage(this.sphereImage);

		mainComp = new Composite(this.shell, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		//layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		mainComp.setLayout(layout);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		mainComp.setLayoutData(data);
		return mainComp;
	}
    
    /**
	 * @param mainComp
	 */
	private void createSaveButton() {
		Composite comp = new Composite(this.mainComp, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
		comp.setLayout(new GridLayout(2, false));
		
		Button save = new Button(comp, SWT.PUSH);
		save.setText(this.bundle.getString(SAVE));
		save.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
            	NewMessage nm = NewMessage.this;
            	nm.bodyEditor.invokeRichTextJSMonitor();
			}
		});
		
		Button cancel = new Button(comp, SWT.PUSH);
		cancel.setText(this.bundle.getString(CANCEL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				getShell().dispose();
			}
		});
		
	}

	/**
	 * @param mainComp
	 */
	private void createBodyEditor() {
		this.bodyEditor = new SMessageBrowser(this);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		this.bodyEditor.setLayoutData(data);
		
		this.bodyEditor.addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(ProgressEvent event) {
				String body = "";
            	Statement viewStatement = NewMessage.this.viewStatement;
            	if( viewStatement==null ) {
            		if (NewMessage.this.prefBody != null ){
            			NewMessage.this.bodyEditor.setTextToTextEditor(TextQuoter.INSTANCE.breakAndCleanUp(NewMessage.this.prefBody));
            		}
            	} else {
            		if(!StringUtils.isBlank(viewStatement.getBody())) {
            			body = viewStatement.getBody();
            		} else if(!StringUtils.isBlank(viewStatement.getOrigBody())){
            			body = viewStatement.getOrigBody();
            		} else {
            			body = viewStatement.getSubject();
            		}
            		NewMessage.this.bodyEditor.setTextToTextEditor(TextQuoter.INSTANCE.breakAndMakeQuoted(body));
            	}
			}
		});
		
		File f = new File(LocationUtils.getTinymceBase()+"tinymce/ss_richtext.html");
		this.bodyEditor.setUrl(f.getAbsolutePath());
		if(this.viewStatement!=null && this.prefSubject!=null) {
			this.bodyEditor.setFocus();
		}
	}

	/**
	 * @param mainComp
	 */
	private void createHeaderPane() {
		Composite comp = new Composite(this.mainComp, SWT.BORDER);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalSpan = 1;
		comp.setLayoutData(data);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		comp.setLayout(layout);

		createLabelPanel(comp);
		createTextFieldPanel(comp);
		
		getSubjectField().setFocus();
	}
	
	private void createAttachFilesComposite(){

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalSpan = 1;
		data.heightHint = 130;
		
		this.attachFileComponent = new AttachFileComponent(this.mainComp, SWT.BORDER);
		this.attachFileComponent.getContent().setLayoutData(data);
	}

	/**
	 * @param comp
	 */
	private void createTextFieldPanel(Composite comp) {
		Composite composite = new Composite(comp, SWT.NONE);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		composite.setLayoutData(data);
		
		composite.setLayout(new GridLayout(1, false));
		
		Label authorName = new Label(composite, SWT.NONE);
		authorName.setText((String) this.session.get(SessionConstants.REAL_NAME));
		
		this.subjectField = new Text(composite, SWT.BORDER);
		this.subjectField.setEditable(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.subjectField.setLayoutData(data);
		
		if(this.prefSubject!=null) {
			this.subjectField.setText(this.prefSubject);
		} else if(this.viewStatement!=null) {
			this.subjectField.setText("RE: "+this.viewStatement.getSubject());
		}
	}

	/**
	 * @param comp
	 */
	private void createLabelPanel(Composite comp) {
		Composite composite = new Composite(comp, SWT.NONE);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		composite.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.CENTER;
		data.widthHint = 55;
		
		this.giverLabel = new Label(composite, SWT.NONE);
		this.giverLabel.setText(this.bundle.getString(AUTHOR));
		this.giverLabel.setLayoutData(data);
		
		Label subjectLabel = new Label(composite, SWT.NONE);
		subjectLabel.setText(this.bundle.getString(TITLE));
		subjectLabel.setLayoutData(data);
	}

	public Document createFileStatement() {
    	return createMessage().getBindedDocument();
    }
    

    public MessageStatement createMessage() {
    	MessageStatement message = new MessageStatement();

        message.setGiver((String)this.session.get(SessionConstants.REAL_NAME));
        message.setGiverUsername((String)this.mP.getRawSession().get(SessionConstants.USERNAME));
        message.setSubject(this.subjectField.getText());
        message.setLastUpdatedBy((String) this.session.get("contact_name"));

        if (this.viewStatement != null) {
        	message.setResponseId(this.viewStatement.getMessageId());
        }
        message.setType("message");
        message.setThreadType("message");
        message.setVersion("3000");

        return message;
    }
    
    public void addKeyListener(InputTextPaneListener itpl) {
//    	this.subjectField.addKeyListener(arg0);
//    	this.bodyEditor.addKeyListener(arg0);
    }
    
    public Text getSubjectField() {
    	return this.subjectField;
    }
    
    public Composite getMainComp() {
    	return this.mainComp;
    }

	/**
	 * @return
	 */
	public SMessageBrowser getBodyEditor() {
		return this.bodyEditor;
	}

	/**
	 * @param bodyMessage
	 */
	@SuppressWarnings("unchecked")
	public void publishMessage(final String bodyMessage) {
		final MessageStatement message = createMessage();

    	message.setVotingModelDesc("Absolute without qualification");
    	message.setVotingModelType("absolute");
        message.setTallyNumber("0.0");
        message.setTallyValue("0.0");
        
        message.setBody(bodyMessage);
        message.setOrigBody(bodyMessage);
        
        this.session.put("delivery_type", "normal");
        ResultStatement result = null;
        if (this.delivery != null ) {
          	result = this.delivery.prepareStatement( message );
        }
        
        //this.mP.client.publishTerse(this.session, message.getBindedDocument());
        this.mP.client.sendMessageFromServer(this.session, this.attachFileComponent.getFiles(), message.getBindedDocument());
        if(result != null) {
        	this.mP.client.publishTerse(this.session, result.getBindedDocument());
        }
        this.mP.sF.setReplyChecked( false);
        this.mP.sF.setSendText("");
        this.shell.dispose();
	}

	@Override
	public void giveBodyFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getViewerSWTWidth() {
		return VIEWER_WIDTH;
	}
	
	@Override
	protected int getViewerSWTHeight() {
		return VIEWER_HEIGHT;
	}
}
