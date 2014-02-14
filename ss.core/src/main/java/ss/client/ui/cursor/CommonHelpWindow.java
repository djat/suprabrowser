/**
 * 
 */
package ss.client.ui.cursor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zobo
 *
 */
public class CommonHelpWindow extends Window {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommonHelpWindow.class);

	protected CommonHelpWindow( final Shell parent ) {
		super( parent );
	}

	@Override
	protected void configureShell( final Shell parent ) {
		super.configureShell( parent );
	}

	@Override
	protected Control createContents( final Composite parent ) {
		return parent;
	}

	@Override
	protected Point getInitialSize() {
		return new Point( 800, 600 );
	}	
}
