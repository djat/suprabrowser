/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.byspheres;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.spheremanagement.memberaccess.MemberAccess;
import ss.util.ImagesPaths;


/**
 * 
 */
public class MemberLabelProvider implements ITableLabelProvider {
	
	/**
	 * 
	 */
	private static final int CHECKED_COLUMN = 1;

	private final Image checked;
	
	private final Image unchecked;
	
	/**
	 * 
	 */
	public MemberLabelProvider() {
		super();
		this.checked = new Image(null,ImagesPaths.openStream(ImagesPaths.CHECKED ) );
	    this.unchecked = new Image(null, ImagesPaths.openStream(ImagesPaths.UNCHECKED ) ); 
	}
	/**
	 * Returns the image
	 * 
	 * @param element
	 *            the element
	 * @param columnIndex
	 *            the column index
	 * @return Image
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == CHECKED_COLUMN ) {
			MemberAccess member = (MemberAccess) element;
			return member.isAccess() ? this.checked : this.unchecked ; 
		}
		return null;
	}

	/**
	 * Returns the column text
	 * 
	 * @param element
	 *            the element
	 * @param columnIndex
	 *            the column index
	 * @return String
	 */
	public String getColumnText(Object element, int columnIndex) {
		if ( columnIndex == CHECKED_COLUMN ) {
			return null;
		}
		else {
			return element.toString();
		}		
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * Disposes any created resources
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Returns whether altering this property on this element will affect the
	 * label
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return boolean
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false; //  MemberListComposite.VISIBLITY_COLUMN_NAME.equals(property);
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(ILabelProviderListener listener) {		
	}
	
}