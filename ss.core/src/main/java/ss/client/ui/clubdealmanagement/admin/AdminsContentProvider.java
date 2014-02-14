/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author zobo
 *
 */
public class AdminsContentProvider implements
		IStructuredContentProvider {

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object o) {
		return ((UserAdminList)o).getAsArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}
}
