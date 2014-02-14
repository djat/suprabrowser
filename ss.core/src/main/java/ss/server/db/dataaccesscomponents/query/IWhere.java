/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 *
 */
public interface IWhere {
	
	public String getWhere();	
	
	public IWhere and(IWhere where);
	
	public IWhere or(IWhere where);

	/**
	 * @return
	 */
	public IWhere getWithoutMoment();

}
