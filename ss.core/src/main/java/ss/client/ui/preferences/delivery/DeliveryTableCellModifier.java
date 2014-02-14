/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import ss.domainmodel.workflow.AbstractDelivery;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class DeliveryTableCellModifier implements ICellModifier {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(DeliveryTableCellModifier.class);

	private EditDeliveryPreferencesComposite editComposite;
	
	public DeliveryTableCellModifier(EditDeliveryPreferencesComposite editComposite) {
		this.editComposite = editComposite;
	}
	
	public boolean canModify(Object obj, String property) {
		AbstractDelivery delivery = ((AbstractDelivery) obj); 
		boolean canModify = property
				.equals(EditDeliveryPreferencesComposite.ENABLED)
				&& delivery.isConfigurable()
				&& delivery.validate();
		return canModify;
	}

	
	public Object getValue(Object obj, String property) {
		if(property.equals(EditDeliveryPreferencesComposite.DELIVERY_NAME)) {
			return ((AbstractDelivery)obj).getDisplayName();
		} else if (property.equals(EditDeliveryPreferencesComposite.ENABLED)) {
			return Boolean.valueOf(((AbstractDelivery)obj).isEnabled());
		}
		return null;
	}

	public void modify(Object obj, String property, Object value) {
		AbstractDelivery delivery = (AbstractDelivery)((Item)obj).getData();
		if(property.equals(EditDeliveryPreferencesComposite.ENABLED)) {
			delivery.setEnabled(((Boolean)value).booleanValue());
			this.editComposite.getDetector().setChanged(true);
			if (logger.isDebugEnabled()) {
				logger.debug("set detector has changes : "+this.editComposite.getDetector().hasChanges());
			}
			
		}
		this.editComposite.getTableViewer().refresh();
	}

}
