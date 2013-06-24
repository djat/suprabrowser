/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.cursor.CursorController;
import ss.client.ui.widgets.UserMessageDialogCreator;

/**
 * @author zobo
 *
 */
public class HelpMenuListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HelpMenuListener.class);
	
	private final SupraSphereFrame sf;

	public HelpMenuListener( final SupraSphereFrame sf ) {
		this.sf = sf;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		if ( true ) {
			UserMessageDialogCreator.info("Sorry, help not available", "information");
		} else {
			perform();
		}
	}
	
	private void perform(){
		final Shell shell = this.sf.getShell();
		if (shell == null) {
			logger.error( "Shell is null" );
			return;
		}
		final Cursor cursor = shell.getCursor();
		shell.setCursor( CursorController.INSTANCE.getCursorHelp() );
		final MouseListener listener = new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseDown(MouseEvent e) {
				if ( e.button == 2 ){
					if (cursor != null){
						shell.setCursor( cursor );
					} else {
						shell.setCursor( CursorController.INSTANCE.getCursorDefault() );
					}
				}
				shell.removeMouseListener( this );
			}

			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		};
		shell.addMouseListener( listener );
	}
}
