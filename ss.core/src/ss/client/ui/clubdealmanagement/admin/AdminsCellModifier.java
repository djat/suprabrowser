/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import org.eclipse.jface.viewers.ICellModifier;

/**
 * @author zobo
 *
 */
public class AdminsCellModifier implements ICellModifier {

	AdminsCellModifier(){
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String arg1) {
		return new Boolean(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {
	}
}
