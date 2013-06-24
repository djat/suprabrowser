/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public class MomentLaw extends FieldLaw {

	public MomentLaw(String moment, LOp op) {
		super(moment, "moment", op);
	}

	@Override
	public IWhere getWithoutMoment() {
		return null;
	}		

}
