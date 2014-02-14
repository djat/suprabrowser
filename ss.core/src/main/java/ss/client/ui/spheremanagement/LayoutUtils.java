/**
 * 
 */
package ss.client.ui.spheremanagement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 *
 */
public class LayoutUtils {

	public static GridData createFullFillGridData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true ); 
		
	}
	
	public static GridData createFillHorizontalGridData() {
		return new GridData(SWT.FILL, SWT.FILL, true, false ); 		
	}

	/**
	 * @return
	 */
	public static Layout createFullFillGridLayout() {
		return createNoMarginGridLayout( 1 );
	}
	
	/**
	 * @return
	 */
	public static Layout createNoMarginGridLayout( int numColumns) {
		GridLayout layout = new GridLayout(numColumns, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	public static void addSpacer( Composite parent ) {
		Composite filler = new Composite( parent, SWT.NONE );
		// filler.setBackground( new Color(parent.getDisplay(), 255, 0, 0 ) );
		GridData gridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
		gridData.widthHint = Integer.MAX_VALUE >> 4; 
		gridData.heightHint = 1;
		filler.setLayoutData( gridData  );
	}

	/**
	 * 
	 */
	public static Composite createPlaceHolderComosite( Composite parent ) {
		Composite box = new Composite( parent, SWT.NONE );
		box.setLayout( createNoMarginGridLayout( 1 ) ); 
		box.setLayoutData( createFullFillGridData() );
		return box;		
	}
}
