/**
 * 
 */
package ss.client.ui.docking;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.browser.SSBrowser;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.MultiCompositeContainer;
import ss.client.ui.viewers.comment.CommentApplicationWindow;
import ss.client.ui.widgets.SearchPane;
import swtdock.ILayoutPart;
import swtdock.PartDragDrop;

/**
 * @author zobo
 *
 */
public class SBrowserDocking extends AbstractDockingComponent implements ISearchable{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SBrowserDocking.class);
	
    private int titleLength = 16;
    private static final String BROWSER = "SBROWSERDOCKING.BROWSER";
    private SupraBrowser browser;
    private String browserTitle;
    private int minHeight = 0;
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_DOCKING_SBROWSERDOCKING);
    
    private MultiCompositeContainer container;
    private SearchPane searchPane;
    private MessagesPane mP;
    private boolean isSupraSearchPane;
    

    public SBrowserDocking(SupraDockingManager dm) {
        this(dm, false);
    }
    
    /**
     * @param dm
     */
    public SBrowserDocking(SupraDockingManager dm, boolean isSupraSearchPane) {
        super(dm);
        this.browserTitle = this.bundle.getString(BROWSER);
        this.isSupraSearchPane = isSupraSearchPane;
    }

    @Override
    public String getName() {
        return this.browserTitle;
    }

    @Override
    public void createContent(Composite parent) {
        parent.setLayout(new FillLayout());
        
        this.container = new MultiCompositeContainer(parent);

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.container.setLayout(layout);
        
		if(!this.isSupraSearchPane) {
			this.browser = new SupraBrowser(this.container, SWT.MOZILLA | SWT.BORDER, true);
		} else {
			this.browser = new SSBrowser(this.container, SWT.MOZILLA | SWT.BORDER, true);
		}
        GridData data = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        this.browser.setLayoutData(data);
        
        if (this.mP!=null) {
        this.browser.setMP(this.mP);
        }
        this.browser.setShowAllCommentedPlaces(false);
        
        this.container.addComposite(this.browser);
        
        this.browser.setVisible(true);
    }
    
    public void loadURL(String title, String URL){
        if (title.length()>this.titleLength) {
            title = title.substring(0,this.titleLength-1);
            title = title+"...";
            this.browserTitle = title;
        }
        this.browser.setUrl(URL);
    }
    
    public void setText(String title, String body){
        this.browserTitle = title;
        setLabelText("Subject: " + this.browserTitle);
        this.browser.setText(body != null ? body : "no body text...");
    }

    @Override
    public SupraBrowser getContent() {
    	logger.debug("call browser");
        return this.browser;
    }

    @Override
    public int getMinimumWidth() {
        return 0;
    }

    @Override
    public int getMinimumHeight() {
        return this.minHeight ;
    }
    
    public void setMinimumHeight(int minHeight){
        this.minHeight = minHeight;
    }

    /**
     * @return Returns the titleLength.
     */
    public int getTitleLength() {
        return this.titleLength;
    }

    /**
     * @param titleLength The titleLength to set.
     */
    public void setTitleLength(int titleLength) {
        this.titleLength = titleLength;
    }

    @Override
    public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
        if (target == null){
            if ((direction == PartDragDrop.SHELL_RIGHT)||(direction == PartDragDrop.SHELL_LEFT))
                return false;
        } else if (!super.checkPossibilityOfDocking(direction,target))
            return false;
        if ((direction == PartDragDrop.RIGHT)||(direction == PartDragDrop.LEFT))
            return false;
        return true;
    }

    @Override
    public boolean checkIfCanDockOn(int direction) {
        if ((direction == PartDragDrop.RIGHT)||(direction == PartDragDrop.LEFT))
            return false;
        return true;
    }

    public void setEmailContextMenu() {
    	if(this.browser==null || this.browser.isDisposed()) {
    		logger.error("browser is null or disposed");
    	}
    	else {
	        this.browser.setContextMenuShow(false);
	    }
    }
    
	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}
	
	public MultiCompositeContainer getMultiContainer() {
		return this.container;
	}
	
	public void addSearchPane() {
		// this.container.setLayout(new GridLayout(1, true));

		this.searchPane = new SearchPane(this);
		this.searchPane.setVisible(true);
		this.container.redraw();
		this.container.layout();
	}

	public void removeSearchPane() {
		this.container.removeComposite(this.searchPane);
		this.searchPane.dispose();
		this.container.redraw();
		this.container.layout();
		this.searchPane = null;
		getContent().setFocus();
	}
	
	public boolean containsSearchPane() {
		return this.searchPane != null;
	}

	public SearchPane getSearchPane() {
		return this.searchPane;
	}
	
	public SupraBrowser getBrowser() {
		logger.debug("browser call");
		return this.getContent();
	}
	
	public void setMP(MessagesPane mP) {
		this.mP = mP;
	}
	
	public MessagesPane getMessagesPane() {
		return this.mP;
	}
	
	public void showCommentWindow() {
    	this.getBrowser().findCommentedPlace(this.mP.getViewComment(), true);
    	
    	CommentApplicationWindow caw = new CommentApplicationWindow(this.mP, this.mP.getViewComment());
		this.mP.sF.getCommentWindowController().addCommentWindow(caw);
		caw.setBlockOnOpen(true);
    	caw.open();
    }

	public void toggleSearchPane() {
		if(this.searchPane == null) {
			addSearchPane();
		} else {
			removeSearchPane();
		}
	}
}
