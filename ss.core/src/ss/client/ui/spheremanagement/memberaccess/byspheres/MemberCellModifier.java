/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.byspheres;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.memberaccess.MemberAccess;


/**
 * 
 */
public class MemberCellModifier implements ICellModifier {
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MemberCellModifier.class);
	
	private final TableViewer memberViewerOwner;
	
	private final IChangesDetector detector;
	
	/**
	 * @param memberViewerOwner
	 */
	public MemberCellModifier(final TableViewer memberViewerOwner, final IChangesDetector detector) {
		super();
		this.memberViewerOwner = memberViewerOwner;
		this.detector = detector;
	}

		/**
	 * Gets whether the specified property can be modified
	 * 
	 * @param element
	 *            the book
	 * @param property
	 *            the property
	 * @return boolean
	 */
	public boolean canModify(Object element, String property) {
		return MemberListComposite.VISIBLITY_COLUMN_NAME.equals(property);
	}

	/**
	 * Gets the value for the property
	 * 
	 * @param element
	 *            the book
	 * @param property
	 *            the property
	 * @return Object
	 */
	public Object getValue(Object element, String property) {
		MemberAccess member = (MemberAccess) element;
		if (MemberListComposite.VISIBLITY_COLUMN_NAME.equals(property)) {
			return Boolean.valueOf(member.isAccess());
		}
		return null;
	}

	/**
	 * Modifies the element
	 * 
	 * @param element
	 *            the book
	 * @param property
	 *            the property
	 * @param value
	 *            the new value
	 */
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		
		MemberAccess member = (MemberAccess) element;
		if (MemberListComposite.VISIBLITY_COLUMN_NAME.equals(property)) {
			member.setAccess( ((Boolean) value).booleanValue() );
			logger.debug( "Setting to " + member + " " + member.isAccess() );			
			this.memberViewerOwner.update(member, null );//VISIBLITY_COLUMN_NAME_AS_ARRAY );
			this.detector.setChanged(true);
		}
	}
}
