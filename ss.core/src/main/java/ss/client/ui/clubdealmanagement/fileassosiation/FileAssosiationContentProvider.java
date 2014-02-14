/**
 * 
 */
package ss.client.ui.clubdealmanagement.fileassosiation;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.clubdeals.ClubdealCollection;


/**
 * @author zobo
 * 
 */
class FileAssosiationContentProvider implements
		IStructuredContentProvider {

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object o) {
		return ((ClubdealCollection)o).toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}
}
