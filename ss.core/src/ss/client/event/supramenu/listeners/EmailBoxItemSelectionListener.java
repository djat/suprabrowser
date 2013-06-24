/**
 * 
 */
package ss.client.event.supramenu.listeners;

import java.util.Hashtable;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.VerifyAuth;

/**
 * @author roman
 *
 */
public class EmailBoxItemSelectionListener implements SelectionListener {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(EmailBoxItemSelectionListener.class);
	
	private SupraSphereFrame sF;

	public EmailBoxItemSelectionListener(SupraSphereFrame sF) {
		this.sF = sF;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent arg0) {
		logger .info("email box item selected");
		VerifyAuth verify = this.sF.client.getVerifyAuth();
		Hashtable session = this.sF.client.session;
		String emailSphereId = verify.getEmailSphere(
				(String) session.get("username"), 
				(String) session.get("real_name"));
		if (StringUtils.isBlank(emailSphereId)){
			UserMessageDialogCreator.error("Email Box is not found");
		} else {
			SphereOpenManager.INSTANCE.request(emailSphereId);
			//MenuSphereLoader.openSphere(emailSphereId, this.sF);
		}
	}

}
