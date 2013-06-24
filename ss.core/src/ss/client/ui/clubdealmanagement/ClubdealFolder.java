/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;

import ss.client.ui.clubdealmanagement.admin.AdminsComposite;
import ss.global.SSLogger;


/**
 * @author roman
 *
 */
public class ClubdealFolder extends CTabFolder {

	private static final Logger logger = SSLogger.getLogger(ClubdealFolder.class);
	
	private ClubdealWindow cw;
	
	private AbstractClubdealItem contactItem;
	
	private AbstractClubdealItem clubdealItem;
	
	private AbstractClubdealItem accessItem;
	
	public ClubdealFolder(ClubdealWindow cw) {
		super(cw.getShell(), SWT.NONE);
		this.cw = cw;
		createContent();
	}
	
	private void createContent() {
		setLayout(new GridLayout());
		
		this.contactItem = new ContactTabItem(this);
		
		this.clubdealItem = new ClubdealTabItem(this);
		
		if(!this.cw.getClient().getVerifyAuth().isAdmin()) {
			return;
		}
		
		this.accessItem = new ModeratorsTabItem(this);
		
		if (this.cw.getClient().getVerifyAuth().isPrimaryAdmin()) {
			final CTabItem item = new CTabItem(this, SWT.NONE);
			final AdminsComposite comp = new AdminsComposite(this, this.cw.getClient(), this.cw);
			item.setControl(comp.getControl());
			item.setText("Admin management");
		}
	}
	
	public ClubdealWindow getWindow() {
		return this.cw;
	}
	
	public AbstractClubdealItem getContactItem() {
		return this.contactItem;
	}
	
	public AbstractClubdealItem getClubdealItem() {
		return this.clubdealItem;
	}
	
	public AbstractClubdealItem getAccessItem() {
		return this.accessItem;
	}
	
	public void totalSave() {
		this.clubdealItem.saveChanges();
		this.contactItem.saveChanges();
		this.accessItem.saveChanges();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void refreshOtherTabs(final Class clazz) {
		if(!clazz.equals(ManageByContactComposite.class)) {
			logger.debug("refresh contacts");
			this.contactItem.refresh();
		} else {
			logger.debug("refresh clubdeals");
			this.clubdealItem.refresh();
		}
		if(this.accessItem==null) {
			return;
		}
		this.accessItem.refresh();
	}
}
