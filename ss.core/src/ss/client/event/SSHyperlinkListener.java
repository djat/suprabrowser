/*
 * Created on Mar 17, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package ss.client.event;

import java.io.IOException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class SSHyperlinkListener implements HyperlinkListener {

	private static final Logger logger = SSLogger.getLogger(SSHyperlinkListener.class);
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

			String newname = e.getURL().toString();
			String execName = ("cmd /c \"start " + newname + "\"");

			if (execName != null) {

				try {
					//@SuppressWarnings("unused")
					Process p = Runtime.getRuntime().exec(execName);
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}

			}

		}

	}

}