/**
 * 
 */
package ss.client.ui.viewers;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.ViewersInputPaneListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.processing.TagActionProcessor;
import ss.client.ui.typeahead.TypeAheadManager;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.global.SSLogger;
import ss.search.URLParser;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class NewWeblink extends ContentTypeViewerSWT{

	private static final Logger logger = SSLogger.getLogger(NewWeblink.class);
	
	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWWEBLINK);

	private static final String AUTHOR = "NEWWEBLINK.AUTHOR";
	private static final String SUBJECT = "NEWWEBLINK.SUBJECT";
	private static final String ADDRESS = "NEWWEBLINK.ADDRESS";
	private static final String SAVE = "NEWWEBLINK.SAVE";
	private static final String CANCEL = "NEWWEBLINK.CANCEL";
	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE = "NEWWEBLINK.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE";
	private static final String NEW_BOOKMARK = "NEWWEBLINK.NEW_BOOKMARK";
	private static final String MUST_CHOOSE_SUBJECT = "NEWWEBLINK.MUST_CHOOSE_SUBJECT";

	private Label giverLabel = null;
	
	private Text subjectText = null;
	
	private Text addressText = null;
	
	private Text tagField = null;
	
	private Text bodyEditor = null;
	
	private Button saveButton = null;
	
	private Button cancelButton = null;

	private AbstractDelivery delivery = null;

	Document savedFromDoc;
	
	private Statement viewStatement = null;

	private boolean is_reply = false;

	private String sendText;

	private String desiredBody;
	
	private String tagtext = null;

	private String desiredSubject = null;
	
	private boolean isRssCreation = false;

	public NewWeblink(Hashtable session, MessagesPane mP, AbstractDelivery delivery, String sendText ) {	
		super();
		this.delivery = delivery;
		this.session = session;
		this.sendText = sendText;

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				NewWeblink.this.shell = new Shell(Display.getDefault(), SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
				NewWeblink.this.shell.setVisible(false);
				NewWeblink.this.shell.setSize(new Point(640, 480));
			}
		});
				
		this.mP = mP;
		this.model_path = "//bookmark/generate";
		
    	this.setFillMessage(this.viewStatement==null);
    	
		createUI();
		addShellListener();

	}
	
	public NewWeblink(Hashtable session, MessagesPane mP, String deliveryType, String sendText ) {
		this( session,mP, DeliveryFactory.INSTANCE.create(deliveryType), sendText  );
	}
	
	public NewWeblink(Hashtable session, MessagesPane mP, String deliveryType, String sendText, String title, String body ) {
		this( session, mP, null, DeliveryFactory.INSTANCE.create(deliveryType), false, sendText, title, false );
		this.desiredBody = body;
		this.desiredSubject = title;
	}
	
	public NewWeblink(Hashtable session, MessagesPane mP, Document viewDoc,
			AbstractDelivery delivery, boolean is_reply, String sendText, final String desiredSubject) {
		this(session, mP, viewDoc, delivery, is_reply, sendText, desiredSubject, true);
	}

	public NewWeblink(Hashtable session, MessagesPane mP, Document viewDoc,
			AbstractDelivery delivery, boolean is_reply, String sendText, final String desiredSubject, final boolean visible) {

		super();
		this.delivery = delivery;
		this.is_reply  = is_reply;
		this.session = session;
		this.sendText = sendText;
		this.desiredSubject = desiredSubject;

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				NewWeblink.this.shell = new Shell(Display.getDefault(), SWT.TITLE | SWT.RESIZE);
				NewWeblink.this.shell.setVisible(false);
				NewWeblink.this.shell.setSize(new Point(640, 480));
			}
		});
		
		if(viewDoc!=null)
			this.viewStatement = Statement.wrap(viewDoc);

		this.mP = mP;
		this.model_path = "//bookmark/generate";
		
    	this.setFillMessage(this.viewStatement==null);

		createUI();
		addShellListener();
		try {
			addKeyListener();
		} catch (Exception ex){
			logger.error("Can not add key listeners",ex);
		}
		
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				NewWeblink.this.shell.setVisible(visible);
			}
		});
	}
	
	public void setSavedFrom(Document savedFromDoc) {
		logger.info("Saved from doc set right here: "
				+ savedFromDoc.asXML());
		this.savedFromDoc = savedFromDoc;
		
	}

	public NewWeblink getFrame() {
		return this;
	}
	
	public void addKeyListener() {
		final ViewersInputPaneListener listener = new ViewersInputPaneListener(SupraSphereFrame.INSTANCE, this.mP);
		
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				NewWeblink.this.bodyEditor.addKeyListener(listener);
				NewWeblink.this.subjectText.addKeyListener(listener);
				NewWeblink.this.addressText.addKeyListener(listener);
			}
		});
	}

	public void disposeAll() {
		this.shell.dispose();
	}

	public void createUI() {
		final NewWeblink nw = NewWeblink.this;
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				GridLayout shellLayout = new GridLayout();
				shellLayout.marginHeight = 0;
				shellLayout.marginWidth = 0;
				shellLayout.numColumns = 1;
				shellLayout.makeColumnsEqualWidth = false;
				nw.shell.setLayout(shellLayout);

				nw.shell.setText(nw.bundle.getString(NEW_BOOKMARK));

				nw.shell.setImage(nw.sphereImage);

				Composite mainComp = new Composite(nw.shell, SWT.NONE);
				GridLayout layout = new GridLayout(1, true);
				layout.numColumns = 1;
				layout.marginHeight = 0;
				layout.marginTop = 0;
				mainComp.setLayout(layout);

				GridData data = new GridData();
				data.grabExcessHorizontalSpace = true;
				data.grabExcessVerticalSpace = true;
				data.verticalAlignment = GridData.FILL;
				data.horizontalAlignment = GridData.FILL;
				mainComp.setLayoutData(data);

				center();

				createLabelAndTextPanel(mainComp);
				createBodyEditor(mainComp);
				createButtonPanel(mainComp);

			}
		});
	}

	/**
	 * @param mainComp
	 */
	private void createLabelAndTextPanel(Composite mainComp) {
		Composite comp = new Composite(mainComp, SWT.NONE);
		
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		comp.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		comp.setLayout(layout);
		
		createLabelPanel(comp);
		createTextFieldPanel(comp);
		
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
		
		this.subjectText = new Text(composite, SWT.BORDER);
		this.subjectText.setEditable(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.subjectText.setLayoutData(data);
		
		this.addressText = new Text(composite, SWT.BORDER);
		this.addressText.setEditable(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.addressText.setLayoutData(data);
		
		this.tagField = new Text(composite, SWT.BORDER);
		this.tagField.setEditable(true);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.tagField.setLayoutData(data);
		TypeAheadManager.INSTANCE.addKeywordAutoComplete(this.tagField);
		
		if(this.sendText!=null) {
			this.addressText.setText(this.sendText);
		}
		if(this.desiredSubject!=null) {
			this.subjectText.setText(this.desiredSubject);
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
		subjectLabel.setText(this.bundle.getString(NewWeblink.SUBJECT));
		subjectLabel.setLayoutData(data);
		
		Label addressLabel = new Label(composite, SWT.NONE);
		addressLabel.setText(this.bundle.getString(NewWeblink.ADDRESS));
		addressLabel.setLayoutData(data);
		
		Label tagLabel = new Label(composite, SWT.NONE);
		tagLabel.setText("Tag: ");
		tagLabel.setLayoutData(data);
		
	}

	/**
	 * @param mainComp
	 */
	private void createButtonPanel(Composite mainComp) {
		Composite comp = new Composite(mainComp, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		this.saveButton = new Button(comp, SWT.NONE);
		this.saveButton.setText(this.bundle.getString(NewWeblink.SAVE));

		this.cancelButton = new Button(comp, SWT.NONE);
		this.cancelButton.setText(this.bundle.getString(NewWeblink.CANCEL));

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.BEGINNING;
		comp.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.BEGINNING;
		data.verticalAlignment = GridData.BEGINNING;
		data.widthHint = 55;
		this.saveButton.setLayoutData(data);
		this.cancelButton.setLayoutData(data);
		
		this.saveButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				if(getSubjectField().getText()==null || getSubjectField().getText().trim().equals("")) {
					UserMessageDialogCreator.error(NewWeblink.this.bundle.getString(MUST_CHOOSE_SUBJECT));
				} else {
					doPublishAction();
				}
			}
			public void widgetDefaultSelected(SelectionEvent se) {

			}
		});
		
		this.cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				final NewWeblink nw = NewWeblink.this;
				logger.info("presssed");

				try {

//					final String sphereId = (String) nw.session.get("sphere_id");

					if (!nw.isFillMessage) {
						UserMessageDialogCreator.warningDeleteMessage(getShell(), nw.bundle.getString(NewWeblink.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE), new WarningDialogListener() {
							public void performCancel() {
							}
							public void performOK() {
//								SupraSphereFrame.INSTANCE.client.recallMessage(nw.session
//										, nw.viewStatement.getBindedDocument(), sphereId);
								getShell().dispose();
							}
						}, true);
					} else {
						getShell().dispose();
					}
				} catch (NullPointerException npe) {
					logger.error(npe.getMessage(), npe);
				}
			}
			public void widgetDefaultSelected(SelectionEvent se) {
				
			}
		});
		
	}

	/**
	 * @param mainComp
	 */
	private void createBodyEditor(Composite mainComp) {
		this.bodyEditor = new Text(mainComp, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.bodyEditor.setLayoutData(data);
		
		if(this.desiredBody!=null) {
			this.bodyEditor.setText(this.desiredBody);
		}
		
		if(this.sendText!=null && this.sendText.length()>0) {
			giveBodyFocus();
		}
	}

	public void doPublishAction() {
		Thread t = new Thread() {
			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				NewWeblink nw = NewWeblink.this;
				
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						NewWeblink.this.saveButton.setEnabled(false);
					}
				});

				BookmarkStatement bookmark = createBookmark();
				ResultStatement result = nw.delivery.prepareStatement( bookmark );
				if (NewWeblink.this.desiredSubject!=null) {
					bookmark.setSubject(NewWeblink.this.desiredSubject);
				}
				bookmark.setVotingModelDesc("Absolute without qualification");
				bookmark.setVotingModelType("absolute");
				bookmark.setTallyNumber("0.0");
				bookmark.setTallyValue("0.0");

				nw.session.put("delivery_type", "normal");

				logger.info("PUBLISHING TO: "
						+ (String) nw.session.get("sphere_id"));

				if (SupraSphereFrame.INSTANCE == null) {
					logger.info("sf was nulll.....");
				}
				
				SupraSphereFrame.INSTANCE.client.publishTerse(nw.session, bookmark.getBindedDocument());
				
				if(result != null) {
					SupraSphereFrame.INSTANCE.client.publishTerse(nw.session, result.getBindedDocument());
				}
				if (SupraSphereFrame.INSTANCE != null) {
					SupraSphereFrame.INSTANCE.setReplyChecked(false);
					SupraSphereFrame.INSTANCE.setSendText( "");
				}
				
				String tagText = NewWeblink.this.tagtext!=null ? NewWeblink.this.tagtext : UiUtils.swtEvaluate(new Callable<String>() {
					public String call() throws Exception {
						return NewWeblink.this.tagField.getText();
					}
				});
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						NewWeblink.this.shell.dispose();
					}
				});
				
				if(StringUtils.isNotBlank(tagText)) {
					String tagString = tagText;
					Document doc = SupraSphereFrame.INSTANCE.client.getSpecificId(NewWeblink.this.session, bookmark.getMessageId());
					TagActionProcessor processor = new TagActionProcessor(SupraSphereFrame.INSTANCE.client, (String) nw.session.get(SessionConstants.SPHERE_ID2), doc);
					processor.doTagAction(tagString);
				}
			}	
		};
		t.start();
	}

	public void fillDoc(Document doc) {
		this.viewStatement = Statement.wrap(doc);

		this.addressText.setText(this.viewStatement.getAddress());
		this.subjectText.setText(this.viewStatement.getSubject());
		this.bodyEditor.setText(this.viewStatement.getBody());

	}

	public Document createFileStatement() {
		return null;
	}

	public BookmarkStatement createBookmark() {
		final BookmarkStatement bookmark = new BookmarkStatement();

		String subjectText = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return NewWeblink.this.subjectText.getText();
			}
		});

		String addressText = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return NewWeblink.this.addressText.getText();
			}
		});

		String bodyText = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return NewWeblink.this.bodyEditor.getText();
			}
		});

		Hashtable all = SupraSphereFrame.INSTANCE.client.createMessageIdOnServer(NewWeblink.this.session);

		String messageId = (String) all.get(SessionConstants.MESSAGE_ID);

		bookmark.setOriginalId(messageId);
		bookmark.setMessageId(messageId);
		bookmark.setThreadId(messageId);
		bookmark.setGiver((String) NewWeblink.this.session.get(SessionConstants.REAL_NAME));
		bookmark.setGiverUsername((String)NewWeblink.this.session.get(SessionConstants.USERNAME));
		bookmark.setSubject(subjectText);
		bookmark.setAddress(getFixedUrl(addressText));
		bookmark.setLastUpdatedBy((String) NewWeblink.this.session.get(SessionConstants.REAL_NAME));
		
		String type = this.isRssCreation ? "rss" : "bookmark";
		bookmark.setType(type);

		if (NewWeblink.this.is_reply == false) {
			bookmark.setThreadType(bookmark.getType());
		} else {
			bookmark.setResponseId(NewWeblink.this.viewStatement.getMessageId());
			bookmark.setOriginalId(NewWeblink.this.viewStatement.getOriginalId());
			bookmark.setThreadType(NewWeblink.this.viewStatement.getThreadType());
		}

		bookmark.setBody(bodyText);
		bookmark.setVersion("3000");
		bookmark.setOrigBody(bodyText);


		return bookmark;
	}

	private String getFixedUrl(String url) {
		return URLParser.getFixedUrl(url);
	}

	@Override
	public void giveBodyFocus() {
		this.bodyEditor.setFocus();
	}
	
	public Text getSubjectField() {
		return this.subjectText;
	}
	
	public Text getBodyEditor() {
		return this.bodyEditor;
	}

	/**
	 * @param tagtext2
	 */
	public void setTagText(String text) {
		this.tagtext = text;
	}
	
	public void setIsRssCreation(final boolean value) {
		this.isRssCreation = value;
	}
}
