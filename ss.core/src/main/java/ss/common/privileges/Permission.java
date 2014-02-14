package ss.common.privileges;

/**
 * 
 * Permissions has level and display name.
 * 
 */
public class Permission {
	
	private String level;

	private String displayName;

	/**
	 * Creates permission
	 * 
	 * @param level
	 *            permission level
	 * @param display name 
	 */
	public Permission( String level, String displayName ) {
		super();
		this.level = level;
		this.displayName = displayName;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * @return dispay name of privilege
	 */
	public String getDisplayName() {
		return this.displayName;
	}

}
