package ss.lab.dm3.orm.query.ext;

import ss.lab.dm3.orm.query.Expression;

public class ExpressionBuilder {

	//private static ThreadLocal<BuilderContextProvider> builderContextProvider = new ThreadLocal<BuilderContextProvider>();
	
	//private final BuilderContext builderContext;
	
	/**
	 * 
	 */
	public ExpressionBuilder() {
		super();
	}

	public Expression compile() {
		throw new UnsupportedOperationException( "Not yet implemented");
	}
	
	public void eq( String propertyName, Object value ) {
		throw new UnsupportedOperationException( "Not yet implemented");
	}
	
	public void ne( String propertyName, Object value ) {
		throw new UnsupportedOperationException( "Not yet implemented");
	}
	
	
}
