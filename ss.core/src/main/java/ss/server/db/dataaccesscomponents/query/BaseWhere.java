/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public abstract class BaseWhere implements IWhere {

	public IWhere and(IWhere where) {
		return new BynaryWhere(this, where, BOp.AND);
	}

	public IWhere or(IWhere where) {
		return new BynaryWhere(this, where, BOp.OR);
	}

	public IWhere getWithoutMoment() {		
		return this;
	}
	
}
