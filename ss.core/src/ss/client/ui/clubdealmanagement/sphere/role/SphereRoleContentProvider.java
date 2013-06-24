/**
 * 
 */
package ss.client.ui.clubdealmanagement.sphere.role;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.configuration.SphereRoleList;

/**
 * @author roman
 *
 */
public class SphereRoleContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object obj) {
		return ((SphereRoleList)obj).toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}
}
