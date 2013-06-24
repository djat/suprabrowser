/*
 * Created on Dec 7, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

import org.eclipse.swt.widgets.Display;

public class WaitForThreadWrapper extends Thread {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(WaitForThreadWrapper.class);

	Object obj = null;

	boolean quit = false;

	Display display = null;

	public WaitForThreadWrapper(Display display, Object obj) {

		this.obj = obj;
		start();

	}

	public void run() {

	}

	public void quit() {
		this.quit = true;
	}

	public boolean waitForNotNull() {

		while (this.obj == null || this.quit) {
			try {
				sleep(50);
			} catch (InterruptedException ex) {
				logger.error("Interruption", ex);
			}
		}
		return true;

	}

}
