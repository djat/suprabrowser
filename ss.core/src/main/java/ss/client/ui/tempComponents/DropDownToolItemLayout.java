/**
 * 
 */
package ss.client.ui.tempComponents;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * @author roman
 *
 */
public class DropDownToolItemLayout extends Layout {

	
	@Override
	protected Point computeSize(Composite comp, int height, int width, boolean flushCashed) {
		return new Point(SWT.DEFAULT, SWT.DEFAULT);
	}

	
	@Override
	protected void layout(Composite arg0, boolean arg1) {
		
	}

}
