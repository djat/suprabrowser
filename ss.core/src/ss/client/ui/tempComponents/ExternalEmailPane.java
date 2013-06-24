/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.docking.EmailControlPanelDocking;
import ss.client.ui.docking.SBrowserDocking;
import ss.client.ui.docking.SupraDockingManager;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.util.StringProcessor;
import swtdock.PartDragDrop;

/**
 * @author zobo
 *
 */
public class ExternalEmailPane extends AbstractShowablePane {

    //private SupraSphereFrame sF;
	
	private ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_EXTERNALEMAILPANE);
    
    private Logger logger = SSLogger.getLogger(this.getClass());
    
    private SBrowserDocking browserDocking;

    private SupraDockingManager dockingManager;
    
    private EmailControlPanelDocking controlPanel;
    
    private SupraSphereFrame sF;
    
    private ExternalEmailStatement email;
    
    private Hashtable session;
    
    private MessagesPane mp;
    
    private static final String SENDER = "EXTERNALEMAILPANE.SENDER";
    private static final String RECEIVER = "EXTERNALEMAILPANE.RECEIVER";
    private static final String CC = "EXTERNALEMAILPANE.CC";
    private static final String BCC = "EXTERNALEMAILPANE.BCC";
    private static final String SUBJECT = "EXTERNALEMAILPANE.SUBJECT";
    private static final String BODY_EMAIL = "EXTERNALEMAILPANE.BODY_OF_THE_EMAIL";

    public ExternalEmailPane(Composite parent, ExternalEmailStatement email, SupraSphereFrame sF, Hashtable session,  MessagesPane mp) {
        super(parent, SWT.NONE);
        this.sF = sF;
        this.email = email;
        this.session = session;
        this.mp = mp;
        initGUI();
        showEmail(this.email);
        this.logger.info("Email Pane created");
    }

    private void initGUI() {
        this.dockingManager = new SupraDockingManager(this,SWT.CLOSE);
        
        
        this.controlPanel = new EmailControlPanelDocking(this);
        this.dockingManager.addPart(this.controlPanel);
        
        this.browserDocking = new SBrowserDocking(this.dockingManager);
        this.browserDocking.setMP(this.mp);
        this.dockingManager.addPart(this.browserDocking);
     
        this.dockingManager.movePart(this.controlPanel, PartDragDrop.TOP, this.browserDocking, (float)0.01);
        
        this.controlPanel.getContent().activate(this.mp, this.email);
    }

    private void showEmail(ExternalEmailStatement email){
        this.email = email;
        
        String text = "";

        text += this.bundle.getString(SENDER) + StringProcessor.toHTMLView(email.getGiver()) + "<br>";
        String str = StringProcessor.toHTMLView(email.getReciever());
        if (str != null)
            text += this.bundle.getString(RECEIVER) + str + "<br>";
        str = StringProcessor.toHTMLView(email.getCcrecievers());
        if (str != null)
            text += this.bundle.getString(CC) + str + "<br>";
        str = StringProcessor.toHTMLView(email.getBccrecievers());
        if (str != null)
            text += this.bundle.getString(BCC) + str + "<br>";
        text += this.bundle.getString(SUBJECT) + email.getSubject() + "<br><br>";
        text += this.bundle.getString(BODY_EMAIL)+"<br>" + email.getOrigBody();
        
        String subject = this.email.getSubject();
        this.logger.info("Email loading with subject: "+subject);
        
        PreviewHtmlTextCreator creator = new PreviewHtmlTextCreator(this.mp); 
        /*if (VariousUtils.isTextHTML(text)) {
            creator.setText(text);
        } else {*/
        creator.addText(text);
        /*String body = email.getBody();
        body = body.replaceAll("\n", "<br>");*/

        this.browserDocking.setText(subject, creator.getText());
    }

    /**
     * @return the email
     */
    public ExternalEmailStatement getEmail() {
        return this.email;
    }
    
    public SBrowserDocking getBrowserDocking() {
    	return this.browserDocking;
    }
    
    public EmailControlPanelDocking getControlPanel() {
    	return this.controlPanel;
    }
    
    public SupraSphereFrame getSF() {
    	return this.sF;
    }
    
    public SupraDockingManager getDockingManager() {
    	return this.dockingManager;
    }
    
    public Hashtable getSession() {
    	return this.session;
    }
    
    public boolean hasComment() {
    	if (this.mp==null || this.email==null) {
    		return false;
    	}

    	List<Document> childrenDocs = this.mp.getMessagesTree().getChildrenFor(this.email.getMessageId());
    	for(Document doc : childrenDocs) {
    		if(Statement.wrap(doc).isComment()) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
