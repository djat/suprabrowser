/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GhostNotificationWindow {
    
    public GhostNotificationWindow() {

        
    }
    Display display = null;
    Shell shell = null;
    
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_GHOSTNOTIFICATIONWINDOW);
    
    private static final String NEW_MESSAGE="GHOSTNOTICATIONWINDOW.NEW_MESSAGE";
	private static final Logger logger = SSLogger.getLogger(GhostNotificationWindow.class);
    
    public void layoutGUI() {
        
        
        this.display = Display.getDefault();
        Thread t= new Thread() {
            public void run() {
            	GhostNotificationWindow gnw = GhostNotificationWindow.this;
        gnw.shell = new Shell(gnw.display,SWT.NONE);
        gnw.shell.setSize(0,0);
        gnw.shell.setVisible(true);
        
        
//		String bdir = System.getProperty("user.dir");
//		String fsep = System.getProperty("file.separator");
		Image im;
		try {
			im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			gnw.shell.setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
		
            }
        };
        UiUtils.swtBeginInvoke(t);
		
        
        
    }
    public void blink() {
        Thread main = new Thread() {
            public void run() {
        
                while (true) {
                    logger.info("Blinking");
                 
                    try {
                        sleep(1500);
                    } catch (InterruptedException e) {
                    	logger.error(e.getMessage(), e);
                    }
                    Thread t = new Thread() {
                        public void run() {

                    GhostNotificationWindow.this.shell.setText(GhostNotificationWindow.this.bundle.getString(GhostNotificationWindow.NEW_MESSAGE));
                        }
                    };
                    UiUtils.swtBeginInvoke(t);
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                	logger.error(e.getMessage(), e);
                }
                Thread blank = new Thread() {
                    public void run() {
                GhostNotificationWindow.this.shell.setText("");
                }
    };
    UiUtils.swtBeginInvoke(blank);
                }
        
            }
        };
        main.start();
        
    }
    
    public void runEventLoop() {
	    

		try {
			this.shell.layout();
			this.shell.open();
			while (!this.shell.isDisposed()) {
			    try {
				if (!this.display.readAndDispatch()) {
					this.display.sleep();
				}
			    } catch (Exception e) {
			    	logger.error(e.getMessage(), e);
			    }
			}
		
			this.display.dispose();
			System.exit(0);

		} catch (Exception e) {

			//e.printStackTrace();
		}
	    

	}
    
    public static void main(String[] args) {
        GhostNotificationWindow gnw = new GhostNotificationWindow();
        gnw.layoutGUI();
        gnw.blink();
        gnw.runEventLoop();
        
    }
    

}
