/**
 * 
 */
package ss.server.db.dataaccesscomponents;

/**
 * @author d!ma
 *
 */
public class UserLoginCondition extends AbstractInlineCondition {

	private String login;
	
	
	/**
	 * @param login
	 */
	public UserLoginCondition(String login) {
		super();
		this.login = login;
	}


	/* (non-Javadoc)
	 * @see ss.server.db.dataaccesscomponents.AbstractInlineCondition#formatLikeString()
	 */
	@Override
	public String formatLikeString() {
		return "%" + this.login + "%";
	}

}
