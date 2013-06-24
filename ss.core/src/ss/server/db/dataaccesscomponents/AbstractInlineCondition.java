/**
 * 
 */
package ss.server.db.dataaccesscomponents;

/**
 * @author d!ma
 *
 */
public abstract class AbstractInlineCondition {

	/**
	 * @return string that passed to SQL query
	 */
	public abstract String formatLikeString();

}
