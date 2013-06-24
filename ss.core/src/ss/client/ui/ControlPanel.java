/*
 * Created on Apr 28, 2004
 *
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.RefreshPeopleListListener;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.localization.LocalizationLinks;
import ss.client.preferences.PreferencesChecker;
import ss.client.ui.Listeners.ControlCompositeReplyBoxMouseListener;
import ss.client.ui.Listeners.ControlCompositeTagBoxMouseListener;
import ss.client.ui.Listeners.ControlPanelInputTextPaneListener;
import ss.client.ui.Listeners.SendFieldModifyListener;
import ss.client.ui.docking.DockingTopTitle;
import ss.client.ui.tempComponents.ControlPanelReplyBox;
import ss.common.UiUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.util.ImagesPaths;
/**
 * @author david
 * 
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ControlPanel extends AbstractControlPanel {
	
	private static ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_CONTROLPANEL);
	
	private static final String REPLY = "CONTROLPANEL.REPLY";
	private static final String TAG = "CONTROLPANEL.TAG";
	private static final String TYPE_LOCK = "CONTROLPANEL.TYPE_LOCK";

	private static final int MARGIN_FROM_FIELD_RIGHT = 10;
	
	private static final int MARGIN_FROM_FIELD_LEFT = 10;
	
    private Text sendField = null;

    private ControlPanelReplyBox replyBox = null;

    private Button tagBox = null;
    
    private Button typeLock = null;

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ControlPanel.class);

	

	private String sphereId;
	
	private WorkflowConfiguration workflowConfiguration;
    
    public ControlPanel(SupraSphereFrame sF, Composite parentComposite, MessagesPane mP, DockingTopTitle headComposite) {
    	super(sF, parentComposite, mP, headComposite);
    	this.sphereId = this.mP.getSphereStatement().getSystemName();		
        layoutComposite(headComposite);
        moveSendFieldOnBottom();
        setDeliveryComboEnabled(sF.client.getPreferencesChecker().isCanChangeDefaultTypeForSphere());
        logger.info("Control Panel for Messages Pane created");
    }
 
    protected void setDeliveryComboEnabled(boolean enabled){
		this.deliveryCombo.setEnabled(enabled);
	}
    
    public void activateP2PDeliveryCheck(){
    	PreferencesChecker checker = this.sF.client.getPreferencesChecker();
    	String defaultDelivery = checker.getDefaultDeliveryP2PSpheres();
   		if (defaultDelivery != null){
   			for (int i = 0; i < this.deliveryCombo.getItemCount(); i++){
   				if (defaultDelivery.equals(this.deliveryCombo.getItem(i))){
   					this.deliveryCombo.select(i);
   					return;
   				}
   			}
   		} else {
   			String defaultName = DeliveryFactory.INSTANCE.getDefaultDeliveryDisplayName();
   			int index = this.deliveryCombo.indexOf(defaultName);
   			this.deliveryCombo.select(index);
   		}
    }
    
    protected void createDeliveryCombo(DockingTopTitle headComposite) {
    	
		FormData data;
		this.deliveryCombo = new Combo(this.parent,SWT.DROP_DOWN | SWT.READ_ONLY);

		data = new FormData();
		data.left = new FormAttachment(headComposite);
		data.top = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,0);
		this.deliveryCombo.setLayoutData(data);
		this.workflowConfiguration = getWorkflowConfiguration();
		for (String deliveryDisplayName : this.workflowConfiguration.getEnabledDisplayNames() ) {
			this.deliveryCombo.add(deliveryDisplayName);
		}	
		this.deliveryCombo.setVisible(true);
		
		String defaultString = this.workflowConfiguration.getDefaultDelivery().getDisplayName();
		this.deliveryCombo.select(this.deliveryCombo.indexOf(defaultString));
	}
    
    /**
	 * @return
	 */
	private WorkflowConfiguration getWorkflowConfiguration() {
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId );
		return preferences.getWorkflowConfiguration();
	}

	protected void layoutComposite(DockingTopTitle headComposite) {
    	FormData data = null;
      	
        this.createDeliveryCombo(headComposite);            
        this.createSendField();        
        this.createToolBar();    
        
        data = new FormData();
        data.left = new FormAttachment(this.deliveryCombo, MARGIN_FROM_FIELD_LEFT);
        data.top = new FormAttachment(0,0);
        data.right = new FormAttachment(this.buttonComposite);
        data.bottom = new FormAttachment(0,0);
        this.sendField.setLayoutData(data);
        
        this.parent.setVisible(true);
        this.parent.layout();
    }

	protected void createToolBar() {
		FormData data;
		this.buttonComposite = new Composite(this.parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.makeColumnsEqualWidth = false/*true*/;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = MARGIN_FROM_FIELD_RIGHT;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.numColumns = 5;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;
		this.buttonComposite.setLayout(layout);
		
        data = new FormData();
        data.right = new FormAttachment(100,0);
        data.top = new FormAttachment(0,0);
        data.bottom = new FormAttachment(100,0);
        
        this.buttonComposite.setLayoutData(data);
        
        createReplyBoxItem();
        createTagBoxItem();
        createCreateLabel();
        createDropDownItem();
        createTypeLockItem();
	}

	private void createSendField() {
		this.sendField = new Text(this.parent,SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.BORDER);

        this.sendField.setEditable(true);
        this.sendField.setVisible(true);
        this.sendField.setForeground(new Color(Display.getDefault(), 0,0,0));
    
        if (this.mP.getInputListener() != null) {
            this.sendField.addKeyListener(
                    new ControlPanelInputTextPaneListener(this.mP.sF,this.mP));
        }

        SendFieldModifyListener sendFieldModifyAndKeyPressedListener = new SendFieldModifyListener(this.mP.sF, this.mP );
        this.sendField.addKeyListener(sendFieldModifyAndKeyPressedListener);
        this.sendField.addModifyListener(sendFieldModifyAndKeyPressedListener );
        this.sendField.addMouseListener(new RefreshPeopleListListener(this.mP,true));
        
	}

    /**
     * @param toolBar
     */
    private void createReplyBoxItem() {
        this.replyBox = new ControlPanelReplyBox(this);
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.BEGINNING;
        data.widthHint = componentReplyPreferedWidth;
        this.replyBox.setLayoutData(data);
        this.replyBox.setText(bundle.getString(REPLY));
        this.replyBox.setSelection(false);
        this.replyBox.setVisible(true);
        this.replyBox.addSelectionListener(new ControlCompositeReplyBoxMouseListener(this));
        this.replyBox.setEnabled(false);
    }

    /**
     * @param toolBar
     */
    private void createTagBoxItem() {
        this.tagBox = new Button(this.buttonComposite, SWT.CHECK);
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.BEGINNING;
        data.widthHint = componentTagPreferedWidth;
        this.tagBox.setLayoutData(data);
        this.tagBox.setText(ControlPanel.bundle.getString(TAG));
        this.tagBox.setSelection(false);
        this.tagBox.setVisible(true);
        this.tagBox.addSelectionListener(new ControlCompositeTagBoxMouseListener(this));
        this.tagBox.setEnabled(false);
    }
    
    private void createTypeLockItem() {
    	this.typeLock = new Button(this.buttonComposite, SWT.TOGGLE);
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.verticalAlignment = GridData.CENTER;
        data.horizontalAlignment = GridData.BEGINNING;
        data.heightHint = 25;//35;
        data.widthHint = 40;//27;
        this.typeLock.setLayoutData(data);
        this.typeLock.setToolTipText(bundle.getString(TYPE_LOCK));
        this.typeLock.setSelection(false);
        this.typeLock.setVisible(true);
        this.typeLock.setEnabled(true);
        
        try {
        	Image lockImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SCROLL_LOCK_ICON).openStream());
        	this.typeLock.setImage(lockImage);
        } catch (IOException ex) {
        	logger.error(ex);
        }
    }

    public ControlPanelReplyBox getReplyBox() {
        return this.replyBox;
    }

    public Button getTagBox() {
        return this.tagBox;
    }
   
    public String getTextOfSendField(){
    	return UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				String text = ControlPanel.this.sendField.getText();
		        text = text != null ? text.replaceAll("[\\n\\r]","") : "";
		        return text;
			}
    	});
    }
    
    public void setTextToTextField(final String text){
    	UiUtils.swtInvoke( new Runnable() {
			public void run() {
				ControlPanel.this.sendField.setText(text);
			}    		
    	});
    }
    

    
    public void setIsTagSelected(){
        if (this.tagBox.getSelection()) {
//        	if (isTypeLocked()){
//        		setTypeLocked(false);
//        	}
            this.dropDownCreateItem.selectActiveAction(CreateKeywordsAction.KEYWORD_TITLE);
            this.mP.getControlPanelDocking().setFocusToTextField();
            if(this.replyBox.getSelection()) {
            	if (logger.isDebugEnabled()) {
					logger.debug("set false selection");
				}
            	this.replyBox.setSelection(false);
            }       
        } else {
            this.dropDownCreateItem.selectActiveAction(this.getPreviousType());
            logger.info("new drop-down type : "+this.getPreviousType());
        }        
        this.layout();
    }

    
    /* (non-Javadoc)
	 * @see ss.client.ui.AbstractControlPanel#setFocusToSendField()
	 */
	@Override
	public void setFocusToSendField(){
		Shell activeShell = SDisplay.display.get().getActiveShell();
		if(activeShell == null) {
			return;
		}
		boolean isShellActive = activeShell.equals(SupraSphereFrame.INSTANCE.getShell());
		if(!isShellActive) {
			return;
		}
		if (this.sendField.getEditable()) {
			if (logger.isDebugEnabled()){
				logger.debug("Focus to send field setted");
			}
            this.sendField.setFocus();
		}
    }
    
	public boolean isFocusControlSendField(){
		final boolean isFocusControl = !this.sendField.isDisposed() && this.sendField.isFocusControl();
		if (logger.isDebugEnabled()){
			logger.debug("Is Focus control Send field checked, returning " + isFocusControl);
		}
        return isFocusControl;
    }

    public Text getSendField() {	
	return this.sendField;
    }

    public MessagesPane getMP() {
	return this.mP;
    }
    
    public void moveSendFieldOnBottom(){
    	this.parent.layout();
        FormData data;
        if (this.parent.getSize().y > separation*(30)){//this.deliveryCombo.getSize().y)){
        	logger.debug(getHighestControl());
            data = new FormData();
            data.left = new FormAttachment(0,0);
            data.right = new FormAttachment(100,0);
            data.bottom = new FormAttachment(100,0);
            data.top = new FormAttachment(getHighestControl());
            this.sendField.setLayoutData(data);
            
            data = new FormData();
            data.left = new FormAttachment(0,0);
            data.top = new FormAttachment(0,0);
            data.bottom = new FormAttachment(this.sendField);
            data.right = new FormAttachment(this.deliveryCombo,-10);
            this.getMP().getControlPanelDocking().getHeadComposite().setLayoutData(data);
            
            data = new FormData();
    		data.top = new FormAttachment(0,4);
    		data.right = new FormAttachment(this.buttonComposite);
    		this.deliveryCombo.setLayoutData(data);
            
            data = new FormData();
            data.top = new FormAttachment(0,0);
            data.right = new FormAttachment(100, 0);
            this.buttonComposite.setLayoutData(data);
        } else {
        	data = new FormData();
            data.right = new FormAttachment(100,0);
            data.top = new FormAttachment(0,0);
            data.bottom = new FormAttachment(100,0);
            this.buttonComposite.setLayoutData(data);
            
            data = new FormData();
            data.left = new FormAttachment(0,0);
            data.top = new FormAttachment(0,0);
            data.bottom = new FormAttachment(100, 0);
            this.getMP().getControlPanelDocking().getHeadComposite().setLayoutData(data);
            
            data = new FormData();
            data.left = new FormAttachment(this.deliveryCombo,MARGIN_FROM_FIELD_LEFT);
            data.top = new FormAttachment(0,0);
            data.right = new FormAttachment(this.buttonComposite);
            data.bottom = new FormAttachment(100,0);
            this.sendField.setLayoutData(data);
            
            int vSpace = (this.parent.getSize().y-this.deliveryCombo.getSize().y)/2;
    		data = new FormData();
    		data.left = new FormAttachment(this.getMP().getControlPanelDocking().getHeadComposite(), 10);
    		data.top = new FormAttachment(0,vSpace);
    		data.bottom = new FormAttachment(100,0);
    		this.deliveryCombo.setLayoutData(data);
        }
    //    this.parent.layout();
    }
    
    /**
	 * @return
	 */
	private Control getHighestControl() {
		int hDelivery = getDelivery().getSize().y;
		int hButton = getButtonComposite().getSize().y;
		int hHead = getMP().getControlPanelDocking().getHeadComposite().getSize().y;
		
		logger.debug(hDelivery);
		logger.debug(hButton);
		logger.debug(hHead);
		
		int max = Math.max(Math.max(hDelivery, hHead), hButton);
		if(max == hDelivery) {
			return getDelivery();
		} else if(max == hButton) {
			return getButtonComposite();
		}
		return getMP().getControlPanelDocking().getHeadComposite();
	}

	public void layoutControlPanel(){
        Point p = this.parent.getSize();
        this.parent.setSize(p.x + 3, p.y + 3);
        this.parent.setSize(p);
        /*this.toolBar.pack();
        this.toolBar.layout();
        this.parent.layout();*/
    }
    
    public AbstractDelivery getDeliveryType() {
    	final WorkflowConfiguration workflowConfiguration = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId).getWorkflowConfiguration();
    	int index = this.deliveryCombo.getSelectionIndex();
    	String displayName =  index >= 0 ? this.deliveryCombo.getItem(index) : null;  
    	return workflowConfiguration.getDeliveryByDisplayName( displayName );
    }
    
    @SuppressWarnings("unchecked")
	@Override
    protected void setDefaultDelivery(final Document sphereDefinition, final Hashtable session, String default_delivery, Vector<String> types) {
		if (sphereDefinition != null) {
			this.deliveryCombo.select(this.deliveryCombo.indexOf(this.workflowConfiguration.getDefaultDelivery().getDisplayName()));
		} else {
		    String name = this.sF.getDC((String) session.get("sphereURL"))
		            .getVerifyAuth().getDisplayName(
		                    (String) session.get("sphere_id"));
		    if (name.equals((String) session.get("real_name"))) {
		        this.deliveryCombo.select(0);
		        logger.info("setting delivery to one");

		    } else {
		        this.deliveryCombo.select(1);
		    }
		}
	}

	/**
	 * @return
	 */
	public boolean isReplyChecked() {
		return UiUtils.swtEvaluate( new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ControlPanelReplyBox replyBox = getReplyBox();
				return replyBox != null ? replyBox.getSelection() : false;
			}
		});
	}
    
	void setReplyChecked( final boolean value ) {
		UiUtils.swtInvoke( new Runnable() {
			public void run() {
				final ControlPanelReplyBox replyBox = getReplyBox();
				if ( replyBox == null ) {
					return;
				}
				replyBox.setSelection(value);
				if (value) {
					final Button tagBox = getTagBox();
			    	if( tagBox != null && tagBox.getSelection()) {
			    		tagBox.setSelection(false);
			    		setIsTagSelected();
			    	}
				}
			}
		});		
	}

	/**
	 * @param value
	 */
	void setTagChecked(final boolean value) {
		UiUtils.swtInvoke( new Runnable() {
			public void run() {
				getTagBox().setSelection(value);
				setIsTagSelected();
			}
		} );
	}

	/**
	 * @return
	 */
	public boolean isTagChecked() {
		return UiUtils.swtEvaluate( new Callable<Boolean>() {
			public Boolean call() throws Exception {
				Button tagBox = getTagBox();
				return tagBox != null ? tagBox.getSelection() : false;
			}
		});
	}
	
	public boolean isTypeLocked() {
		try {
			return this.typeLock.getSelection();
		} catch (NullPointerException ex) {
			return false;
		}
	}
	
	public void setTypeLocked(boolean locked) {
		if(this.typeLock==null) {
			return;
		}
		this.typeLock.setSelection(locked);
	}
	
	public Composite getButtonComposite() {
		return this.buttonComposite;
	}
}