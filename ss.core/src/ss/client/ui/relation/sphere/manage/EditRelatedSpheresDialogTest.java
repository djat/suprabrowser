package ss.client.ui.relation.sphere.manage;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import ss.global.LoggerConfiguration;
import ss.global.SSLogger;

public class EditRelatedSpheresDialogTest {

	public static void main(String[] args) {
		SSLogger.initialize( LoggerConfiguration.DEFAULT );
		final SphereRelationModel model = SphereRelationModelTestHelper.createModel(); 
		ApplicationWindow wnd = new ApplicationWindow( null ) {
			{}
			@Override
			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				shell.setText( "Boots Up Window" );
				EditRelatedSpheresDialog dlg = new EditRelatedSpheresDialog( shell, model );
				dlg.open();
				close();
			}
			@Override
			protected Point getInitialSize() {
				return new Point( 640, 480 );
			}
		};
		wnd.setBlockOnOpen( true );
		wnd.open();
	}

}
