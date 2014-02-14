/**
 * 
 */
package ss.server.functions.setmark;

/**
 * @author zobo
 *
 */
final class MarkProceduresProvider {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MarkProceduresProvider.class);

	
	Class<? extends SetMarkProcedure<? extends SetMarkData>> get( Class<? extends SetMarkData> clazz ){
		AssociatedProcedure associatedProcedure = clazz.getAnnotation( AssociatedProcedure.class );
		if ( associatedProcedure == null ) {
			throw new IllegalStateException( clazz + " does not have AssociatedProcedure annotation" );
		}
		return associatedProcedure.value();
	}
}
