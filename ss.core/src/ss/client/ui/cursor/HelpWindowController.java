/**
 * 
 */
package ss.client.ui.cursor;

/**
 * @author zobo
 *
 */
public class HelpWindowController {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HelpWindowController.class);

	public static final HelpWindowController INSTANCE = new HelpWindowController();
	
	private HelpWindowController(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
