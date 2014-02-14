package ss.client.ui.viewers.actions;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.networking.SupraClient;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewBinarySWT;
import ss.domainmodel.FileStatement;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class NewBinarySaveAsActionListener implements SelectionListener {
    private NewBinarySWT newBinary;
    
    Logger logger = SSLogger.getLogger(this.getClass());
    /**
     * 
     */
    public NewBinarySaveAsActionListener(NewBinarySWT newBinary) {
        super();
        this.newBinary = newBinary;
    }
    
	
    
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}
	
	public void widgetSelected(SelectionEvent arg0) {
	       Thread t = new Thread() {
            private NewBinarySWT newBinary = NewBinarySaveAsActionListener.this.newBinary;
            Logger logger = NewBinarySaveAsActionListener.this.logger;
            @SuppressWarnings("unchecked")
			public void run() {
    
                Hashtable sendSession = (Hashtable) this.newBinary.getSession().clone();
    
               SupraSphereFrame.INSTANCE.client.voteDocument(this.newBinary.getSession(), this.newBinary.getViewDoc()
                        .getRootElement().element("message_id")
                        .attributeValue("value"), this.newBinary.getViewDoc());
    
               FileStatement file = FileStatement.wrap(this.newBinary.getViewDoc());
    
                SupraClient sClient = new SupraClient(
                        (String) sendSession.get("address"),
    
                        (String) sendSession.get("port"));
    
                sClient.setSupraSphereFrame(SupraSphereFrame.INSTANCE);
    
                Hashtable saveInfo = new Hashtable();
    
                this.logger.info("DOC :" + this.newBinary.getViewDoc().asXML());
    
                saveInfo.put("data_filename", file.getDataId());
    
                StringTokenizer st = new StringTokenizer(file.getDataId(),
                        "_____");
    
                st.nextToken();
    
                this.logger.info("only filename...doesn't really matter: "
                        + st.nextToken());
    
                sendSession.put("passphrase", SupraSphereFrame.INSTANCE.getTempPasswords()
                        .getTempPW(
                                ((String) sendSession
                                        .get("supra_sphere"))));
    
                sendSession.put("saveInfo", saveInfo);
    
                sClient.setShellToDispose(this.newBinary.getShell());
                sClient.startZeroKnowledgeAuth(sendSession,
                        "SaveBinary");
    
            }; // run()
    
        }; // new Thread()
    
        t.start();
	}
}