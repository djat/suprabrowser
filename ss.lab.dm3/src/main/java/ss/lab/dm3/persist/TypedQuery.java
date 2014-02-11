package ss.lab.dm3.persist;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.persist.backend.search.SecureLockCollector;

/**
 * @author Dmitry Goncharov
 */
public class TypedQuery<T extends DomainObject> extends Query {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3353694404407836092L;
	
	protected final Class<T> entityClass;

	private Expression restriction = new ExpressionList( ExpressionList.Junction.AND );
	
	private int limitOffset = -1;
	
	private int limitSize = -1;
	
	private final OrderList orders = new OrderList();
	
	/**
	 * Implemented only for lucene search {{
	 */
	private boolean secure = false;
	
	private final SecureLockCollector secureKeys = new SecureLockCollector();
	/**
	 * }} Implemented only for lucene search
	 */
	
	/**
	 * @param entityClass
	 */
	public TypedQuery(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public final Class<T> getEntityClass() {
		return this.entityClass;
	}

	public DomainObjectCollector<T> find() {
		return getDomain().find( this );
	}
	
	public Expression getRestriction() {
		return this.restriction;
	}
	
	public void setRestriction(Expression restriction) {
		if ( restriction == null ) {
			throw new NullPointerException( "restriction" );
		}
		this.restriction = restriction;
	}

	public T resolve() {
		return getDomain().resolve( this );
	}
		
	public int getLimitSize() {
		return this.limitSize;
	}

	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
	}
	
	/**
	 * @return
	 */
	private Domain getDomain() {
		return DomainResolverHelper.getCurrentDomain();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb.append( "entityClass", this.entityClass.getSimpleName() )
		.append( "restriction", this.restriction )
		.toString();
	}

	/**
	 * @return
	 */
	public boolean isEvaluable() {
		return this.restriction != null && this.restriction.isEvaluable();
	}

	public OrderList getOrders() {
		return this.orders;
	}
	
	public TypedQuery<T> addOrderByAsc( String propertyName ) {
		getOrders().add( Order.asc(propertyName) );
		return this;
	}
	
	public TypedQuery<T> addOrderByDesc( String propertyName ) {
		getOrders().add( Order.desc(propertyName) );
		return this;
	}
	
	public TypedQuery<T> addLimit( int size ) {
		setLimitSize( size );
		return this;
	}
	
	public TypedQuery<T> addLimit( int offset, int size ) {
		setLimitOffset(offset);
		setLimitSize(size);
		return this;
	}

	public int getLimitOffset() {
		return this.limitOffset;
	}

	public TypedQuery<T> setLimitOffset(int limitOffset) {
		this.limitOffset = limitOffset;
		return this;
	}
	
	public boolean hasLimitSize() {
		return this.limitSize != -1;
	}
	
	public boolean hasLimitOffset() {
		return this.limitOffset != -1;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public SecureLockCollector getSecureKeys() {
		return secureKeys;
	}
	
	
		
}