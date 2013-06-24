/**
 * 
 */
package ss.common.textformatting.obtainscenarios;

import java.util.ArrayList;
import java.util.List;

import ss.common.StringUtils;
import ss.common.textformatting.ComplexSingleKeywordFormatResolver;
import ss.common.textformatting.RawSingleKeywordFormatResolver;

/**
 * @author zobo
 *
 */
public class ObtainTypeScenario extends DefaultObtainScenario {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ObtainNotesScenario.class);

	private static final String TYPE = "type";
	
	public final static ObtainTypeScenario INSTANCE = new ObtainTypeScenario();
	
	/* (non-Javadoc)
	 * @see ss.common.textformatting.obtainscenarios.DefaultObtainScenario#obtain(java.lang.String)
	 */
	public List<String> obtain( final String text ) {
		if ( StringUtils.isBlank( text ) ) {
			return null;
		}
		final List<String> results = new ArrayList<String>();
		try {
			final List<String> rawResults = RawSingleKeywordFormatResolver.INSTANCE.parse( text, TYPE );
			if ( (rawResults != null) && (!rawResults.isEmpty()) ) {
				results.add( rawResults.get(0) );
				return results;
			}
		} catch ( Exception ex ){
			logger.error( "error getting raw format",ex );
		}
		try {
			final List<String> complexResults = ComplexSingleKeywordFormatResolver.INSTANCE.parse( text, TYPE );
			if ( (complexResults != null) && (!complexResults.isEmpty()) ) {
				results.add( complexResults.get(0) );
				return results;
			}
		} catch ( Exception ex ){
			logger.error( "error getting raw format",ex );
		}
		return results;
	}
}
