/**
 * 
 */
package ss.client.ui.viewers;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.event.ViewMessageShowListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class ViewMessageSWT extends ContentTypeViewerSWT {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4596878140485312389L;

	private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_VIEWMESSAGE);
	
	private static final String TITLE = "VIEWMESSAGE.TITLE";
	private static final String MOMENT = "VIEWMESSAGE.MOMENT";
	private static final String AUTHOR = "VIEWMESSAGE.AUTHOR";
	private static final String SENDER = "VIEWMESSAGE.SENDER";
	private static final String PRINT = "VIEWMESSAGE.PRINT";
	private static final String SHOW_AND_EDIT_XML = "VIEWMESSAGE.SHOW_AND_EDIT_XML";
//	private static final String DELETE = "VIEWMESSAGE.DELETE";
	public static final String YES = "VIEWMESSAGE.YES";
	public static final String NO = "VIEWMESSAGE.NO";
	public static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE = "VIEWMESSAGE.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_MESSAGE";
	public static final String WARNING = "VIEWMESSAGE.WARNING";
	private static final String SAVE_XML = "VIEWMESSAGE.SAVE_XML";
	private static final String THAT_IS_NOT_VALID_XML = "VIEWMESSAGE.THAT_IS_NOT_VALID_XML";
	//private static final String SUBJECT = "VIEWMESSAGE.SUBJECT";
		
    
    private static final Logger logger = SSLogger.getLogger(ViewMessageSWT.class);
    
    private Button saveXML = null;

    private boolean isAdmin = false;  
    
    private Statement viewStatement = null;
    
    private Composite buttonComposite;
    
    private Composite mainComp;
    
    String bodyString;
    
    String subjectString;
    
    private Text bodyEditor;
    //private Text bodyEditor;
    //private SupraBrowser bodyEditor;

    public ViewMessageSWT(Hashtable session, org.dom4j.Document viewDoc,MessagesPane mP) {
    	super();   	

        this.session = session;
        this.mP = mP;

        if(viewDoc!=null) {
        	this.viewDoc = viewDoc;
        	this.viewStatement = Statement.wrap(viewDoc);
        }
       
       	initBodyString();
        
        createUI();
        addKeyListener();
    }

    
	protected void initBodyString() {
		if (this.viewStatement.isComment()) {
		    logger.info("doing it here!");
		    this.bodyString = CommentStatement.wrap(this.viewDoc).getComment();
		    
		} else if (this.viewStatement.isTerse()) {
		    this.bodyString = this.viewStatement.getSubject();
		    
		}  else if (this.viewStatement.isMessage()
		        || this.viewStatement.isContact()
		                || this.viewStatement.isAudio()
		                || this.viewStatement.isBookmark() 
		                || this.viewStatement.isEmail() || this.viewStatement.isReply()) {
		    String text = null;
		    CommentStatement cs = CommentStatement.wrap(this.viewStatement.getBindedDocument());
		    if (cs.getComment() != null) {
		        text = cs.getComment();
		    } else {
		        text = cs.getBody();
		    }	    
		    this.bodyString = text;
		}
	}
    
    public void addKeyListener() {
    	UiUtils.swtBeginInvoke(new Runnable() {
    		public void run() {
    			ViewMessageSWT.this.shell.addKeyListener(new KeyAdapter() {
            		public void keyPressed(org.eclipse.swt.events.KeyEvent ke) {
            			if(ke.character==SWT.ESC) {
            				ViewMessageSWT.this.shell.dispose();
            			}
            		}
            	});
    		}	
    	});   	
    }

    public ContentTypeViewerSWT returnFrame() {
      return this;

    }

    protected void createUI() {
    	if (this.mP.sF.client.getVerifyAuth().isAdmin((String) this.session.get(SessionConstants.REAL_NAME),
    			(String) this.session.get(SessionConstants.USERNAME))) {
    		this.isAdmin = true;
    	}

    	UiUtils.swtBeginInvoke(new Runnable() {
    		public void run() {
    			org.eclipse.swt.layout.GridLayout shellLayout = new org.eclipse.swt.layout.GridLayout();
    			shellLayout.marginHeight = 0;
    			shellLayout.marginWidth = 0;
    			shellLayout.numColumns = 1;
    			shellLayout.makeColumnsEqualWidth = false;
    			ViewMessageSWT.this.shell.setLayout(shellLayout);

    			ViewMessageSWT.this.shell.setImage(ViewMessageSWT.this.sphereImage);

    			ViewMessageSWT.this.mainComp = new Composite(ViewMessageSWT.this.shell, SWT.NONE);
    			org.eclipse.swt.layout.GridLayout layout = new org.eclipse.swt.layout.GridLayout(1, true);
    			layout.numColumns = 1;
    			layout.marginHeight = 0;
    			layout.marginTop = 0;
    			ViewMessageSWT.this.mainComp.setLayout(layout);

    			GridData data = new GridData();
    			data.grabExcessHorizontalSpace = true;
    			data.grabExcessVerticalSpace = true;
    			data.verticalAlignment = GridData.FILL;
    			data.horizontalAlignment = GridData.FILL;
    			ViewMessageSWT.this.mainComp.setLayoutData(data);

    			createLabelPanel(ViewMessageSWT.this.mainComp);
    			createBodyEditor(ViewMessageSWT.this.mainComp);
    			createButtonPanel(ViewMessageSWT.this.mainComp);

    			ViewMessageSWT.this.shell.setVisible(true);
    		}
    	});

    	initSubjectString();
    }

    private void initSubjectString() {
    	try {
    		if (!(this.viewStatement.isTerse())) {
    			String test = this.viewStatement.getSubject();
    			if (test.length() < 50) {
    					this.subjectString = test;
    			} else {
    					this.subjectString = test;
    			}
    		}
    	} catch (Exception e) {
    		String test = this.viewStatement.getSubject();
    		if (test.length() < 50) {
    			this.subjectString = test;
    		} else {
    			if (!(this.viewStatement.getThreadType().equals("email"))) {
    				logger.info("do here2");
    				this.bodyString = test;
    			}
    		}        
    	}
    }

   /* @SuppressWarnings("unchecked")
	private boolean canAddDeleteButton() {
    	MessagesPane selectedMessagesPane = (MessagesPane) this.mP.sF.tabbedPane
    	.getSelectedMessagesPane();
    	if (selectedMessagesPane == null) {
			logger.warn("Selected message pane is null");
			return false;
		}
    	boolean ismember = false;

    	if (selectedMessagesPane.getCreateDefinition() != null) {
    		Vector<Element> create = new Vector<Element>();
    		try {
    			List<Element> members = selectedMessagesPane.getCreateDefinition().getRootElement().elements("member");
    			create = new Vector<Element>(members);
    		} catch (NullPointerException npe) {
    		}

    		for (int i = 0; i < create.size(); i++) {
    			Element one = create.get(i);
    			String check = one.attributeValue("contact_name");
    			if (check.lastIndexOf((String) this.session.get("real_name")) != -1) {
    				ismember = true;
    			}
    		}
    	} else {
    		String giver = this.viewStatement.getGiver();

        	String systemName = this.mP.client.getVerifyAuth()
            	.getSystemName((String) this.session.get(SessionConstants.REAL_NAME));
        	if (giver.equals((String) this.session.get("real_name"))
        			|| systemName.equals((String) this.session.get("sphere_id"))) {
        		ismember = true;
        	}
    	}
    	
    	return ismember;
    }*/
    
    
    /**
	 * @param mainComp
	 */
    private void createButtonPanel(Composite mainComp) {
    	this.buttonComposite = new Composite(mainComp, SWT.NONE);

    	this.buttonComposite.setLayout(new GridLayout(4, false));

    	Button showAndEdit = null;
    	if(this.isAdmin) {
    		showAndEdit = new Button(this.buttonComposite, SWT.PUSH);
    		showAndEdit.setText(this.bundle.getString(SHOW_AND_EDIT_XML));
    		showAndEdit.addSelectionListener(new ViewMessageShowListener(this));
    	}

    	Button printButton = new Button(this.buttonComposite, SWT.PUSH);
    	printButton.setText(this.bundle.getString(PRINT));
    	printButton.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent se) {
    			printDoc();
    		}
    	});

    	/*if(canAddDeleteButton()) {
    		Button deleteButton = new Button(this.buttonComposite, SWT.PUSH);
    		deleteButton.setText(this.bundle.getString(DELETE));
    		deleteButton.addSelectionListener(new ViewMessageDeleteButtonListener(this));
    	}*/
    }

	/**
	 * @param mainComp
	 */
	private void createBodyEditor(Composite mainComp) {
//		this.bodyEditor = new SupraBrowser(mainComp, SWT.BORDER);
//		this.bodyEditor.setSupraSphereFrame(this.mP.sF);
		
		this.bodyEditor = new Text(mainComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		
		this.bodyEditor.setLayoutData(data);
		//this.bodyEditor.setEditable(false);
		
		if(this.bodyString!=null) {
			this.bodyEditor.setText(this.bodyString);
		}
		
	}

	/**
	 * @param mainComp
	 */
	private void createLabelPanel(Composite mainComp) {
		Composite comp = new Composite(mainComp, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		comp.setLayout(layout);

		Label authorLabel = new Label(comp, SWT.NONE);
		authorLabel.setText(this.bundle.getString(AUTHOR));
		if(this.viewStatement.isTerse()) {
			authorLabel.setText(this.bundle.getString(SENDER));
		}

		Label nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText(this.viewStatement.getGiver());

		Label titleLabel = new Label(comp, SWT.NONE);
		titleLabel.setText(this.bundle.getString(TITLE));

		Label subjectLabel = new Label(comp, SWT.NONE);
		if(this.subjectString != null) {
			subjectLabel.setText(this.subjectString);
		}
		if(this.viewStatement.isBookmark()) {
			subjectLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
			subjectLabel.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent arg0) {
					String address = ViewMessageSWT.this.viewStatement.getAddress();
					MessagesPane mp = ViewMessageSWT.this.mP;
					mp.showSmallBrowserNoFocusSteal(mp.getRawSession(), true, address, null, null, null);
				}
			});
			subjectLabel.addMouseTrackListener(new MouseTrackListener() {
				public void mouseEnter(MouseEvent arg0) {
					Cursor handCursor = Display.getDefault().getSystemCursor(SWT.CURSOR_HAND);
					Display.getCurrent().getCursorControl().setCursor(handCursor);
				}				
				public void mouseExit(MouseEvent arg0) {
					Cursor arrowCursor = Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW);
					Display.getCurrent().getCursorControl().setCursor(arrowCursor);
				}
				public void mouseHover(MouseEvent arg0) {					
				}				
			});
		}


		Label momentLabel = new Label(comp, SWT.NONE);
		momentLabel.setText(this.bundle.getString(MOMENT));

		Label timeLabel = new Label(comp, SWT.NONE);


		if(this.viewStatement.getMoment()==null)
			timeLabel.setText("--:--:--");
		else{
			timeLabel.setText(this.viewStatement.getMoment());
		}

	} 

	public void addSaveXMLDocButton() {
      if(this.saveXML==null)
      {
       this.saveXML = new Button(this.buttonComposite, SWT.PUSH);
       this.saveXML.setText(this.bundle.getString(SAVE_XML));
       this.saveXML.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent se) {
            try {         
              //doc = DocumentHelper.parseText(ViewMessageSWT.this.bodyEditor.getText());
              //ViewMessageSWT.this.mP.sF.client.replaceDoc(ViewMessageSWT.this.session,doc);             
            } catch (Exception e1) {
            	logger.error(e1.getMessage(), e1);
            	UserMessageDialogCreator.error(ViewMessageSWT.this.bundle.getString(ViewMessageSWT.THAT_IS_NOT_VALID_XML));
            }
            ViewMessageSWT.this.shell.dispose();
          }
        });
        }
      this.buttonComposite.layout();
      this.mainComp.layout();
    }

    public void printDoc() {
//        try {
//        	String toPrint = null; 
//            toPrint = this.bodyEditor.getText();
//            
//            String momentString = this.bundle.getString(MOMENT) + this.viewStatement.getMoment() + "\n\n";
//
//            String subjectStr = null;
//            
//            if (!(this.viewStatement.getThreadType().equals("terse"))) {
//
//                if (this.bodyEditor.getText().length() > 0) {
//                    subjectStr = this.bundle.getString(SUBJECT) + this.viewStatement.getSubject() + "\n";
//                }
//            }
//            
//            String senderString = this.bundle.getString(SENDER) + this.viewStatement.getGiver() + "\n";
//
//            toPrint = senderString + subjectStr + momentString + toPrint;
//            
//            if (this.bodyEditor.getText().length() < 1) {
//                toPrint = toPrint + this.viewStatement.getSubject() + "\n";
//            }
//
//            StyleContext sc5 = new StyleContext();
//        	Style style5 = sc5.addStyle(null, null);            
//            
//            PlainDocument doc = new PlainDocument();
//            doc.insertString(0, toPrint, style5);
//            
//            DocumentRenderer d = new DocumentRenderer();
//            d.print(doc);
//        } catch (Exception e) {
//        }
//
    }


	@Override
	public void giveBodyFocus() {
		this.bodyEditor.setFocus();
	}
	
	public ResourceBundle getBundle() {
		return this.bundle;
	}
	
	public void dispose() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				ViewMessageSWT.this.shell.dispose();
			}
		});
	}
	
	public Statement getViewStatement() {
		return this.viewStatement;
	}
	
	public Text getBodyEditor() {
		return this.bodyEditor;
	}

}
