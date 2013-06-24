/**
 * 
 */
package ss.client.ui.cursor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

import ss.client.ui.SDisplay;

/**
 * @author zobo
 *
 */
public class CursorController {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CursorController.class);
	
	public static final CursorController INSTANCE = new CursorController();
	
	private final Cursor cursorHand = new Cursor(SDisplay.display.get(), SWT.CURSOR_HAND);
	
	private final Cursor cursorHelp = new Cursor(SDisplay.display.get(), SWT.CURSOR_HELP);
	
	private final Cursor cursorWait = new Cursor(SDisplay.display.get(), SWT.CURSOR_WAIT);
	
	private final Cursor cursorDefault = new Cursor(SDisplay.display.get(), SWT.CURSOR_ARROW);

	private CursorController(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public Cursor getCursorHelp() {
		return this.cursorHelp;
	}

	/**
	 * @return
	 */
	public Cursor getCursorDefault() {
		return this.cursorDefault;
	}
}
