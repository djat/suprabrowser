/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.SDisplay;
import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class ContactLabelProvider implements ITableLabelProvider, IColorProvider {

	private final ManageByClubdealComposite manageComposite;
	
	public ContactLabelProvider(final ManageByClubdealComposite manageComposite) {
		this.manageComposite = manageComposite;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object o, int index) {
		ContactStatement member = (ContactStatement)o;
		return member.getFullContactName();
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
		if(this.manageComposite==null) {
			return SDisplay.display.get().getSystemColor(SWT.COLOR_BLACK);
		}
		if (this.manageComposite.getSelection() == null) {
			return SDisplay.display.get().getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		String systemName = this.manageComposite.getSelection().getClubdealSystemName();
		final ContactStatement contact = (ContactStatement) obj;
		boolean canModerate = ModerationUtils.INSTANCE.canModerate(this.manageComposite.getFolder().getWindow().getClient(), 
				systemName, contact.getContactNameByFirstAndLastNames());
		if (!canModerate) {
			return SDisplay.display.get().getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		return SDisplay.display.get().getSystemColor(SWT.COLOR_BLACK);
	}

}
