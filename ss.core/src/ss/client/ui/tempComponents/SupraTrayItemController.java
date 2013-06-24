/**
 * 
 */
package ss.client.ui.tempComponents;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TrayItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.ExitDialog;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.balloons.BalloonsController;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * System tray of Supra Sphere application
 */
public class SupraTrayItemController {
    
    /**
	 * @author zobo
	 *
	 */
	private final class LoadDragAndDropBalloonListener implements Listener {
		public void handleEvent(Event event) {
			
			BalloonsController.INSTANCE.addDragAndDropBalloon(SupraTrayItemController.this.supra.getMainMessagesPane());
		/*
		    this.supra.getDisplay().asyncExec(new Runnable() {
		        private SupraSphereFrame supra = SupraTrayItemController.this.supra;
		        @SuppressWarnings("unchecked")
				public void run() {
		        	if (isDragAndDropBalloonActive()){
		        		return;
		        	}
		
		            MessagesPane main = (MessagesPane) this.supra.tabbedPane
		                    .getComponentAt(0);
		
		            final Hashtable bwSession = (Hashtable) main
		                    .getSession().clone();
		
		            bwSession.put("sphere_id", this.supra.client.getVerifyAuth()
		                    .getSystemName(
		                            (String) bwSession.get("real_name")));
		            
		            BalloonWindow bw = new BalloonWindow(Display.getCurrent(), this.supra, null,
		                    bwSession, SWT.ON_TOP | SWT.CLOSE);
		
		            bw.setAnchor(SWT.NONE); // change this to make
		            // it look like a
		            // conversation
		            // dialog...
		
		            bw.setText(SupraTrayItemController.this.bundle
		            		.getString(SupraTrayItemController.DROP_AND_DRAG));
		
		            final Label label = new Label(bw.getContents(), SWT.WRAP);
		
		            // label.setText("Drag and Drop any item to
		            // SupraIndex It!");
		            label.setSize(label.computeSize(260, 50));
		            label.setBackground(bw.getShell().getBackground());
		            bw.getContents().setSize(label.getSize());
		            bw.addSelectionControl(label);
		            // bw.setSize(250,40);
		
		            bw.setVisible(true);
		            
		            setActiveDragAndDropBallon(bw);
		            this.supra.setBw(bw);
		        }
		    });
		*/
		}
	}

	private TrayItem trayItem;
    
    private SupraSphereFrame supra;

    private Vector<MenuItem> detachedMenus = new Vector<MenuItem>();

    private boolean isBlinkOn;

    private boolean blinkPossible;
    
    @SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SupraTrayItemController.class);
    
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SUPRATRAYITEMCONTROLLER);

    private static final String EXIT_COMPLETELY = "SUPRATRAYITEMCONTROLLER.EXIT_COMPLETELY";
    private static final String MAIN_CONSOLE = "SUPRATRAYITEMCONTROLLER.MAIN_CONSOLE";
    private static final String NEW_MESSAGE = "SUPRATRAYITEMCONTROLLER.NEW_MESSAGE";
    
    public SupraTrayItemController(SupraSphereFrame supraFrame) {
        this.supra = supraFrame;
        this.trayItem = new TrayItem(this.supra.getDisplay().getSystemTray(), SWT.NONE);
        init();
    }

    private void init(){
//        Display display = this.supra.getDisplay();
//        String fsep = System.getProperty("file.separator");
//        String bdir = System.getProperty("user.dir");
        //final Image image = new Image(display, 16, 16);
        Image im;
		try {
			im = new Image(this.supra.getDisplay(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			this.trayItem.setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
        
        final Menu menu = createTrayMenu();
        this.trayItem.addListener(SWT.Show, new Listener() {
            public void handleEvent(Event event) {

            }
        });
        this.trayItem.addListener(SWT.Hide, new Listener() {
            public void handleEvent(Event event) {

            }
        });
        this.trayItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {

            }
        });
        
        this.trayItem.addListener(SWT.DefaultSelection, new LoadDragAndDropBalloonListener());

        this.trayItem.addListener(SWT.MenuDetect, new Listener() {
            private SupraSphereFrame supra = SupraTrayItemController.this.supra;
            
            private SupraTrayItemController tray = SupraTrayItemController.this;
            
            public void handleEvent(Event event) {

                for (int i = 0; i < this.tray.detachedMenus.size(); i++) {

                    MenuItem menuItem = (MenuItem) this.tray.detachedMenus.get(i);
                    menuItem.dispose();

                }

                for (final MessagesPane mP : this.supra.getMessagePanesController().getAll() ) {
                    int number = mP.getUnseenNumber();
                    int reply = mP.getReplyNumber();

                    String name = this.supra.client.getVerifyAuth().getDisplayName(
                            (String) mP.getRawSession().get("sphere_id"));

                    if (!name.equals((String) mP.getRawSession().get("supra_sphere"))) {
                        final MenuItem detachedItem = new MenuItem(menu,
                                SWT.DROP_DOWN);

                        this.tray.detachedMenus.add(detachedItem);

                        detachedItem.setText(name + " (" + number + "," + reply
                                + ")");
                        detachedItem.addListener(SWT.Selection, new Listener() {
                        	private Logger logger = SSLogger.getLogger(this.getClass());
                            private SupraSphereFrame supra = SupraTrayItemController.this.supra;
                            public void handleEvent(Event event) {
                                this.logger.info("TEST: "
                                        + detachedItem.getText());
                                StringTokenizer st = new StringTokenizer(
                                        detachedItem.getText(), " ");
                                String token = null;
                                token = st.nextToken();
                                while (true) {
                                    String test = st.nextToken();
                                    if (test.startsWith("(")) {
                                        this.logger.info("Yes, starts with");
                                        break;
                                    }
                                    token = token + " " + test;
                                }

                                this.supra.toFrontOnTop();
                                this.supra.tabbedPane.selectTabByTitle(token);
                                this.supra.getMenuBar().checkAddRemoveEnabled();
                                String key = this.supra.client.getVerifyAuth()
                                        .getSystemName(token);

                                this.logger.info("Here is the key: " + key
                                        + " L :+ " + token);
                                /*
                                 * MessagesPane mP = (MessagesPane)
                                 * sF.messagePanes .get(token);
                                 * 
                                 * 
                                 * mP.setUnseenNumber(0); mP.setReplyNumber(0);
                                 */

                                /*
                                 * System.out.println("name! " + token);
                                 * DetachedPane show = (DetachedPane)
                                 * sF.detached.get(token);
                                 * show.setVisibility(true);
                                 * show.setUnseenNumber(0);
                                 * show.setReplyNumber(0);
                                 */
                            }
                        });
                    }
                    menu.setVisible(true);
                }
            }
        });
    }
    
    private Menu createTrayMenu() {
        Menu menu = new Menu(this.supra.getShell(), SWT.POP_UP);
        MenuItem exitSubMenu = new MenuItem(menu, SWT.DROP_DOWN);
        exitSubMenu.setText(this.bundle.getString(EXIT_COMPLETELY));
        exitSubMenu.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
            	UiUtils.swtBeginInvoke(new Runnable(){
           			public void run() {
           				new ExitDialog(SupraTrayItemController.this.supra);	
           			}
            			
           		});			
            }
        });

        /*MenuItem showSubMenu = new MenuItem(menu, SWT.CASCADE);
        showSubMenu.setText(this.bundle.getString(SHOW));
        Menu subMenu = new Menu(this.supra.getShell(), SWT.DROP_DOWN);
        showSubMenu.setMenu(subMenu);*/
        MenuItem mainSub = new MenuItem(menu, SWT.PUSH);
        mainSub.setText(this.bundle.getString(MAIN_CONSOLE));
        mainSub.addListener(SWT.Selection, new Listener() {
            private SupraSphereFrame supra = SupraTrayItemController.this.supra;
            public void handleEvent(Event e) {
                this.supra.toFrontOnTop();
            }
        });
        /*MenuItem allSub = new MenuItem(subMenu, SWT.PUSH);
        allSub.setText(this.bundle.getString(ALL_DETACHED));
        allSub.addListener(SWT.Selection, new Listener() {
        	private Logger logger  = SSLogger.getLogger(this.getClass());
            private SupraSphereFrame supra = SupraTrayItemController.this.supra;
            public void handleEvent(Event e) {
                this.logger.info("showing detached");
                for (Enumeration num = this.supra.detached.keys(); num
                        .hasMoreElements();) {
                    String key = (String) num.nextElement();
                }
            }
        });
        */
        // MenuItem cut = new MenuItem (menu, SWT.PUSH);
        // cut.setText("Cut");
        // MenuItem copy = new MenuItem (menu, SWT.PUSH);
        // copy.setText("Copy");
        /*
         * for (int i=0; i <5; i++) { MenuItem subMenuItem = new
         * MenuItem(subMenu, SWT.PUSH); subMenuItem.setText("Clear Item " +
         * (i+1)); }
         */
        /*
         * item.addArmListener(new ArmListener() { public void
         * widgetArmed(ArmEvent e) {
         * 
         * System.out.println("it was armed"); }
         * 
         * });
         */
        // }
        return menu;
    }
    
    public void blinkTray() {
        Thread t = new Thread() {
            private SupraTrayItemController tray = SupraTrayItemController.this;
            @Override
			public void run() {
                final Image image = new Image(this.tray.supra.getDisplay(), 16, 16);
                this.tray.setImage(image);
                try {
                    sleep(750);
                } catch (Exception e) {
                }
                Image im;
				try {
					im = new Image(Display.getDefault(), getClass()
							.getResource(ImagesPaths.SUPRA_ICON).openStream());
					this.tray.setImage(im);
				} catch (IOException ex) {
					logger.error("can't create supra icon", ex);
				}
            }
        };
        UiUtils.swtBeginInvoke(t);
    }

    public void endBlink() {

        this.isBlinkOn = false;

        Thread t = new Thread() {
            private SupraTrayItemController tray = SupraTrayItemController.this;
            @Override
			public void run() {
                try {
                	final String supraSphereId =  this.tray.supra.getSurpaSphereId();
                	final String userContactName = this.tray.supra.getUserContactName(); 
                    this.tray.supra.getShell().setText(supraSphereId + " : " + userContactName );
                } catch (Exception e) {
                }
            }
        };
        UiUtils.swtBeginInvoke(t);

    }

    public void startBlink() {

        if (!this.isBlinkOn && this.blinkPossible == true) {

            this.isBlinkOn = true;

            Thread main = new Thread() {
                private SupraTrayItemController tray = SupraTrayItemController.this;
                @Override
				public void run() {

                    while (this.tray.isBlinkOn) {

                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                        	logger.error(e.getMessage(), e);
                        }
                        Thread t = new Thread() {
                            private SupraTrayItemController tray = SupraTrayItemController.this;
                            @Override
							public void run() {

                                if (this.tray.isBlinkOn) {
                                    this.tray.supra.getShell().setText(this.tray.bundle.
                                    					getString(SupraTrayItemController.NEW_MESSAGE));
                                }
                            }
                        };
                        UiUtils.swtBeginInvoke(t);
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                        	logger.error(e.getMessage(), e);
                        }
                        Thread blank = new Thread() {
                            private SupraTrayItemController tray = SupraTrayItemController.this;
                            @Override
							public void run() {
                            	try {
                            		if (this.tray.isBlinkOn) {
                                        SupraSphereFrame.INSTANCE.getShell().setText("");
                                    }
                            	} catch(NullPointerException ex) {
                            		logger.error("Probably SupraSphereFrame is disposed", ex);
                            	}
                            }
                        };
                        UiUtils.swtBeginInvoke(blank);
                    }
                }
            };
            main.start();
        }

    }
    
    public void setImage(Image image) {
        this.trayItem.setImage(image);
    }

    public void dispose() {
        this.trayItem.dispose();
    }

    /**
     * @return Returns the blinkPossible.
     */
    public boolean isBlinkPossible() {
        return this.blinkPossible;
    }

    /**
     * @param blinkPossible The blinkPossible to set.
     */
    public void setBlinkPossible(boolean blinkPossible) {
        this.blinkPossible = blinkPossible;
    }
}
