package ss.client.ui.viewers.actions;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.networking.SupraClient;
import ss.client.ui.viewers.NewBinarySWT;
import ss.global.SSLogger;

public class NewBinaryLaunchPDFActionListener implements SelectionListener {
    private NewBinarySWT newBinary;

    Logger logger = SSLogger.getLogger(this.getClass());

    /**
     * 
     */
    public NewBinaryLaunchPDFActionListener(NewBinarySWT newBinary) {
        super();
        this.newBinary = newBinary;
    }
    
    public void actionPerformed(ActionEvent g) {}

	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	
	public void widgetSelected(SelectionEvent arg0) {

        Thread t = new Thread() {
            private NewBinarySWT newBinary = NewBinaryLaunchPDFActionListener.this.newBinary;

            Logger logger = NewBinaryLaunchPDFActionListener.this.logger;

            @SuppressWarnings("unchecked")
			public void run() {
                Hashtable sendSession = (Hashtable) this.newBinary.getSession().clone(); // Need
                // to
                // do
                // this
                // otherwise
                // the
                // session
                // variable
                // will
                // get
                // changed
    
                Element root = this.newBinary.getViewDoc().getRootElement();
    
                this.newBinary.getMessagesPane().sF.client.voteDocument(this.newBinary.getSession(), this.newBinary.getViewDoc()
                        .getRootElement().element("message_id")
                        .attributeValue("value"), this.newBinary.getViewDoc());
                //String leading_dir = "File";
                // SimplestClient client = new
                // SimplestClient((String)mP.session.get("address"),((Integer)mP.session.get("port")).intValue());
                // client.getForLaunch(mP.session,leading_dir,root.element("data_id").attributeValue("value"),getFrame());
                // Client bootSimplest = new
                // Client((String)session.get("address"),(String)session.get("port"));
                // SimplestClient client =
                // (SimplestClient)bootSimplest.startZeroKnowledgeAuth((String)session.get("supra_sphere"),"supra","user","SimplestClient");
                // client.setSupraSphereFrame(mP.sF);
    
                // client.getForLaunch(mP.session,leading_dir,root.element("data_id").attributeValue("value"),getFrame());
    
                SupraClient sClient = new SupraClient(
                        (String) sendSession.get("address"),
                        (String) sendSession.get("port"));
    
                sClient.setSupraSphereFrame(this.newBinary.getMessagesPane().sF);
    
                Hashtable getInfo = new Hashtable();
    
                this.logger.info("DOC :" + this.newBinary.getViewDoc().asXML());
                String dataId = root.element("data_id").attributeValue(
                        "value");
    
                getInfo.put("document", this.newBinary.getViewDoc());
                if (!dataId.toLowerCase().endsWith(".pdf")) {
                    getInfo.put("data_filename", dataId + ".pdf");
    
                } else {
                    getInfo.put("data_filename", dataId);
                }
    
                String internal = dataId;
                StringTokenizer st = new StringTokenizer(internal,
                        "_____");

                this.logger.info("only filename...doesn't really matter: "
                        + st.nextToken());
                // getInfo.put("fname",fname);
    
                sendSession.put("passphrase", this.newBinary.getMessagesPane().sF.getTempPasswords()
                        .getTempPW(
                                ((String) sendSession
                                        .get("supra_sphere"))));
                sendSession.put("getInfo", getInfo);
                sClient
                        .startZeroKnowledgeAuth(sendSession,
                                "GetBinary");
    
            }
        };
        t.start();
        this.newBinary.getShell().dispose();
    
	}
}
