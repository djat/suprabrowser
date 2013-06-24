/**
 * 
 */
package ss.client.ui;

import org.eclipse.swt.widgets.Display;

/**
 * @author zobo
 *
 */
public class SDisplay {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SDisplay.class); 
	
	private static Display systemDisplay;
	
	static {
		try {
			systemDisplay = Display.getDefault();
		} catch (Throwable ex) {
			systemDisplay = null;
			logger.error("Can not get Display" , ex);
		}
	}
	
	private SDisplay(){

	}
	
	public static final SDisplay display = new SDisplay();

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void sync(Runnable runnable){
		try {
			if (systemDisplay != null)
				systemDisplay.syncExec(runnable);
		} catch (Throwable ex){
			logger.error("Error with display", ex);
		}
	}
	
	public void async(Runnable runnable){
		try {
			if (systemDisplay != null)
				systemDisplay.asyncExec(runnable);
		} catch (Throwable ex){
			logger.error("Error with display", ex);
		}
	}
	
	public Display get(){
		return systemDisplay;
	}
	
	public boolean readAndDispatch(){
		try {
			if (systemDisplay != null)
				return systemDisplay.readAndDispatch();
			else 
				return false;
		} catch (Throwable ex){
			logger.error("Error with display", ex);
			return false;
		}
	}
	
	public boolean sleep(){
		try {
			if (systemDisplay != null)
				return systemDisplay.sleep();
			else
				return false;
		} catch (Throwable ex){
			logger.error("Error with display", ex);
			return false;
		}
	}
}
