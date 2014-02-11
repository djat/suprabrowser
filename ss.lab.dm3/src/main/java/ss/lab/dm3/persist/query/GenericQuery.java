package ss.lab.dm3.persist.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.persist.Query;

public class GenericQuery<T> extends Query {

	/**
	 * 
	 */
	private static final long serialVersionUID = -912809848778493155L;

	private final Class<T> genericClazz;
	
	private final SqlExpression restriction;
	
	public GenericQuery(Class<T> genericClazz, SqlExpression restriction) {
		super();
		if ( genericClazz == null ) {
			throw new NullPointerException( "genericClazz" );
		}
		if ( restriction == null ) {
			throw new NullPointerException( "restriction" );
		}
		this.genericClazz = genericClazz;
		this.restriction = restriction;
	}
	
	public SqlExpression getRestriction() {
		return this.restriction;
	}
	
	public Class<T> getGenericClazz() {
		return this.genericClazz;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "genericClazz", this.genericClazz );
		tsb.append( "restriction", this.restriction );
		return tsb.toString();
	}
	
	
}
