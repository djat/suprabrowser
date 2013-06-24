package ss.client.ui;

/*
 *  This is a popup dialog that takes over the screen. It is activated when a message is sent with
 *  the option "Confirm Receipt" set.
 */

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.messagedeliver.popup.SOptionPane;
import ss.common.UiUtils;
import ss.global.SSLogger;

/**
 *  Description of the Class
 *
 * @author     david
 * @created    December 21, 2003
 */
public class SEditMessage {
	String message = "";
	MessagesPane mp = null;
	Document doc = null;
	MessagesPane main = null;
	String current_sphere = null;
	Text tf = null;
	String linktext = null;
	///new String("http://www.suprasphere.com");
	String type = null;
	SOptionPane so = null;
	Hashtable session = null;
	boolean keepalive = true;
	boolean clicked = false;
	final Display display;

	Shell currentShell = null;

	GridLayout gridLayout = null;
	GridData gridData = null;
	
	String selectedText = "";

	int totalWithCommentText = 0;
	
//	private ResourceBundle bundle = ResourceBundle
//    .getBundle(LocalizationLinks.CLIENT_UI_SEDITMESSAGE);
//	
	private static final Logger logger = SSLogger.getLogger(SEditMessage.class);
	
	private static final String SIM = "SEDITMESSAGE.SIM";
	private static final String SAVE = "SEDITMESSAGE.SAVE";
	private static final String CLOSE = "SEDITMESSAGE.CLOSE";

	/**
	 *Constructor for the SOptionPane object
	 *
	 * @param  main     Description of the Parameter
	 * @param  userSession  Description of the Parameter
	 */
	public SEditMessage() {
	    this.display = Display.getDefault();
	    
	}
	public SEditMessage(MessagesPane main, Hashtable session) {

		this.main = main;
		this.session = session;
		this.display = Display.getDefault();

	}
	public SEditMessage(Hashtable session, Document document, MessagesPane mP, String selectedText, String origText) {
	    
	 
	    this.session = session;
	    this.display = Display.getDefault();
	    this.selectedText = selectedText;
	    
	}
	
	
	public static void main(String[] args) {
	    SEditMessage se = new SEditMessage();
	    se.selectedText = "Hey there, this is a big ol test, plenty of text perhaps even a couple of lines to edit things.\n\n Then much will happen and all will be well";
	    se.showDialog();
	    
	}


	boolean once = false;


	/**
	 *Constructor for the SOptionPane object
	 *
	 * @param  doc   Description of the Parameter
	 * @param  mp    Description of the Parameter
	 * @param  main  Description of the Parameter
	 */
	



	/**
	 *Constructor for the SOptionPane object
	 */
	

	/**
	 *  The main program for the SOptionPane class
	 *
	 * @param  args  The command line arguments
	 */
	


	/**
	 *  Sets the doc attribute of the SOptionPane object
	 *
	 * @param  doc  The new doc value
	 */
	



	/**
	 *  Description of the Method
	 */

	public void closeFromWithin() {

		
						this.currentShell.dispose();

		

	}


	/**
	 *  Description of the Method
	 */
	public void showDialog() {
		UiUtils.swtInvoke( new Runnable() { 
				public void run() {


		SEditMessage editM = SEditMessage.this;
		final Shell shell = new Shell(editM.display);

		editM.currentShell = shell;

		editM.gridLayout = new GridLayout();
		editM.gridLayout.numColumns = 2;
			shell.setLayout(editM.gridLayout);

			shell.setSize(640, 480);

			shell.setText(SEditMessage.SIM);

			editM.gridData = new GridData();

			final StyledText mainText = new StyledText(shell, SWT.WRAP  | SWT.BORDER | SWT.V_SCROLL);
			mainText.addVerifyKeyListener(new MyVerifier());
			mainText.setSize(150, 150);
			mainText.setBackground(new Color(null, 255, 255, 255));

			editM.gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
			mainText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			editM.gridData = new GridData();

			editM.gridData.verticalAlignment = GridData.FILL;
			editM.gridData.horizontalAlignment = GridData.FILL;

			editM.gridData.horizontalSpan = 2;
			editM.gridData.grabExcessHorizontalSpace = true;
			editM.gridData.grabExcessVerticalSpace = true;

			mainText.setLayoutData(editM.gridData);
			
			
			
			Button publishButton = new Button(shell,SWT.PUSH);
			publishButton.setText(SAVE);
            
            publishButton.addListener(SWT.Selection,
                    new Listener() {

                public void handleEvent(Event event) {
                    //mp.sF.client.publishTerse(session,
                    
                }
    });

            
            Button closeButton = new Button(shell, SWT.PUSH);
			closeButton.setText(CLOSE);
			
			closeButton.addListener(SWT.Selection,
					new Listener() {

						public void handleEvent(Event event) {
						    closeFromWithin();
						}
			});
				
											
			
			StyleRange styleRange = new StyleRange();
			styleRange.start = 0;
			styleRange.length = editM.selectedText.length();
			styleRange.foreground = editM.display.getSystemColor(SWT.COLOR_BLUE);

			editM.message = editM.selectedText;// + "\n\n-----Comment Below-----\n";
			editM.totalWithCommentText = editM.message.length();
			mainText.setText(editM.message);
			mainText.setStyleRange(styleRange);
							
			//boolean returnFocus = false;
			//if (main.sF.hasFocus()) { 
				//tf.setFocus();
				//returnFocus = true;
			//	}
			
			/*
			int flags = OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE;
			
			OS.SetWindowPos(shell.handle, OS.HWND_TOPMOST, 0, 0, 0, 0, flags);
			
			
			//tf.setFocus();
			
			TCHAR windowClass = new TCHAR(0, "SWT_Window1", true);
			TCHAR lpWindowName = new TCHAR(0, "SIM", true);
			int hWnd = OS.FindWindow(windowClass, lpWindowName);
			
			
			

			OS.SendMessage(hWnd, OS.WM_ACTIVATE, 0, 0);

			OS.SendMessage(hWnd, OS.WM_SYSCOMMAND, OS.SC_RESTORE, 0);
			OS.ShowWindow(hWnd, OS.SC_RESTORE);
				*/
			shell.setVisible(true);
			
			mainText.addMouseListener(new MouseListener() {
			    	public void mouseDown(MouseEvent e) {
			    	   
			    	    if (e.button == 3) {
			    	    	logger.info("hey: "+mainText.getSelectionText());
			    	        
			    	        	
			    	    }
			    	    else {
			    	        logger.info("There");
			    	    }
			    	    
			    	
			    	}
			    	
			    	public void mouseDoubleClick(MouseEvent e) {}
			    	public void mouseUp(MouseEvent e) {}
			    
			    
			        
			});
			mainText.addKeyListener(
					new KeyListener() {
						SEditMessage editM = SEditMessage.this;
					    public void keyReleased(KeyEvent evt) {}

						public void keyPressed(KeyEvent evt) {
						    
						    
					
						    
						    
						      if (evt.character == SWT.DEL) {
								    try {
								    
								    
								    //String nextText = mainText.getText(mainText.getCaretOffset(),mainText.getCaretOffset());
								            
								        //System.out.println("next text: "+nextText);
								        //System.out.println("newText: "+newText);
								    StyleRange styleRange = new StyleRange();
									styleRange.start = mainText.getCaretOffset();
									styleRange.length = 1;
									styleRange.foreground = this.editM.display.getSystemColor(SWT.COLOR_RED);
									styleRange.background = new Color(null,255,230,230);
									
									

									//mainText.replaceTextRange(mainText.getCaretOffset(),1,nextText);
									mainText.setStyleRange(styleRange);
									mainText.setCaretOffset(mainText.getCaretOffset()+1);
								    } catch (IllegalArgumentException iae) {}
									
								    
								}
								else if (evt.character == SWT.BS) {
								    try {
							
								    StyleRange curRange = mainText.getStyleRangeAtOffset(mainText.getCaretOffset()-1);
								    
								    if (curRange.foreground.equals(this.editM.display.getSystemColor(SWT.COLOR_DARK_GREEN))) {
								        
								        mainText.replaceTextRange(mainText.getCaretOffset()-1,1,"");
								        this.editM.totalWithCommentText-=1;
								        
								    }
								    else {
								      
							
						    StyleRange styleRange = new StyleRange();
							styleRange.start = mainText.getCaretOffset()-1;
							styleRange.length = 1;
							styleRange.foreground = this.editM.display.getSystemColor(SWT.COLOR_RED);
							styleRange.background = new Color(null,255,230,230);
							
							mainText.setStyleRange(styleRange);
							mainText.setCaretOffset(mainText.getCaretOffset()-1);
								    
								    }
								} catch (Exception e) {}   
								}
								else {
								 
								    if (mainText.getCaretOffset()<this.editM.totalWithCommentText) {
								        
								            if (evt.keyCode<127) {
								    
								    StyleRange styleRange = new StyleRange();
									styleRange.start = mainText.getCaretOffset()-1;
									styleRange.length = 1;
									styleRange.foreground = this.editM.display.getSystemColor(SWT.COLOR_DARK_GREEN);
									styleRange.background = new Color(null,235,235,235);
									
									mainText.setStyleRange(styleRange);
									this.editM.totalWithCommentText+=1;
								            }
									
								    }
								    
								    
								}
						}
					});
								

			

			//shell.setMaximized(true);


			while(!shell.isDisposed()) {
				if (!editM.display.readAndDispatch()) {
					editM.display.sleep();
				}
			}

				};	
		
				
	
	}
	);

	}
}
class MyVerifier implements VerifyKeyListener {
	public void verifyKey(VerifyEvent e) {
	    //e.doit = false;
	    
	if (e.keyCode == SWT.DEL) {
	 
	   e.doit = false;
	    
	}
	if (e.keyCode == SWT.BS) {
	    e.doit = false;
	
	}
	
	}
}


