/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.clubdeals.ClubdealCollection;

/**
 * @author roman
 *
 */
public class ClubdealContentProvider implements IStructuredContentProvider {
	
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object o) {
		ClubdealCollection list = ((ClubdealManager)o).getAllClubdeals();
		return list.toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}
}
