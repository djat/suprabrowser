/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.SDisplay;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubdealLabelProvider implements ITableLabelProvider, IColorProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ClubdealLabelProvider.class);
	
	private final ManageByContactComposite manageComposite;
	
	public ClubdealLabelProvider(final ManageByContactComposite manageComposite) {
		this.manageComposite = manageComposite;
	}
	
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object o, int index) {
		final ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) o;
		if( index == 0 ) {
			return clubdeal.getClubdeal().getName();
		} else if ( index == 1 ) {
			final ContactStatement contact = this.manageComposite.getSelection();
			if ( contact==null ) {
				//logger.error("Contact is null");
				return null;
			}
			final String type = this.manageComposite.getManager().getChangedType( clubdeal.getClubdeal().getSystemName(), contact );
			if (StringUtils.isNotBlank( type )) {
				return type;
			}
			final ContactStatement existedContact = clubdeal.getContactByName(contact.getContactNameByFirstAndLastNames());
			return (existedContact == null) ? MemberReference.NO_TYPE : existedContact.getRole();
		}
		return null;
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

	public Color getBackground(Object obj) {
		return null;
	}
	
	public Color getForeground(Object obj) {
		if (this.manageComposite.getSelection() == null) {
			return SDisplay.display.get().getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		String selectedContact = this.manageComposite.getSelection()
				.getContactNameByFirstAndLastNames();
		final ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) obj;
		boolean canModerate = ModerationUtils.INSTANCE.canModerate(
				this.manageComposite.getFolder().getWindow().getClient(),
				clubdeal.getClubdealSystemName(), selectedContact);
		if (!canModerate) {
			return SDisplay.display.get().getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		return SDisplay.display.get().getSystemColor(SWT.COLOR_BLACK);
	}
}
