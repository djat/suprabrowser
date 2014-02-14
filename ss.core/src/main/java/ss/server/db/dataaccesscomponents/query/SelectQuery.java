/**
 * 
 */
package ss.server.db.dataaccesscomponents.query;

/**
 * @author dankosedin
 * 
 */
public class SelectQuery {
	private static final String LIMIT = " LIMIT ";

	private static final String WHERE = " WHERE ";

	private static final String FROM = " FROM ";

	private static final String SELECT = " SELECT ";

	private String select;

	private String from;

	private IWhere where;

	private String limit;

	private Order order;

	public SelectQuery() {
		this("XMLDATA", "supraspheres");
	}

	public SelectQuery(String select, String from) {
		this.setSelect(select);
		this.setFrom(from);
	}
	
	

	public SelectQuery(String select, String from, IWhere where, String limit, Order order) {		
		this.select = select;
		this.from = from;
		this.where = where;
		this.limit = limit;
		this.order = order;
	}

	public String getQuery() {
		return this.getQSelect() + this.getQFrom() + this.getQWhere()
				+ this.getQOrder() + this.getQLimit();
	}
	
	/**
	 * @return
	 */
	public String getNoLimitQuery() {		
		return this.getWithoutLimit().getQuery();
	}
	
	/**
	 * @return
	 */
	public String getNoMomentQuery() {		
		return this.getWithoutMoment().getQuery();
	}


	/**
	 * @return
	 */
	public SelectQuery getWithoutMoment() {
		return new SelectQuery(this.select, this.from,this.where.getWithoutMoment(),null,this.order);
	}

	/**
	 * @return
	 */
	public SelectQuery getWithoutLimit() {		
		return new SelectQuery(this.select, this.from,this.where,null,this.order);
	}

	private String checkFild(String field, String prefix) {
		return (field == null) ? "" : prefix + field;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getSelect() {
		return this.select;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return this.from;
	}

	public void setWhere(IWhere where) {
		this.where = where;
	}

	public IWhere getWhere() {
		return this.where;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getLimit() {
		return this.limit;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return this.order;
	}

	private String getQSelect() {
		return checkFild(this.select, SELECT);
	}

	private String getQFrom() {
		return checkFild(this.from, FROM);
	}

	private String getQWhere() {
		return checkFild(this.where.getWhere(), WHERE);
	}

	private String getQLimit() {
		return checkFild(this.limit, LIMIT);
	}

	private String getQOrder() {
		return checkFild(this.order.getOrder(), "");
	}

}