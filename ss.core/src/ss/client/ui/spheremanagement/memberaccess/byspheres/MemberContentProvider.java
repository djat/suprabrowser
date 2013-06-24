/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.byspheres;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 *
 */
public class MemberContentProvider implements IStructuredContentProvider {
	  /**
	   * Returns the Person objects
	   */
	  public Object[] getElements(Object inputElement) {
	    return (Object[])inputElement;
	  }

	  /**
	   * Disposes any created resources
	   */
	  public void dispose() {
	    // Do nothing
	  }

	  /**
	   * Called when the input changes
	   */
	  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    // Ignore
	  }
}