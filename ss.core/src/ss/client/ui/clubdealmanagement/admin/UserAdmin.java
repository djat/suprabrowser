/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import java.io.Serializable;

/**
 * @author zobo
 *
 */
public class UserAdmin implements Serializable {

	private static final long serialVersionUID = 809308580654176476L;

	private final String contact;
	
	private final String login;
	
	private boolean admin = false;
	
	private boolean primary = false;
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public UserAdmin(String contact, String login){
		this(contact, login, false, false);
	}
	
	public UserAdmin(String contact, String login, boolean admin){
		this(contact, login, admin, false);
	}

	public UserAdmin(String contact, String login, boolean admin,
			boolean primary) {
		super();
		this.contact = contact;
		this.login = login;
		this.admin = admin;
		this.primary = primary;
	}

	/**
	 * @param oldUa
	 */
	public UserAdmin(UserAdmin oldUa) {
		this.contact = oldUa.getContact();
		this.login = oldUa.getLogin();
		this.admin = oldUa.isAdmin();
		this.primary = oldUa.isPrimary();
	}

	public String getContact() {
		return this.contact;
	}

	public String getLogin() {
		return this.login;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public boolean isPrimary() {
		return this.primary;
	}

	@Override
	public String toString() {
		return "member: (" + this.login + ":" + this.contact + ")" + 
			(isAdmin() ? (isPrimary() ? "PRIMARY ADMIN": "ADMIN") : "");
	}
}
