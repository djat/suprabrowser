package ss.domainmodel;

public class LoginSphere {

	private String systemName;
	
	private String displayName;

	
	/**
	 * @param systemName
	 * @param displayName
	 */
	public LoginSphere(String systemName, String displayName) {
		super();
		this.systemName = systemName;
		this.displayName = displayName;
	}

	public String getSystemName() {
		return this.systemName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
