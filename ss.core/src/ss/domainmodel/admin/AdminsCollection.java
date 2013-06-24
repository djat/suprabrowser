/**
 * 
 */
package ss.domainmodel.admin;

import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author zobo
 *
 */
public class AdminsCollection extends XmlListEntityObject<AdminItem> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AdminsCollection.class);

	public AdminsCollection() {
		super( AdminItem.class, AdminItem.ITEM_ROOT_ELEMENT_NAME );
	}
	
	public final AdminItem getAdminByLoginAndContact( final String login, final String contact ) {
		if (!check(login, contact)){
			return null;
		}
		return findFirst( new IXmlEntityObjectFindCondition<AdminItem>() {
			public boolean macth(AdminItem entityObject) {
                return (entityObject.getLogin().equals(login) 
                		&& entityObject.getContact().equals(contact));
			}			
		});			
	}
	
	public AdminItem getPrimaryAdmin(){
		for ( AdminItem item : this ) {
			if (item.isMain()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Retuned primary: " + item.toString());
				}
				return item;
			}
		}
		for ( AdminItem item : this ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retuned primary: " + item.toString());
			}
			return item;
		}
		logger.fatal("No Admins at all!");
		return null;
	}
	
	public boolean isPrimaryAdmin( final String login, final String contact ){
		if (!isAdmin(login, contact)) {
			if (logger.isDebugEnabled()) {
				logger.debug("it is even not admin: " + login + ":" + contact);
			}
			return false;
		}
		final AdminItem mainAdmin = getPrimaryAdmin();
		if (mainAdmin.getContact().equals(contact) && mainAdmin.getLogin().equals(login)) {
			if (logger.isDebugEnabled()) {
				logger.debug("This is primary admin: " + mainAdmin.toString());
			}
			return true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("This is not primary admin: " + login + ":" + contact);
		}
		return false;
	}
	
	public boolean isAdmin( final String login, final String contact ){
		if (!check(login, contact)){
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Checking is admin for: " + login + ":" + contact + "...");
		}
		for ( AdminItem item : this ) {
			if (item.getLogin().equals(login) && item.getContact().equals(contact)) {
				if (logger.isDebugEnabled()) {
					logger.debug("returning true");
				}
				return true;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("returning false");
		}
		return false;
	}
	
	private boolean check( final String login, final String contact ){
		if ( login == null ) {
			logger.error("login is null");
			return false;
		}
		if (contact == null) {
			logger.error("contact is null");
			return false;
		}
		return true;
	}
	
	public void makePrimary( final String login, final String contact ){
		if (!check(login, contact)){
			return;
		}
		if (!isAdmin(login, contact)) {
			logger.error("Not admin : " + login + ":" + contact);
			return;
		}
		for ( AdminItem adminItem : this ) {
			if (adminItem.getLogin().equals(login) && adminItem.getContact().equals(contact)){
				adminItem.setMain(true);
				logger.warn("New primary admin: " + login + ":" + contact );
			} else {
				adminItem.setMain(false);
			}
		}
	}
	
	public void addAdmin( final String login, final String contact ) {
		if (!check(login, contact)){
			return;
		}
		final AdminItem existedAdminItem = getAdminByLoginAndContact(login, contact);
		if ( existedAdminItem != null ) {
			logger.error("Such admin already exists " + existedAdminItem.toString());
			return;
		}
		AdminItem item = new AdminItem();
		item.setLogin(login);
		item.setContact(contact);
		item.setMain(false);
		final AdminItem primary = getPrimaryAdmin();
		if ( (primary != null) && (!primary.isMain()) ) {
			primary.setMain(true);
		}
		logger.warn("Added: " + item.toString());
		super.internalAdd( item );
	}
	
	public void removeAdmin( final String login, final String contact ) {
		if (!check(login, contact)){
			return;
		}
		final AdminItem mainAdmin = getPrimaryAdmin();
		if (mainAdmin.getContact().equals(contact) && mainAdmin.getLogin().equals(login)) {
			logger.error("Trying to remove primary admin");
			return;
		}
		final AdminItem existedAdminItem = getAdminByLoginAndContact(login, contact);
		if ( existedAdminItem != null ) {
			super.internalRemove( existedAdminItem );
			logger.warn("Removed " + existedAdminItem.toString());
		}
	}
}
