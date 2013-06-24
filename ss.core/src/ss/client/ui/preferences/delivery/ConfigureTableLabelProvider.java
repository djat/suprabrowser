/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import ss.domainmodel.workflow.ModelMemberEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ConfigureTableLabelProvider implements ITableLabelProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ConfigureTableLabelProvider.class);
	
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
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object obj, int index) {
		ModelMemberEntityObject member = (ModelMemberEntityObject)obj;
		switch(index) {
		case 0 : {
			return member.getContactName();
		}
		case 1 : {
			return member.getRoleName();
		}
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
}
