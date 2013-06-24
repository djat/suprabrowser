/**
 * 
 */
package ss.client.ui;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class TableContentProvider implements IStructuredContentProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(TableContentProvider.class);
	
	
	public Object[] getElements(Object obj) {
		List input = (List)obj;
		return input.toArray();
	}

	
	public void dispose() {
	}

	
	public void inputChanged(Viewer viewer, Object first, Object second) {
	}

}
