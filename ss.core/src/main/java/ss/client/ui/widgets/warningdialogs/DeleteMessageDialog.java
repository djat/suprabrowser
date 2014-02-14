/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class DeleteMessageDialog extends WarningDialog {

	private DialogsMainCli client;
	private Hashtable session;
	private Document doc;
	private final WarningDialogListener listener;
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(DeleteMessageDialog.class);
	
	public DeleteMessageDialog(String warning, DialogsMainCli client, Hashtable session, Document doc, WarningDialogListener listener) {
		super(SDisplay.display.get().getActiveShell(), warning);
		this.client = client;
		this.session = session;
		this.doc = doc;
		this.listener = listener;
	}
	
	protected void buttonPressed(int i) {
		switch(i){
		case 0 :{
			this.listener.performOK();
			yesPressed();
		}
		case 1 :
			this.listener.performCancel();
			cancelPressed();
		}
	}
	
	private void yesPressed() {
		String sphereId = (String)this.session.get("sphere_id");
		this.client.recallMessage(this.session, this.doc, sphereId);
		if(this.parentShell.equals(SupraSphereFrame.INSTANCE.getShell())) {
			return;
		}
		this.parentShell.dispose();
	}
	
	protected void cancelPressed() {
		getShell().dispose();
	}
}
