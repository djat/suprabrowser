package ss.lab.dm3.persist;


import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.ExpressionHelper;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.persist.query.HqlExpression;
import ss.lab.dm3.persist.query.LuceneExpression;
import ss.lab.dm3.persist.query.QueryList;
import ss.lab.dm3.persist.query.GenericQuery;
import ss.lab.dm3.persist.query.SqlExpression;

/**
 * @author Dmitry Goncharov
 */
public class QueryHelper {
	
	/**
	 * 
	 */
	public static final String ID_PROPERTY_NAME = "Id";

	public static <T extends DomainObject> TypedQuery<T> eq( Class<T> entityClass, String propertyName, Object propertyValue ) {
		TypedQuery<T> query = eq( entityClass );
		and( query, ExpressionHelper.eq(propertyName, propertyValue) );
		return query;
	}

	public static <T extends DomainObject> TypedQuery<T> eq( Class<T> entityClass, String propertyName1, Object propertyValue1, String propertyName2, Object propertyValue2 ) {
		TypedQuery<T> query = eq( entityClass, propertyName1, propertyValue1 );
		and( query, ExpressionHelper.eq(propertyName2, propertyValue2) );
		return query;
	}
	
	/**
	 * @param query
	 * @param restriction
	 */
	private static void and(TypedQuery<?> query, Expression restriction) {
		query.setRestriction( ExpressionHelper.and( query.getRestriction(), restriction ) );
	}
	
	public static <T extends DomainObject> TypedQuery<T> eq( QualifiedObjectId<T> id ) {
		return eq( id.getObjectClazz(), id.getId() );
	}
	
	public static <T extends DomainObject> TypedQuery<T> eq(Class<T> entityClass, Long id) {
		final TypedQuery<T> query = eq( entityClass );
		query.setRestriction( ExpressionHelper.eq( ID_PROPERTY_NAME, id ) );
		query.setLimitSize( 1 );
		return query;
	}

	public static <T extends DomainObject> TypedQuery<T> eq(Class<T> entityClass) {
		return new TypedQuery<T>( entityClass );
	}
	
	public static <T extends DomainObject> TypedQuery<T> hql(Class<T> entityClass, String hql, Object ... params ) {
		final TypedQuery<T> query = eq( entityClass );
		query.setRestriction( new HqlExpression(hql, params != null ? params : new Object[0] ) );		
		return query;
	}
	
	/**
	 */
	public static Query combine(Query ... queries ) {
		return new QueryList( queries );
	}

	public static boolean isIdQuery( TypedQuery<?> query) {
		if ( query.getLimitSize() == 1 ) {
			final Expression restriction = query.getRestriction();
			if ( restriction instanceof SimpleExpression ) {
				SimpleExpression simpleExpression = (SimpleExpression) restriction; 
				return simpleExpression.getOperator() == SimpleExpression.Operator.EQ &&
					   ID_PROPERTY_NAME.equals( simpleExpression.getPropertyName() );
			}
		}
		return false;
	}
	
	public static boolean isLuceneQuery( TypedQuery<?> query ) {
		final Expression restriction = query.getRestriction();
		if ( restriction instanceof  LuceneExpression ) {
			return true;
		}
		return false;
	}

	public static Long getIdQueryValue( TypedQuery<?> query) {
		if ( isIdQuery(query) ) {
			SimpleExpression simpleExpression = (SimpleExpression) query.getRestriction();
			return (Long) simpleExpression.getValue();
		}
		else {
			throw new IllegalArgumentException( "Invalid query " + query );
		}
	}

	/**
	 * @return
	 */
	public static <T extends DomainObject> TypedQuery<T> luceneSearch(Class<T> entityClass, String text ) {
		TypedQuery<T> query = eq( entityClass );
		query.setRestriction( new LuceneExpression( text ) );
		return query;
	}

	public static <T extends DomainObject> TypedQuery<T> sql(Class<T> entityClass, String sql, Object ... params) {
		final TypedQuery<T> query = eq( entityClass );
		query.setRestriction( new SqlExpression(sql, params != null ? params : new Object[0] ) );
		return query;
	}
	
	public static <T> GenericQuery<T> genericSql(Class<T> genericClass, String sql, Object ... params) {
		final SqlExpression sqlExpression = new SqlExpression(sql, params != null ? params : new Object[0] );
		final GenericQuery<T> query = new GenericQuery<T>( genericClass, sqlExpression );
		return query;
	}
}
