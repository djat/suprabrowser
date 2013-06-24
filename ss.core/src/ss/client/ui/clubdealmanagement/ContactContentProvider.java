/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class ContactContentProvider implements IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object input) {
		TreeSet<ContactStatement> set = new TreeSet<ContactStatement>( ContactStatement.getComparatorForContactStatements() );
		set.addAll( ((ClubdealManager)input).getMembers() );
		return set.toArray();
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
