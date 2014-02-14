/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.widgets.Display;

import ss.client.networking.DialogsMainCli;
import ss.common.UiUtils;

/**
 * @author roman
 *
 */
public class RemoveSphereDialog extends WarningDialog {

	private DialogsMainCli client;
	private Hashtable session;
	private Document doc;
	
	public RemoveSphereDialog(String warning, DialogsMainCli client, Hashtable session, Document doc) {
		super(Display.getDefault().getActiveShell(), warning);
		this.client = client;
		this.session = session;
		this.doc = doc;
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				open();
			}
		});
	}

	protected void buttonPressed(int i) {
		switch(i){
		case 0 : {
			this.client.removeSphere(this.session, this.doc);
		}
		case 1 : 
			getShell().dispose();
		}
	}

}
