/**
 * 
 */
package ss.client.ui.viewers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author roman
 *
 */
public abstract class ContentTypeViewerSWT implements ContentTypeViewable {

	protected Image sphereImage = null;
	
	protected Image warningImage = null;
	
	private static final Logger logger = SSLogger.getLogger(ContentTypeViewerSWT.class);
	
	protected Shell shell = null;
	
	String threshold = null;

    protected Hashtable session = new Hashtable();

    protected MessagesPane mP = null;

    String model_path = null;

    String another = null;

    protected Document viewDoc = null;

    boolean isFillMessage = true;
    
    private static final ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_CONTENTTYPEVIEWER);
    
    public static final String ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW = "CONTENTTYPEVIEWER.ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW";
    public static final String WARNING = "CONTENTTYPEVIEWER.WARNING";
    public static final String YES = "CONTENTTYPEVIEWER.YES";
    public static final String CANCEL = "CONTENTTYPEVIEWER.CANCEL";
   
    
    public ContentTypeViewerSWT() {
    	
    	UiUtils.swtInvoke(new Runnable() {
    		public void run() {
    			ContentTypeViewerSWT.this.shell = new Shell(Display.getDefault(), SWT.CLOSE | SWT.RESIZE);
    			ContentTypeViewerSWT.this.shell.setVisible(false);
    			ContentTypeViewerSWT.this.shell.setSize(getViewerSWTWidth(), getViewerSWTHeight());
    		}
    	});
    	
    	try{
			this.sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
			this.warningImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.WARNING_ICON).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}
    }
    
    public void addShellListener() {
    	if(this.shell!=null) {
    		UiUtils.swtBeginInvoke(new Runnable() {
    			public void run() {   				
    				ContentTypeViewerSWT.this.shell.addShellListener(new ShellAdapter() {
    					private ContentTypeViewerSWT typeWindow = ContentTypeViewerSWT.this;
    					public void shellClosed(ShellEvent se) {
    						if (!this.typeWindow.isFillMessage) {
    							se.doit = false;
    							UserMessageDialogCreator.warningYesCancelButton(getDialogString());
    						}
    					}
    				});
    			}
    		});   		
    	}
    }

	public abstract void giveBodyFocus();

    Document XMLDoc() {
        Document doc = null;
        return doc;
    }

    /**
     * @return Returns the session.
     */
    public Hashtable getSession() {
        return this.session;
    }

    /**
     * @return Returns the mP.
     */
    public MessagesPane getMessagesPane() {
        return this.mP;
    }

    /**
     * @return Returns the viewDoc.
     */
    public Document getViewDoc() {
        return this.viewDoc;
    }

    /**
     * @param viewDoc The viewDoc to set.
     */
    public void setViewDoc(Document viewDoc) {
        this.viewDoc = viewDoc;
    }
    
    public void setFillMessage(boolean trueFalse) {
        this.isFillMessage = trueFalse;
    }
    
    public void center() {
    	Monitor primary = Display.getDefault().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		this.shell.setLocation(x, y);
    }

    public Shell getShell() {
    	return this.shell;
    }
    
    public static Image getWarningImage() {
    	try{
			return new Image(Display.getDefault(), ContentTypeViewerSWT.class
														.getResource(ImagesPaths.WARNING_ICON).openStream());
		} catch (IOException ex) {
		}
		return null;
    }
    
    public static Image getAlertImage() {
    	try{
			return new Image(Display.getDefault(), ContentTypeViewerSWT.class
														.getResource(ImagesPaths.CLOSE_ICON).openStream());
		} catch (IOException ex) {
		}
		return null;
    }
    
    public String getDialogString() {
    	return bundle.getString(ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW);
    }
    
    public String getWarningString() {
    	return bundle.getString(WARNING);
    }
    
    public String[] getButtonLabels() {
    	return new String[]{bundle.getString(YES), bundle.getString(CANCEL)};
    }
    
	protected int getViewerSWTHeight() {
		return 480;
	}

	protected int getViewerSWTWidth() {
		return 640;
	}
}
