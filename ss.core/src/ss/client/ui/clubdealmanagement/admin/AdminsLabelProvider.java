/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class AdminsLabelProvider implements ITableLabelProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AdminsLabelProvider.class);

	private final Image checked;
	
	private final Image unchecked;

	public AdminsLabelProvider() {
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
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		final UserAdmin user = (UserAdmin)element;
		if ( columnIndex == 1 ) {
			return user.isAdmin() ? this.checked : this.unchecked;
		} else if ( columnIndex == 2 ){
			return user.isPrimary() ? this.checked : this.unchecked;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object o, int index) {
		final UserAdmin user = (UserAdmin)o;
		if( index == 0 ) {
			return user.getContact();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {
		
	}
	
}