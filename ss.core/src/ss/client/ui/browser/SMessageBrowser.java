/**
 * 
 */
package ss.client.ui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

import ss.client.ui.email.EmailCommonShell;
import ss.client.ui.viewers.NewMessage;

/**
 * @author roman
 *
 */
public class SMessageBrowser extends SupraBrowser {

	private EmailCommonShell shell;
	
	private NewMessage nm;
	
	public SMessageBrowser(NewMessage nm) {
		super(nm.getMainComp(), SWT.MOZILLA, false);
		this.nm = nm;
		createRichTextBrowser();
	}
	
	public SMessageBrowser(EmailCommonShell shell) {
		super(shell.getCompBody(), SWT.MOZILLA, false);
		this.shell = shell;
		createRichTextBrowser();
	}
	
	@Override
	protected void createContent(int style, boolean belongsToBrowserPane) {
		// DO NOTHING
	}

	protected void createRichTextBrowser() {
		setLayout(new FillLayout());
		String origBody = this.shell!=null ? this.shell.getOrigBody() : null;
		super.initializeBrowser( new SWTMessageBrowser(this, this.shell!=null, origBody ) );	
	}
	
	public NewMessage getNewMessageWindow() {
		return this.nm;
	}
	
	public EmailCommonShell getEmailShell() {
		return this.shell;
	}
	
	public void setTextToTextEditor(final String text) {
		((SWTMessageBrowser)this.getCheckedBrowser()).setTextToTextEditor(text);
	}
	
	public void invokeRichTextJSMonitor() {
		((SWTMessageBrowser)this.getCheckedBrowser()).invokeRichTextJSMonitor();
	}
	
}
