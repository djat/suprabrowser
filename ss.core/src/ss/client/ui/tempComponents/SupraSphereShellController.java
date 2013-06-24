package ss.client.ui.tempComponents;

import java.io.IOException;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.ExitDialog;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.balloons.BalloonsController;
import ss.common.UiUtils;
import ss.util.ImagesPaths;

public class SupraSphereShellController {
    
    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		 .getLogger(SupraSphereShellController.class);
   
    private final SupraSphereFrame supra;
    
    private final ShellListener shellListener;
    
    private Shell shell;

    public SupraSphereShellController(SupraSphereFrame supraFrame ) {
        super();
        this.supra = supraFrame;
        this.shellListener = new SupraFrameShellListener();
    }

    public void init( Shell supraFrameShell ){  
        this.shell = supraFrameShell;
//        String fsep = System.getProperty("file.separator");
//        String bdir = System.getProperty("user.dir");
		try {
			Image im;
			im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			this.shell.setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
        
        setupListeners();
    }

    /**
	 * @author roman
	 *
	 */
	private final class SupraFrameShellListener extends ShellAdapter {
		private SupraSphereShellController shellController = SupraSphereShellController.this;

		public void shellMinimized(ShellEvent e) {
		
		    logger.info("IT minimized here");
		
		    this.shellController.supra.getTrayItem().setBlinkPossible(true);
		}

		public void shellClosed(ShellEvent e) {
		    e.doit = false;
		    UiUtils.swtBeginInvoke(new Runnable() {
		        private SupraSphereShellController shellController = SupraSphereShellController.this;
		        public void run() {
		            new ExitDialog(this.shellController.supra);
		        }
		    });
		}

		public void shellIconified(ShellEvent e) {
		
			BalloonsController.INSTANCE.setShown(false);
		
		}

		public void shellDeiconified(ShellEvent e) {
		
			BalloonsController.INSTANCE.setShown(true);
		
		    this.shellController.supra.getTrayItem().endBlink();
		
		}

		public void shellDeactivated(ShellEvent e) {
		    // logger.info("deactivated");
		    this.shellController.supra.getTrayItem().setBlinkPossible(true);
		    /*
		     * if (bw!=null) { bw.showOrHide(false); }
		     */
		
		}

		public void shellActivated(ShellEvent e) {
		    if (this.shellController.supra.tabbedPane != null) {
		    	if (this.shellController.supra.getShell().isFocusControl()){
		    		this.shellController.supra.setFocusToSendField();
		    	}
		    }
		    this.shellController.supra.getTrayItem().setBlinkPossible(false);
		    this.shellController.supra.getTrayItem().endBlink();		
		}

		public void shellResized(ShellEvent e) {
		
		}

		public void shellMaximized(ShellEvent e) {
		    logger.info("IT MAXIMIZED here");
		    this.shellController.supra.getTrayItem().endBlink();
		}
	}

	
    /**
     * 
     */
    private void setupListeners() {
        this.shell.addMouseListener(new MouseListener() {
            
            private SupraSphereShellController shell = SupraSphereShellController.this;
            
            public void mouseDoubleClick(MouseEvent arg0) {
                this.shell.supra.getTrayItem().endBlink();
            }

            public void mouseDown(MouseEvent arg0) {
                this.shell.supra.getTrayItem().endBlink();
            }

            public void mouseUp(MouseEvent arg0) {
                this.shell.supra.getTrayItem().endBlink();
            }

        });
        
        this.shell.addControlListener(new ControlAdapter() {
           // private SupraSphereShellController shellController = SupraSphereShellController.this;
            
            public void controlResized(ControlEvent e) {

                // mainEmbedComp.setBounds(mainEmbedComp.getClientArea());
                // shell was maximized so undo it
                /*if (this.shellController.shell.getMaximized()) {
                    logger.info("IT MAXIMIZED");

                }
                logger.warn("E SOURCDE: " + e.getSource());
                if (this.shellController.shell.getMinimized()) {
                    logger.warn("IT MINIMIZED");

                }
                */

            }
        });
        
        this.shell.addFocusListener(new FocusListener() {
        	
            private SupraSphereShellController shellController = SupraSphereShellController.this;
            
            public void focusGained(FocusEvent e) {

                if (this.shellController.supra.tabbedPane != null) {
                	this.shellController.supra.setFocusToSendField();
                }

                this.shellController.supra.getTrayItem().setBlinkPossible(false);
                this.shellController.supra.getTrayItem().endBlink();

            
            }

            public void focusLost(FocusEvent e) {

                this.shellController.supra.getTrayItem().setBlinkPossible(true);
            }

        });
        
        
    }

	/**
	 * @return the shellListener
	 */
	public ShellListener getShellListener() {
		return this.shellListener;
	}
}

