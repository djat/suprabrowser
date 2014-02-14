/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public class BynaryWhere extends BaseWhere {

	private IWhere first;

	private IWhere second;

	private BOp op;

	public BynaryWhere(IWhere first, IWhere second) {
		this(first, second, BOp.AND);
	}

	public BynaryWhere(IWhere first, IWhere second, BOp op) {
		this.first = first;
		this.second = second;
		this.op = op;
	}

	public String getWhere() {
		return this.op.eval(this.first, this.second);
	}

	public static IWhere AndAll(IWhere... wheres) {
		IWhere old = null;
		for (IWhere where : wheres) {
			if (old != null) {
				old = old.and(where);
			} else {
				old = where;
			}
		}
		return old;
	}

	public static IWhere OrAll(IWhere... wheres) {
		IWhere old = null;
		for (IWhere where : wheres) {
			if (old != null) {
				old = old.or(where);
			} else {
				old = where;
			}
		}
		return old;
	}

	@Override
	public IWhere getWithoutMoment() {
		IWhere withoutMomentFirst = this.first.getWithoutMoment();
		IWhere withoutMomentSecond = this.second.getWithoutMoment();
		if (withoutMomentFirst == null) {
			return withoutMomentSecond;
		}
		if (withoutMomentSecond == null) {
			return withoutMomentFirst;
		}
		return new BynaryWhere(withoutMomentFirst, withoutMomentSecond);
	}

}
