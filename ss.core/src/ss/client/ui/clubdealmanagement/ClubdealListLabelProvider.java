/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubdealListLabelProvider implements ITableLabelProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ClubdealListContentProvider.class);

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object o, int index) {
		ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject)o;
		return clubdeal.getClubdeal().getName();
	}
	public void addListener(ILabelProviderListener arg0) {
		
	}

	public void dispose() {
		
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
		
	}
}
