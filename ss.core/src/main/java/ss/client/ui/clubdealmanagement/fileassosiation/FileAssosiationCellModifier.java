/**
 * 
 */
package ss.client.ui.clubdealmanagement.fileassosiation;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import ss.domainmodel.clubdeals.ClubdealWithContactsObject;

/**
 * @author zobo
 *
 */
public class FileAssosiationCellModifier implements ICellModifier {

	private final IDataHashProvider provider;

	FileAssosiationCellModifier(final IDataHashProvider provider ){
		this.provider = provider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object arg0, String arg1) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String arg1) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		Boolean bool = this.provider.getHash().get((ClubdealWithContactsObject)element);
		if (bool != null) {
			return bool;
		}
		return new Boolean(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		this.provider.getHash().put((ClubdealWithContactsObject)element, (Boolean)value);
		this.provider.getViewer().update(element, null);
	}
}
