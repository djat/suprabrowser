/**
 * 
 */
package ss.client.ui.spheremanagement;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.util.ImagesPaths;

/**
 *
 */
public class SphereTreeLabelProvider extends LabelProvider implements IColorProvider {

	protected final Image image;
	
	
	/**
	 * @param image
	 */
	public SphereTreeLabelProvider() {
		super();
		this.image = new Image(null, getClass().getResourceAsStream( ImagesPaths.SPHERE )); 
	}


	@Override
	public Image getImage(Object element) {
		return this.image;
	}


	public Color getBackground(Object arg0) {
		return null;
	}

	public Color getForeground(Object o) {
		return ((ManagedSphere)o).getStatement().isDeleted() || !((ManagedSphere)o).isEditable() ? Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY) : Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	}
	
	
	
	
	
}
