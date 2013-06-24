/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class TypeCellModifier implements ICellModifier {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(TypeCellModifier.class);

	private ManageByContactComposite manageComposite;
	
	private TableViewer tv;

	public TypeCellModifier(final ManageByContactComposite manageComposite, final TableViewer tv) {
		this.manageComposite = manageComposite;
		this.tv = tv;
	}

	public boolean canModify(Object o, String property) {
		ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) o;
		boolean can = ManageByContactComposite.TYPE.equals(property)
			&& clubdeal.hasContact(this.manageComposite.getSelection()); 
		return can;
	}

	public Object getValue(Object o, String property) {
		ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject) o;
		if (ManageByContactComposite.TYPE.equals(property)) {
			ContactStatement cd = clubdeal.getContactByName(this.manageComposite
					.getSelection().getContactNameByFirstAndLastNames());
			if (cd != null && cd.getRole()!=null && this.manageComposite.getManager().getContactTypes().contains(cd.getRole())) {
				return this.manageComposite.getManager().getContactTypes().indexOf(cd.getRole());
			}
		}
		return this.manageComposite.getManager().getContactTypes().indexOf(MemberReference.NO_TYPE);
	}

	public void modify(Object element, String property, Object value) {
		if (!property.equals(ManageByContactComposite.TYPE)) {
			return;
		}
		int index = ((Integer) value).intValue();
		ClubdealWithContactsObject cd = (ClubdealWithContactsObject) ((Item)element).getData();
		if(index<0) {
			logger.error("index is out of bounds");
			return;
		}
		final String type = this.manageComposite.getManager().getContactTypes().get(index);
		if ( type == null ) {
			logger.error("Type is null");
			return;
		}
		final ContactStatement contact = this.manageComposite.getSelection();
		final ContactStatement existedContact = cd.getContactByName(contact.getContactNameByFirstAndLastNames());
		if ((existedContact != null) && (existedContact.getRole().equals( type ))) {
			return;
		}
		this.manageComposite.getManager().typeForContactChanged(cd.getClubdeal(), 
				(existedContact != null) ? existedContact : contact, type, cd.hasContact(contact) );
		this.manageComposite.setChanged(true);
		this.tv.refresh();
	}
}
