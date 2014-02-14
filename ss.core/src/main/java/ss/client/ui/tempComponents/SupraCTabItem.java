/**
 * 
 */
package ss.client.ui.tempComponents;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SimpleBrowserDataSource;
import ss.client.ui.root.SupraTab;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.common.debug.DebugUtils;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.global.SSLogger;
import ss.rss.RSSParser;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
public class SupraCTabItem extends CTabItem {

	public static int CONTENT_NONE = 0;
    
    public static int CONTENT_MESSAGES_PANE = 1;
    
    public static int CONTENT_BROWSER = 2;
    
    public static int CONTENT_EMAIL = 3;
    
    public static int CONTENT_SUPRA_SEARCH = 4;
    
    public static int CONTENT_ROOT_PANE = 5;
    
    private SupraTab root = null;

    private MessagesPane pane = null;

    private SupraCTabTable table;
    
    private int content = CONTENT_NONE;
    
    private BrowserPane browserPane;
    
    private boolean isBrowserPane = false;

    private ExternalEmailPane externalEmail;
    
    private boolean isBrowserLocationListening = false;
    
    private SupraSearchPane ssp = null;
    
    private static final Logger logger = SSLogger.getLogger(SupraCTabItem.class);
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SUPRACTABITEM);
    
    private final static String SUPRASPHERE = "SUPRACTABITEM.SUPRASPHERE";
    
    private static final String NO_TITLE = "SUPRACTABITEM.NO_TITLE";
   
    public SupraCTabItem( final CTabFolder parent, final SupraCTabTable table ) {
        super(parent, SWT.CENTER);
        this.table = table;
    }

    public SupraCTabItem(CTabFolder parent, int index, SupraCTabTable table) {
        super(parent, SWT.CENTER | SWT.CLOSE, index);
        this.table = table;
    }

    
    /**
     * @return Returns the panel.
     */
    public MessagesPane getMessagesPane() {
        return this.pane;
    }
    
    public BrowserPane getBrowserPane() {
    	return this.browserPane;
    }
    
    public ExternalEmailPane getEmailPane() {
    	return this.externalEmail;
    }
    
    public Composite getPane() {
    	if(this.browserPane!=null)
    		return this.browserPane;
    	else if(this.externalEmail!=null)
    		return this.externalEmail;
    	else return this.pane;
    }

    public void setMessagesPane(MessagesPane pane) {

        this.pane = pane;
        setControl(this.pane);
        this.content = CONTENT_MESSAGES_PANE;

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        this.pane.setLayoutData(layoutData);

        this.table.redraw();
        //this.table.setSelection(this);
        checkMenuEnabled();
        //this.table.getSelectionListener().widgetSelected(null);
    }
    
    public void setRootPane(SupraTab root){
    	this.root = root;
    	setControl(this.root.getControl());
    	if (root.getDesiredTitle() != null){
    		setText(root.getDesiredTitle());
    	} else {
    		setText(this.bundle.getString(NO_TITLE));
    	}
    	GridData layoutData = new GridData();
    	layoutData.grabExcessHorizontalSpace = true;
    	layoutData.grabExcessVerticalSpace = true;
    	layoutData.verticalAlignment = GridData.FILL;
    	layoutData.horizontalAlignment = GridData.FILL;
    	this.root.getControl().setLayoutData(layoutData);
    	this.table.redraw();
    	this.table.setSelection(this);


    	try {
    		Image rootImage = null;
    		rootImage = new Image(Display.getDefault(), getClass()
    				.getResource(ImagesPaths.SPHERE).openStream());
    		setImage(rootImage);
    	} catch (IOException ex) {
    		logger.error("cannot create sphere image", ex);
    	}


    	checkMenuEnabled();
    }
    
    public boolean isRootPane() {
    	return this.root!=null;
    }
    
    private static String getShorterTitle(String title) {
    	if (title != null) {
			if (title.length() > 19) {
				title = title.substring(0, 18);
				title = title + "...";
			}
		}
    	return title;
    }
    
    public void setTitleDelayed(final String passTitle, final BrowserDataSource browserDataSource, final SupraSphereFrame sF, final SupraBrowser browser) {
    	logger.info("Calling title delayed...");
		Thread t = new Thread() {
			public void run() {
				String title = passTitle;
				if (title == null) {
					try {
						title = RSSParser.getTitleFromURL(browserDataSource
								.getURL());
					} catch (Exception e) {
						logger.error("Can't parse RSS", e);
					}
				}
				if (title != null) {
					title = getShorterTitle(title);
				} else {
					title = SupraCTabItem.this.bundle.getString(SUPRASPHERE);
				}
				wrappedSetText(title, sF, browser);
			}
		};
		t.start(); 
    }
    
    public void addBrowserLocationListener(final SupraSphereFrame sF, final String passTitle) {
      this.isBrowserLocationListening = true;
      getMBrowser().addLocationListener(new LocationListener() {
        public void changed(LocationEvent event) {
          BrowserPane browserPane = SupraCTabItem.this.browserPane;
          String newURL = event.location;
          String replacedNew = newURL.replace("/", "");
          String old = browserPane.getStartURL();
          String replacedOld = old.replace("/", "");
          
          browserPane.getControlPanel().getDiscuss()
          	.setEnabled(replacedNew.equals(replacedOld) && browserPane.hasComment());
          browserPane.getControlPanel().getDiscuss().setSelection(false);
          
          setTitleDelayed(passTitle,new SimpleBrowserDataSource(newURL),sF,getMBrowser());
          browserPane.getControlPanel().getAddressField().setText(newURL);
          //setText("Loading...");
          
        }
        public void changing(LocationEvent event) {
        //  browserPane.getControlPanel().getAddressField().setText(event.location);
        }
      });
      
      
    }
    public boolean isBrowserLocationListening() {
      return this.isBrowserLocationListening;
    }
    
    public void wrappedSetText (final String passTitle, SupraSphereFrame sF, final SupraBrowser browser) {

	Thread t = new Thread() {
	    public void run() {
		if(!SupraCTabItem.this.isDisposed())
		{
		    setText(passTitle);
        browser.setFocus();
		}
	    }
	};
	UiUtils.swtBeginInvoke(t);

    }
    
    public void setBrowser(Hashtable session, final String passTitle, final BrowserDataSource browserDataSource, final SupraSphereFrame sF, final boolean addListener, final BookmarkStatement bookmark){
      setUpBrowser(sF, bookmark);
      final SupraBrowser browser = this.browserPane.getBrowser();
        try {
            Image image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.BROWSER_ICON).openStream());
            this.setImage(image);
        } catch (IOException e) {
        	logger.error("Error with loading image for BROWSER_ICON", e);
        }
    	
    	this.setIsBrowserPane(true);
    	
    	if (passTitle==null) {
    		logger.warn("Pass title was null....");
    		
    		setText("Loading...");
    		
    		setTitleDelayed(passTitle,browserDataSource,sF,browser);
    		
    	}
    	else {
    		
    		setText(getShorterTitle(passTitle));
    	}
        this.browserPane.setStartURL(browserDataSource.getURL());  
        browserDataSource.setupBrowser(browser);
        browser.getMozillaBrowserController().setCurrentSession(session);
       // this.browserPane.getControlPanel().getAddressField().setText(browserDataSource.getURL());

        if (addListener) { // Need to exclude the case where adding a blank tab
          this.addBrowserLocationListener(sF, passTitle);
        }

        browser.forceFocus();
        browser.setFocus();

        //return this.browserPane.getBrowser();
        /*Composite comp = new Composite(this.getParent(), SWT.NONE);

        this.setControl(comp);
        final MozillaBrowser mb = new MozillaBrowser(comp, SWT.EMBEDDED);
        mb.setSupraSphereFrame(sF);
        mb.getMozillaBrowserController().setCurrentSession(session);
        this.mBrowser = mb;
        this.content = CONTENT_BROWSER;

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        mb.setLayoutData(layoutData);
        comp.setLayout(new GridLayout());

        mb.setUrl(URL);
        
        mb.setVisible(true);
        
 
        return this.mBrowser;*/
    }
    
    private void setUpBrowser(SupraSphereFrame sF, BookmarkStatement bookmark) {
    	//TODO: fix messages pane usage
    	final MessagesPane messagesPane = sF.tabbedPane.getSelectedMessagesPane();
        this.browserPane = new BrowserPane(this.getParent(), sF, messagesPane, bookmark);
        this.content = CONTENT_BROWSER;
        this.setControl(this.browserPane);
    }
    
    private void setUpSupraSearchResult(SupraSphereFrame sF,BrowserDataSource source){
    	//TODO: fix messages pane usage 
    	final MessagesPane messagesPane = sF.tabbedPane.getSelectedMessagesPane();
        this.ssp = new SupraSearchPane(this.getParent(), sF, messagesPane ,source);
        this.content = CONTENT_BROWSER;
        this.setControl(this.ssp);
    }

    /**
     * @return Returns the content.
     */
    public int getContent() {
        return this.content;
    }

    /**
     * @return Returns the mBrowser.
     */
    public SupraBrowser getMBrowser() {
    	logger.debug("call browser");
    	if(this.browserPane != null) {
    		return this.browserPane.getBrowser();
    	} else if ( this.ssp != null ){
    		return this.ssp.getBrowser();
    	} else if (this.externalEmail != null ) {
    		return this.externalEmail.getBrowserDocking().getBrowser();
    	} else if (this.pane != null) {
    		return this.pane.getSmallBrowser();
    	}
    	else {
    		return null;
    	}
    }
    
    public boolean isBrowserPane() {
    	return this.isBrowserPane;
    }
    
    private void setIsBrowserPane(boolean value) {
    	this.isBrowserPane = value;
    }

    public void showEmail(final ExternalEmailStatement email, final SupraSphereFrame sF, final Hashtable session, MessagesPane mp) {
        try {
            Image image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.EMAIL_OUT_ICON).openStream());
            this.setImage(image);
        } catch (IOException e) {
        }
        setText(getShorterTitle(email.getGiver()));
        this.content = CONTENT_EMAIL;
        this.externalEmail = new ExternalEmailPane(this.getParent(), email, sF, session, mp);
        this.setControl(this.externalEmail);
    }
    
    public void setTabProperties(String title, Image image, MessagesPane messPane) {
    	if (title != null)
    		this.setText(title);
    	if (image != null) {
    		this.setImage(image);
    	}
    	this.setMessagesPane(messPane);
    }
    
    public String getSphereId() {
    	return (String)this.pane.getRawSession().get("sphere_id");
    }
    
    public void select()
    {
    	this.table.setSelection(this);
    	checkMenuEnabled();    	
    }
    
    public void checkMenuEnabled() {
    	getSupraSphereFrame().getMenuBar().checkAddRemoveEnabled();
    }

	/**
	 * @param session
	 * @param title
	 * @param browserDataSource
	 * @param sf
	 * @param addListener
	 */
	public void setSupraSearchResult(Hashtable session, final String title, BrowserDataSource browserDataSource, SupraSphereFrame sF) {
		setUpSupraSearchResult(sF,browserDataSource);
	      final SupraBrowser browser = this.ssp.getBrowser();
	        try {
	            Image image = new Image(Display.getDefault(),getClass().getResource(
	            ImagesPaths.BROWSER_ICON).openStream());
	            this.setImage(image);
	        } catch (IOException e) {
	        }
	    	
	    	this.setIsBrowserPane(true);
	    	
	    	if (title==null) {
	    		logger.warn("Pass title was null....");
	    		setTitleDelayed(title,browserDataSource,sF,browser);
	    	}
	    	else {
	    		
	    		setText(getShorterTitle(title));
	    	}
	        browserDataSource.setupBrowser(browser);
	        browser.getMozillaBrowserController().setCurrentSession(session);
	        browser.forceFocus();
	        browser.setFocus();
	}

	/**
	 * @return
	 */
	public SupraSearchPane getSupraSearchPane() {
		return this.ssp;
	}

	/**
	 * @param toolTip
	 */
	public void safeSetToolTipText(final String toolTip) {
		UiUtils.swtBeginInvoke( new Runnable() {
			public void run() {
				setToolTipText(toolTip);
			}
		});
		
	}

	/**
	 * 
	 */
	public void safeClose() {
		final SupraSphereFrame supraSphereFrame = getSupraSphereFrame();
		final SupraCTabTable parent = getSupraParent();
		final MessagesPane messagePane = (MessagesPane) getMessagesPane();
		final SupraBrowser browser = getMBrowser();
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (messagePane != null){
					final String str = new String(messagePane.getSystemName());
					messagePane.closeBrowser();
					if (totalyRemoved(str)) {
						SphereOpenManager.INSTANCE.unregister(messagePane);
						messagePane.closeClientPresence();
					}
					try {
						final String title = getText();
						supraSphereFrame.removeMessagesPane(title, messagePane.getRawSession() );
					} catch (Throwable ex) {
						logger.error("Error removing messages pane", ex);
					}
				} else if ( browser != null && !browser.isDisposed()) {
					browser.dispose();
				}
				parent.remove( SupraCTabItem.this );
				supraSphereFrame.getMenuBar().checkAddRemoveEnabled();
			}
		});			
	}
	
	private boolean totalyRemoved(String s) {
		int counter = 0;
		for (MessagesPane pane : this.table.getMessagesPanes()){
			if(StringUtils.equals(s, pane.getSystemName())) {
				counter++;
			}
		}
		if(counter>1) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	private SupraCTabTable getSupraParent() {
		return (SupraCTabTable) getParent();
	}

	/**
	 * @return
	 */
	private SupraSphereFrame getSupraSphereFrame() {
		return SupraSphereFrame.INSTANCE;
	}

	/**
	 * @return
	 */
	public boolean isClosable() {
		final MessagesPane pane = getMessagesPane();
		return pane != null && !pane.isRootView();
	}

	/**
	 * 
	 */
	public void resetMark() {
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				setFont(getSupraParent().getFont());
			}
		});
	}

	/**
	 * 
	 */
	public void mark() {
		if (logger.isDebugEnabled()) {
			logger.debug("TODO: message here"+DebugUtils.getCurrentStackTrace());
		}
		UiUtils.swtInvoke( new Runnable() {
			public void run() {
				if ((SupraCTabItem)(getSupraParent().getSelection()) != SupraCTabItem.this){
					Font markFont = getSupraParent().getMarkFont();
					setFont(markFont);	
				}
			}
		});
	}

}
