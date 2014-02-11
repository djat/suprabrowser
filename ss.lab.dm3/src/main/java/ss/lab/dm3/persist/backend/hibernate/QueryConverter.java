package ss.lab.dm3.persist.backend.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.QualifiedUtils;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.orm.query.expressions.SimpleExpression.Operator;
import ss.lab.dm3.orm.query.index.MatchUtils;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.Order;
import ss.lab.dm3.persist.OrderList;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.query.HqlExpression;
import ss.lab.dm3.persist.query.SqlExpression;

/**
 * @author Dmitry Goncharov
 */
public class QueryConverter {

	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(QueryConverter.class);
	
	private final Session session;

	private final Mapper<DomainObject> mapper;
	
	/**
	 * @param session
	 * @param dataMapper
	 * @param domainMapper
	 */
	public QueryConverter(Session session, Mapper<DomainObject> mapper) {
		super();
		this.session = session;
		this.mapper = mapper;
	}

	private Criteria createBlank(TypedQuery<?> query) {
		return this.session.createCriteria( getDataClass( query ) ); 
	}
	
	public Query convertToQuery(TypedQuery<?> domTypedQuery) {
		Expression restriction = domTypedQuery.getRestriction();
		Query query = null;
		if ( restriction instanceof HqlExpression ) {
			query = convertToHibQuery( domTypedQuery );
		}
		else if( restriction instanceof SqlExpression ) {
			query = convertToSqlQuery( domTypedQuery );
		} 
		else {
			throw new IllegalArgumentException("Unexpected restriction " + restriction);
		}
		return setUpLimitParams(query, domTypedQuery);
	}
	
	private static Query setUpLimitParams(final Query query, final TypedQuery<?> domTypedQuery) {
		final int limitSize = domTypedQuery.getLimitSize();
		final int offsetPosition = domTypedQuery.getLimitOffset();
		if ( limitSize > 0 ) {
			query.setMaxResults( limitSize );
		}
		if ( offsetPosition > 0 ) {
			query.setFirstResult( offsetPosition );
		}
		return query;
	}
	
	/**
	 * @param domTypedQuery
	 */
	private Query convertToHibQuery(TypedQuery<?> domTypedQuery) {
		final HqlExpression hqlRestriction = (HqlExpression)domTypedQuery.getRestriction();
		final String hql = hqlRestriction.getHql();
		StringBuilder sb = new StringBuilder();
		sb.append( hql );
		for (Order order : domTypedQuery.getOrders()) {
			sb.append( " order by " );
			sb.append( order.getPropertyName() );
			sb.append( order.isAscending() ? " asc" : " desc" );
		}		
		final Query hibQuery = this.session.createQuery(sb.toString());
		if (log.isDebugEnabled()) {
			log.debug( "Hql " + hql );
		}
		Object[] params = hqlRestriction.getParameters();
		setUpQueryParams(hibQuery, params);		
		return hibQuery;	
	}

	private SQLQuery convertToSqlQuery(TypedQuery<?> domTypedQuery) {	
		final SqlExpression sqlRestriction = (SqlExpression)domTypedQuery.getRestriction();
		StringBuilder sb = new StringBuilder();
		final String sql = sqlRestriction.getSql();
		sb.append( sql );
		for (Order order : domTypedQuery.getOrders()) {
			sb.append( " order by " );
			sb.append( order.getPropertyName() );
			sb.append( order.isAscending() ? " asc" : " desc" );
		}
		sqlRestriction.setSql(sb.toString());
		final SQLQuery sqlQuery = createSqlQuery(sqlRestriction);
		if (log.isDebugEnabled()) {
			log.debug( "Sql " + sql );
		}
		sqlQuery.addEntity( domTypedQuery.getEntityClass() );
		return sqlQuery;
	}

	public SQLQuery createSqlQuery(final SqlExpression sqlRestriction) {
		final SQLQuery sqlQuery = this.session.createSQLQuery( sqlRestriction.getSql() );
		Object[] params = sqlRestriction.getParameters();
		setUpQueryParams(sqlQuery, params);
		return sqlQuery;
	}

	public static void setUpQueryParams(final Query query, Object[] params) {
		for (int n = 0; n < params.length; n++) {
			Object param = params[n];
			if ( param instanceof QualifiedObjectId<?> ) {
				param = QualifiedUtils.toId( param ).getId();
			}
			Type type = TypeFactory.basic( param.getClass().getName() );
			if (log.isDebugEnabled()) {
				log.debug( "Parameter " + n + " " + param + " pclass " + param.getClass() );
			}
			query.setParameter( n, param, type );
		}
	}
	
	public boolean canConvertToCriterai(TypedQuery<?> domainQuery) {
		return domainQuery.isEvaluable();
	}
	
	public boolean canConvertToLuceneCriterai(TypedQuery<?> domainQuery) {
		return QueryHelper.isLuceneQuery( domainQuery );
	}
	
	public Criteria convertToCriteria( TypedQuery<?> domainQuery ) {
		if ( TypedQuery.class == domainQuery.getClass() ) {
			final Criteria hibCriteria = createBlank(domainQuery);
			BeanMapper<DomainObject> beanMapper = this.mapper.get( domainQuery.getEntityClass() );
			final Criterion hibCriterion = convert( beanMapper, domainQuery.getRestriction() );
			hibCriteria.add( hibCriterion );
			if (log.isDebugEnabled()) {
				log.debug("Converted criterion is " + hibCriterion );
			}
			final OrderList orders = domainQuery.getOrders();
			if ( !orders.isEmpty() ) {
				for (Order order : orders) {
					hibCriteria.addOrder( toHibernateOrder( order ) );
				}
			}
			final int limitSize = domainQuery.getLimitSize();
			if ( limitSize > 0 ) {
				hibCriteria.setMaxResults( limitSize );				
			}
			return hibCriteria;
		}
		else {
			throw new QueryConvertException( domainQuery );
		}
	}

	/**
	 * @param restrictions
	 * @return
	 */
	private Criterion convert(BeanMapper<DomainObject> beanMapper, ExpressionList restrictions) {
		final ExpressionList.Junction junction = restrictions.getJunction();
		final Junction hibJunction = createHibJunction(junction);
		for( Expression exp : restrictions ) {
			hibJunction.add( convert( beanMapper, exp ) );
		}
		return hibJunction;
	}
	
//	private Criterion convert(BeanMapper<DomainObject> beanMapper, LuceneExpression restriction) {
//		return null;
//	}

	private Criterion convert(BeanMapper<DomainObject> beanMapper, SimpleExpression restriction) {
		final Property<?> property = beanMapper.getProperty(restriction.getPropertyName());
		Object value = restriction.getValue();
		value = MatchUtils.getMatchable(value, property);
		// Create hibernate equivalent
		final Operator operator = restriction.getOperator();
		if ( operator == SimpleExpression.Operator.EQ ) {
			if ( value != null ) {
				return Restrictions.eq(property.getPersistentName(), value );
			}
			else {
				return Restrictions.isNull( property.getPersistentName() );
			}
		}
		else if ( operator == SimpleExpression.Operator.NE ) {
			if ( value != null ) {
				return Restrictions.ne(property.getPersistentName(), value );
			}
			else {
				return Restrictions.isNotNull( property.getPersistentName() );
			}
		}
		else {
			throw new IllegalArgumentException( "Unsupported restriction " + restriction );
		}
	}

	/**
	 * @param restriction
	 * @return
	 */
	private Criterion convert(BeanMapper<DomainObject> beanMapper, Expression restriction) {
		if ( restriction instanceof ExpressionList ) {
			return convert( beanMapper, (ExpressionList) restriction );
		}
		else if ( restriction instanceof SimpleExpression ) {
			return convert( beanMapper, (SimpleExpression) restriction );
		}
//		else if ( restriction instanceof LuceneExpression ) {
//			return convert( beanMapper, (LuceneExpression) restriction );
//		}
		else {
			throw new IllegalArgumentException( "Unexpected expressoin " + restriction );
		}
	}

	/**
	 * @param junction
	 * @return
	 */
	private Junction createHibJunction(final ExpressionList.Junction junction) {
		if ( junction == ExpressionList.Junction.AND ) {
			return Restrictions.conjunction();
		}
		else if ( junction == ExpressionList.Junction.OR ) {
			return Restrictions.disjunction();			
		}
		else {
			throw new IllegalArgumentException( "Unexpected junction " + junction );
		}
	}

	/**
	 * @return
     */
	public Class<? extends DomainObject> getDataClass(TypedQuery<?> query) {
		return query.getEntityClass();
	}
		
	/**
	 * @param order
	 * @return
	 */
	public static org.hibernate.criterion.Order toHibernateOrder(ss.lab.dm3.persist.Order order) {
		return order.isAscending() ? org.hibernate.criterion.Order.asc( order.getPropertyName() ) : org.hibernate.criterion.Order.desc( order.getPropertyName() );
	}
	
}
