/**
 * 
 */
package ss.common.textformatting;

import java.util.List;

import ss.common.textformatting.obtainscenarios.ObtainNotesScenario;
import ss.common.textformatting.obtainscenarios.ObtainTypeScenario;

/**
 * @author zobo
 *
 */
public class TextCustomFormat {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TextCustomFormat.class);
	
	public static final TextCustomFormat INSTANCE = new TextCustomFormat();
	
	private TextCustomFormat(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public List<String> getNotesFromText( final String text ){
		return ObtainNotesScenario.INSTANCE.obtain( text );
	}
	
	public List<String> getSingleTypeFromText( final String text ){
		return ObtainTypeScenario.INSTANCE.obtain( text );
	}
}
