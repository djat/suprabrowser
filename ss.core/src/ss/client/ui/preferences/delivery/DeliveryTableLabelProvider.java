/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.domainmodel.workflow.AbstractDelivery;
import ss.util.ImagesPaths;

/**
 * @author roman
 *
 */
public class DeliveryTableLabelProvider implements ITableLabelProvider, IColorProvider {

	private final Image enabled;
	
	private final Image disabled;
	
	private static final Color INVALID_DELIVERY_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	
	public DeliveryTableLabelProvider() {
		super();
		this.enabled = new Image(null,ImagesPaths.openStream(ImagesPaths.CHECKED ) );
	    this.disabled = new Image(null, ImagesPaths.openStream(ImagesPaths.UNCHECKED ) ); 
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == 1 ) {
			AbstractDelivery delivery = (AbstractDelivery) element;
			return delivery.isEnabled() ? this.enabled : this.disabled ; 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object obj, int index) {
		if(index==0) {
			return ((AbstractDelivery)obj).getDisplayName();
		} 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object obj) {
		AbstractDelivery delivery = (AbstractDelivery)obj;
		if(!delivery.validate()) {
			return INVALID_DELIVERY_COLOR;
		}
		return null;
	}

}
