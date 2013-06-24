package ss.client.debug;


import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.MessagesPane;

public class DebugConsoleWindow extends Window implements IDebugCommandConext {

	private DebugCommandCollection allCommands;
	
	private Text txtOutput;
	
	private MessagesPane messagesPageOwner;
	
	private ParsedDebugCommandLine parsedDebugCommandLine = null; 
	
	public DebugConsoleWindow() {
		this( null );
	}
	
	/**
	 * 
	 */
	public DebugConsoleWindow( MessagesPane messagesPageOwner) {
		super( (Shell) null );
		this.messagesPageOwner = messagesPageOwner;
		this.allCommands = new DebugCommandCollection();
		this.allCommands.loadDefault();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayoutData( new GridData( GridData.FILL,GridData.FILL, true, true) );
		composite.setLayout( new GridLayout( 2, true ) );
		
		Label lblCommand = new Label( composite, SWT.LEFT );
		lblCommand.setText( "Command" );

		final Text txtCommand = new Text( composite, SWT.BORDER | SWT.MULTI  );
		txtCommand.setText( "Please type command here..." );
		GridData ldCommand = new GridData(SWT.FILL, SWT.CENTER, true, false );
		ldCommand.horizontalSpan = 2;
		ldCommand.minimumHeight = 40;
		txtCommand.setLayoutData( ldCommand );
		txtCommand.addKeyListener( new KeyAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.keyCode == '\r' ) {
					String command = txtCommand.getText() ;
					command = command != null ? command.trim() : "";
					if ( DebugConsoleWindow.this.executeCommand( command ) ) {
						txtCommand.setText( "" );
					}			
					else {
						txtCommand.setText( command );
					}
				}
			}
			
		});
		
		Label lblOutput = new Label( composite, SWT.LEFT | SWT.TOP );
		lblOutput.setText( "Output" );
		
		this.txtOutput = new Text( composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		this.txtOutput.setText( "Welcome to debug console!\r\nPlease type your request in command field." );
		GridData ldOutput = new GridData(SWT.FILL, SWT.FILL, true, true );
		ldOutput.horizontalSpan = 2;
		ldOutput.minimumHeight = 100;
		this.txtOutput.setLayoutData( ldOutput );
		
		return composite;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize( new Point( 640, 480 ) );
		String title; 		
		if ( this.messagesPageOwner != null ) {
			title = "#" + this.messagesPageOwner.client.getVerifyAuth().getUserSession().getSphereId()  + " - Messages Page debug console";
		}
		else {
			title = "Unbounded debug console";
		}
		newShell.setText( title );
	}



	/**
	 * Perfrom execute command line   
	 * @return true if command have been executed
	 */
	protected boolean executeCommand(String commandLine) {
		this.parsedDebugCommandLine = new ParsedDebugCommandLine( commandLine );
		return this.allCommands.processCommand( this );
	}	
	
	
	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandConext#getMessagesPageOwner()
	 */
	public MessagesPane getMessagesPageOwner() {
		return this.messagesPageOwner;
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandConext#getParsedDebugCommandLine()
	 */
	public ParsedDebugCommandLine getParsedDebugCommandLine() {
		return this.parsedDebugCommandLine;
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandConext#HandleOutput(java.lang.String)
	 */
	public void handleOutput(String commandOuput) {
		if ( commandOuput == null ) {
			commandOuput = "[null]";		}
		this.txtOutput.setText( commandOuput );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.IDebugCommandConext#getAllCommands()
	 */
	public DebugCommandCollection getAllCommands() {
		return this.allCommands;
	}

	
}
