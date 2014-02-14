/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.workflow.ModelMemberCollection;
import ss.domainmodel.workflow.ModelMemberEntityObject;

/**
 * @author roman
 *
 */
public class ConfigureTableContentProvider implements
		IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object obj) {
		ModelMemberCollection collection = (ModelMemberCollection)obj;
		Object[] members = new Object[collection.getCount()];
		int i = 0;
		for(ModelMemberEntityObject member : collection) {
			members[i] = member;
			i++;
		}
		return members;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

}
