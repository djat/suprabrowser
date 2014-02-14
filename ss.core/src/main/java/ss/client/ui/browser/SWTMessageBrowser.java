/**
 * 
 */
package ss.client.ui.browser;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import ss.common.UiUtils;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SWTMessageBrowser extends SWTBrowser {

	private static final Logger logger = SSLogger.getLogger(SWTMessageBrowser.class);
	
	private boolean isEmailShell = false;
	
	private String origBody;
	
	public SWTMessageBrowser(SMessageBrowser parent, boolean isEmailShell, String origBody) {
		super(parent, SWT.MOZILLA);
		this.isEmailShell = isEmailShell;
		this.origBody = origBody;
		addResizeListener();
	}
	
	private void addResizeListener() {
		addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent arg0) {
			}

			public void controlResized(ControlEvent ce) {
				resizeTextEditor(getSize().y);
			}
		});
	}
	
	protected void resizeTextEditor(final int newHeight) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("richtext_object.resizeTextEditor('"+newHeight+"');");
			}
		});	
	}
	
	public void setTextToTextEditor(final String text) {
		final String converted = text.replaceAll("\n", "<br>");
		logger.info("converted:"+converted);
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				checkWidget();
				execute("richtext_object.setTextToEditor('"+converted+"');");
			}
		});
	}
	
	public void invokeRichTextJSMonitor() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("richtext_object.createRichTextElement();");
			}
		});
	}
	
	
	@Override
	protected void onProgressCompleted(ProgressEvent event) {	
		super.onProgressCompleted(event);
		SWTMessageBrowser.this.parent.getMozillaBrowserController().injectJS();
		resizeTextEditor(getSize().y);
		if(this.isEmailShell) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					setTextToTextEditor(SWTMessageBrowser.this.origBody);
				}
			});
		}
	}

}
