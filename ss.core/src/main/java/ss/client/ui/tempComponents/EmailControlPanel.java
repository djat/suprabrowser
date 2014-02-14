/**
 * 
 */
package ss.client.ui.tempComponents;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.Listeners.emailbrowser.EmailControlPanelComposeListener;
import ss.client.ui.Listeners.emailbrowser.EmailControlPanelDiscussListener;
import ss.client.ui.Listeners.emailbrowser.EmailControlPanelForwardListener;
import ss.client.ui.Listeners.emailbrowser.EmailControlPanelReplyListener;
import ss.client.ui.docking.EmailControlPanelDocking;
import ss.client.ui.tempComponents.researchcomponent.ReSearchToolItemComponent;
import ss.domainmodel.ExternalEmailStatement;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class EmailControlPanel extends Composite{
    
    private MessagesPane mp = null;
    
    private Hashtable session;
    
    @SuppressWarnings("unused")
    private static final Logger logger = SSLogger.getLogger(
            EmailControlPanel.class);

    private ToolItem itemForward;

    private ToolItem itemReply;
    
    private ToolItem itemCompose;
    
    private ReSearchToolItemComponent itemResearch;
    
    private ToolItem itemDiscuss;

    private Image imageReply = null;

    private Image imageForward = null;

    private Image imageCompose = null;
    
    private Button discuss = null;
    
    private EmailControlPanelDocking docking = null;
    
    
    private static final String REPLY = "EMAILCONTROLPANEL.REPLY";
    private static final String FORWARD = "EMAILCONTROLPANEL.FORWARD";
    private static final String COMPOSE = "EMAILCONTROLPANEL.COMPOSE";
    private static final String DISCUSS = "EMAILCONTROLPANEL.DISCUSS";
    private static final String RESEARCH = "EMAILCONTROLPANEL.RESEARCH";
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_EMAILCONTROLPANEL);

    
    /**
     * 
     */
    public EmailControlPanel(EmailControlPanelDocking docking, Composite parentComposite) {
        super(parentComposite,SWT.NONE);
        this.docking = docking;
        this.session = docking.getSession();
        initIcons();
        layoutComposite();
        logger.info("Control Panel for sBrowser created");
    }
    
    private void initIcons() {
        try {
            this.imageReply = new Image(Display.getDefault(),getClass().getResource(
                    ImagesPaths.EMAIL_REPLY_ICON).openStream());
            this.imageForward = new Image(Display.getDefault(),getClass().getResource(
                    ImagesPaths.EMAIL_FORWARD_ICON).openStream());
            this.imageCompose = new Image(Display.getDefault(),getClass().getResource(
                    ImagesPaths.EMAIL_COMPOSE_ICON).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void activate(MessagesPane mp, ExternalEmailStatement email){
        if (mp == null)
            return;
        this.mp = mp;
        
        this.itemCompose.addSelectionListener(
                new EmailControlPanelComposeListener(this.mp, this.session));
        this.itemCompose.setEnabled(true);
        
        this.itemForward.addSelectionListener(
                new EmailControlPanelForwardListener(this.mp, email, this.session));
        this.itemForward.setEnabled(true);
        
        this.itemResearch.activate( this.docking.getBrowser() );
        
        this.discuss.setEnabled(this.docking.getEP().hasComment());
        this.discuss.addSelectionListener(new EmailControlPanelDiscussListener(this));
        
        if (email.isInput()){
        	this.itemReply.addSelectionListener(
        			new EmailControlPanelReplyListener(this.mp, email, this.session));
        	this.itemReply.setEnabled(true);
        }
        
        logger.info("Control Panel for EmailBrowser activated");
    }

    private void layoutComposite(){
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;

        GridLayout layout = new GridLayout();
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginTop = 0;
        layout.marginRight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);
        
        ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
        toolBar.setLayoutData(layoutData);
        GridData data = new GridData();
        data.horizontalSpan = 5;
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        toolBar.setLayoutData(data);
        
        this.itemCompose = new ToolItem(toolBar, SWT.PUSH);
        this.itemCompose.setText(this.bundle.getString(COMPOSE));
        this.itemCompose.setImage(this.imageCompose);
        this.itemCompose.setEnabled(false);
        
        this.itemForward = new ToolItem(toolBar, SWT.PUSH);
        this.itemForward.setText(this.bundle.getString(FORWARD));
        this.itemForward.setImage(this.imageForward);
        this.itemForward.setEnabled(false);

        this.itemReply = new ToolItem(toolBar, SWT.PUSH);
        this.itemReply.setText(this.bundle.getString(REPLY));
        this.itemReply.setImage(this.imageReply);
        this.itemReply.setEnabled(false);
        
        this.itemResearch = new ReSearchToolItemComponent( toolBar );
        this.itemResearch.setEnabled(false);
        
        this.itemDiscuss = new ToolItem(toolBar, SWT.SEPARATOR);
        this.discuss = new Button(toolBar, SWT.TOGGLE);
        this.discuss.setText(this.bundle.getString(DISCUSS));
        this.itemDiscuss.setControl(this.discuss);
        this.itemDiscuss.setWidth(68);
        
        toolBar.setVisible(true);
    }
    
    public EmailControlPanelDocking getDocking() {
    	return this.docking;
    }
    
    public Button getDiscuss() {
    	return this.discuss;
    }
}
