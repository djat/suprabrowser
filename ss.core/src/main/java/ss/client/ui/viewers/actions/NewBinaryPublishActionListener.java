package ss.client.ui.viewers.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.networking.SupraClient;
import ss.client.ui.ControlPanel;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.FileStatement;
import ss.global.SSLogger;

public class NewBinaryPublishActionListener implements SelectionListener {
    private NewBinarySWT newBinary;
    
    @SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(NewBinaryPublishActionListener.class);
    
    @SuppressWarnings("unused")
	private static final String YOU_MUST_WRITE_SUBJECT = "NEWBINARYPUBLISHACTIONLISTENER.YOU_MUST_WRITE_SUBJECT";
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_ACTIONS_NEWBINARYPUBLISHACTIONLISTENER);
    
    /**
     * 
     */
    public NewBinaryPublishActionListener(NewBinarySWT newBinary) {
        super();
        this.newBinary = newBinary;
    }
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	
	public void widgetSelected(SelectionEvent arg0) {
		if( StringUtils.isBlank( this.newBinary.getSubject() ) ) {
			UserMessageDialogCreator.error(this.bundle.getString(YOU_MUST_WRITE_SUBJECT));
			return;
		} 

		final String tagText = this.newBinary.getTagText();
		final FileStatement file = this.newBinary.createFileStatement();
		
		final Thread t = new Thread() {
			private Logger logger = SSLogger.getLogger(this.getClass());
			NewBinarySWT newBinary = NewBinaryPublishActionListener.this.newBinary;
			@SuppressWarnings("unchecked")
			public void run() {

				Hashtable sendSession = (Hashtable) this.newBinary.getSession().clone();

				long longnum = System.currentTimeMillis();

				File fname = new File(this.newBinary.getFileName());

				int inBytes = -1;
				try {
					FileInputStream fin = new FileInputStream(fname);

					inBytes = fin.available();
					fin.close();

				} catch (IOException except) {
					this.logger.error("IOException in getFile", except);
				}

				Integer asdf = new Integer(inBytes);
				String bytes = asdf.toString();

				file.setBytes(bytes);

				String data_filename = (Long.toString(longnum))
				+ "_____" + fname.getName();
				file.setOriginalId(data_filename);
				file.setDataId(data_filename);
				file.setConfirmed(true);
				file.setVotingModelDesc("Absolute without qualification");
				file.setVotingModelType("absolute");
				file.setTallyNumber("0.0");
				file.setTallyValue("0.0");

				sendSession.put("delivery_type", "normal");

				SupraClient sClient = new SupraClient(
						(String) sendSession.get("address"),
						(String) sendSession.get("port"));

				sClient.setSupraSphereFrame(this.newBinary.getMessagesPane().sF);
				Hashtable publishInfo = new Hashtable();

				sendSession.put("passphrase", this.newBinary.getMessagesPane().sF
						.getTempPasswords().getTempPW(
								((String) sendSession
										.get("supra_sphere"))));

				publishInfo.put("messageId", file.getMessageId());
				publishInfo.put("threadId", file.getThreadId());
				publishInfo.put("data_filename", data_filename);
				publishInfo.put("fname", fname);
				publishInfo.put("doc", file.getBindedDocument());
				publishInfo.put("tag", tagText);

				sendSession.put("publishInfo", publishInfo);

				sendSession.put("sphere_id", this.newBinary.getCurrentSphere());

				sClient.startZeroKnowledgeAuth(sendSession,
				"PutBinary");

				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						NewBinaryPublishActionListener.this.newBinary.getShell().dispose();
						((ControlPanel)NewBinaryPublishActionListener.this.newBinary.getMessagesPane()
								.getControlPanel()).getSendField().setText("");
					}
				});
			} // run()
		}; // new Thread()
		t.start();
	}
	
	private Document getExistedDoc(final String sphereId, final String messageId) {
		DialogsMainCli cli = SupraSphereFrame.INSTANCE.client;
		return cli.getSpecificId(cli.session, messageId);
	}
}