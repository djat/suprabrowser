/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
class AllowedUsersLabelProvider implements ITableLabelProvider {

	private final Image checked;

	private final Image unchecked;

	private final ReSearchUsersPreferencesComposite parent;

	public AllowedUsersLabelProvider( final ReSearchUsersPreferencesComposite parent ) {
		super();
		this.parent = parent;
		this.checked = new Image(null, ImagesPaths
				.openStream(ImagesPaths.CHECKED));
		this.unchecked = new Image(null, ImagesPaths
				.openStream(ImagesPaths.UNCHECKED));
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
		if (columnIndex == 0) {
			Boolean bool = this.parent.getUsers().get((String) element);
			if (bool == null) {
				bool = new Boolean(false);
			}
			return bool.booleanValue() ? this.checked : this.unchecked;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object o, int index) {
		final String contactName = (String) o;
		if (index == 0) {
			return null;
		} else if (index == 1) {
			return contactName;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {

	}

}
