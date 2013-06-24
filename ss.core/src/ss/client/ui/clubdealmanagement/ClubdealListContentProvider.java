/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.domainmodel.clubdeals.ClubdealCollection;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubdealListContentProvider implements IStructuredContentProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ClubdealListContentProvider.class);
	
	public Object[] getElements(Object o) {
		ClubdealManager manager = (ClubdealManager)o;
		ClubdealCollection list = manager.getAllClubdeals();
		return list.toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

	}
}
