package ss.client.ui.viewers.actions;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.networking.SupraClient;
import ss.client.ui.viewers.NewBinarySWT;
import ss.global.SSLogger;

public class NewBinarySaveAsPDFActionListener implements SelectionListener {
    private NewBinarySWT newBinary;

    Logger logger = SSLogger.getLogger(this.getClass());

    /**
     * 
     */
    public NewBinarySaveAsPDFActionListener(NewBinarySWT newBinary) {
        super();
        this.newBinary = newBinary;
    }

	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	
	public void widgetSelected(SelectionEvent arg0) {
        Thread t = new Thread() {
            private NewBinarySWT newBinary = NewBinarySaveAsPDFActionListener.this.newBinary;

            Logger logger = NewBinarySaveAsPDFActionListener.this.logger;

            @SuppressWarnings("unchecked")
			public void run() {

                Hashtable sendSession = (Hashtable) this.newBinary.getSession()
                        .clone();

                this.newBinary.getMessagesPane().sF.client.voteDocument(
                        this.newBinary.getSession(), this.newBinary
                                .getViewDoc().getRootElement().element(
                                        "message_id").attributeValue("value"),
                        this.newBinary.getViewDoc());

                Element root = this.newBinary.getViewDoc().getRootElement();

                // String leading_dir = "File";

                SupraClient sClient = new SupraClient((String) sendSession
                        .get("address"),

                (String) sendSession.get("port"));

                sClient
                        .setSupraSphereFrame(this.newBinary.getMessagesPane().sF);

                Hashtable saveInfo = new Hashtable();

                this.logger.info("DOC :" + this.newBinary.getViewDoc().asXML());

                String dataId = root.element("data_id").attributeValue("value");

                saveInfo.put("data_filename", dataId);
                saveInfo.put("as_pdf", "true");

                String internal = dataId;

                StringTokenizer st = new StringTokenizer(internal, "_____");

                // String fname = st.nextToken();

                this.logger.info("only filename...doesn't really matter: "
                        + st.nextToken());

                sendSession.put("passphrase",
                        this.newBinary.getMessagesPane().sF.getTempPasswords()
                                .getTempPW(
                                        ((String) sendSession
                                                .get("supra_sphere"))));

                sendSession.put("saveInfo", saveInfo);

                sClient.setShellToDispose(this.newBinary.getShell());
                sClient.startZeroKnowledgeAuth(sendSession, "SaveBinary");

            }; // run()

        }; // new Thread()

        t.start();
	}
}