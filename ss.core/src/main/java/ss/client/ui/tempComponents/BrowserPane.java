/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.List;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.BrowserControlPanelDocking;
import ss.client.ui.docking.SBrowserDocking;
import ss.client.ui.docking.SupraDockingManager;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.Statement;
import swtdock.PartDragDrop;

/**
 * @author zobo
 *
 */
public class BrowserPane extends AbstractShowablePane {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BrowserPane.class);
	
    private SupraDockingManager dockingManager;
    
    private BrowserControlPanelDocking controlPanel;
    
    private SBrowserDocking browserDocking;
    
    private SupraSphereFrame sF;
    
    private MessagesPane mP;
    
    private String startURL;
    
    private BookmarkStatement bookmark;

    /**
     * @param arg0
     * @param arg1
     */
    public BrowserPane(Composite arg0, SupraSphereFrame sF, MessagesPane mP, BookmarkStatement bookmark) {
        super(arg0, SWT.NONE);
        this.sF = sF;
        this.mP = mP;
        this.bookmark = bookmark;
        initGUI();
    }
    
    private void initGUI(){
        this.dockingManager = new SupraDockingManager(this,SWT.CLOSE);
        
        
        this.controlPanel = new BrowserControlPanelDocking(this);
        this.dockingManager.addPart(this.controlPanel);
        
        this.browserDocking = new SBrowserDocking(this.dockingManager);
        this.browserDocking.setMP(this.mP);
        this.dockingManager.addPart(this.browserDocking);
        
        this.dockingManager.movePart(this.controlPanel, PartDragDrop.TOP, this.browserDocking, (float)0.01);
        
        this.controlPanel.getContent().activate(this.browserDocking.getContent());
    }
    
    public SupraBrowser getBrowser(){
    	logger.debug("call browser");
        return this.browserDocking.getContent();
    }
    
    public BrowserControlPanel getControlPanel() {
    	return this.controlPanel.getContent();
    }
    
    public SBrowserDocking getBrowserDocking() {
    	return this.browserDocking;
    }
    
    public MessagesPane getMessagesPane() {
    	return this.mP;
    }
    
    public void setStartURL(String url) {
    	this.startURL = url;
    }
    
    public String getStartURL() {
    	return this.startURL;
    }
    
    public BookmarkStatement getBookmark() {
    	return this.bookmark;
    }
    
    public SupraDockingManager getDockingManager() {
    	return this.dockingManager;
    }
    
    public SupraSphereFrame getSF() {
    	return this.sF;
    }
    
    public boolean hasComment() {
    	if (this.mP==null || this.bookmark==null) {
    		return false;
    	}

    	List<Document> childrenDocs = this.mP.getMessagesTree().getChildrenFor(this.bookmark.getMessageId());
    	for(Document doc : childrenDocs) {
    		if(Statement.wrap(doc).isComment()) {
    			return true;
    		}
    	}
    	
    	return false;
    }

}
