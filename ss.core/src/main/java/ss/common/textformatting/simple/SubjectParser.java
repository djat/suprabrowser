/**
 * 
 */
package ss.common.textformatting.simple;

import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class SubjectParser {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SubjectParser.class);
	
	public static final SubjectParser INSTANCE = new SubjectParser();
	
	private SubjectParser(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void parse( final String text ){
		if ( StringUtils.isBlank( text ) ) {
			logger.error("Text is blank");
			return;
		}
		
	}
}
