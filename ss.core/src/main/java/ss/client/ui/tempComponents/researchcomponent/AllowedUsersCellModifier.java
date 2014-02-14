/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;


/**
 * @author zobo
 * 
 */
public class AllowedUsersCellModifier implements ICellModifier {

	private final ReSearchUsersPreferencesComposite provider;

	AllowedUsersCellModifier(final ReSearchUsersPreferencesComposite provider) {
		this.provider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean canModify(Object arg0, String arg1) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 *      java.lang.String)
	 */
	public Object getValue(Object element, String arg1) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		Boolean bool = this.provider.getUsers().get((String) element);
		if (bool != null) {
			return bool;
		}
		return new Boolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		this.provider.getUsers().put((String) element, (Boolean) value);
		this.provider.getViewer().update(element, null);
	}
}
