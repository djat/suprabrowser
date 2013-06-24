package ss.client.ui.viewers;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import ss.client.ui.preferences.delivery.DecesiveConfigureDialog;
import ss.domainmodel.workflow.DecisiveDelivery;
import ss.domainmodel.workflow.ModelMemberCollection;
import ss.domainmodel.workflow.ModelMemberEntityObject;

/**
 * @author roman
 *
 */
public class NewMemberCellModifier implements ICellModifier {

	DecesiveConfigureDialog dialog;
	
	public NewMemberCellModifier(DecesiveConfigureDialog dialog) {
		this.dialog = dialog;
	}
	
	public boolean canModify(Object element, String property) {
		if(property.equals(DecesiveConfigureDialog.PROP_NAME)) {
			return false;
		}							
		return true;
	}

	
	public Object getValue(Object element, String property) {
		ModelMemberEntityObject member = (ModelMemberEntityObject)element;
		
		int index = 0;
		
		ModelMemberCollection collection = (ModelMemberCollection)this.dialog.getTV().getInput();
		for(ModelMemberEntityObject mm : collection) {
			if(mm.getUserName().equals(member.getUserName())) {
				if(mm.getRoleName().equals(new DecisiveDelivery().getDefaultRole().getTitle()))
					index = 1;
			}
		}
		if (DecesiveConfigureDialog.PROP_ROLE.equals(property)) {
			return new Integer(index);
		} 
		return null;
	}

	
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item)
			element = ((Item) element).getData();

		ModelMemberEntityObject member = (ModelMemberEntityObject)element;
		
		if (DecesiveConfigureDialog.PROP_ROLE.equals(property)) {
			member.setRoleName(DecesiveConfigureDialog.allRoles[((Integer)value).intValue()]);
		}
			
		this.dialog.getTV().refresh();
	}
}