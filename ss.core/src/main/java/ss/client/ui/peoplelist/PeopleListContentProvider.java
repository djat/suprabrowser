/**
 * 
 */
package ss.client.ui.peoplelist;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class PeopleListContentProvider implements IStructuredContentProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(PeopleListContentProvider.class);
	
	public Object[] getElements(Object o) {
		SphereMembersTableModel model = (SphereMembersTableModel)o;
		return model.toArray();
	}
	
	public void dispose() {
	}
	
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}
