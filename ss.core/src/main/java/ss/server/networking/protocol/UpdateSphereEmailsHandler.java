/**
 * 
 */
package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.SphereEmail;
import ss.server.SystemSpeaker;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 * @author zobo
 * 
 */
public class UpdateSphereEmailsHandler implements ProtocolHandler {

    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger logger = ss.global.SSLogger
            .getLogger(OpenSphereForMembersHandler.class);

    private DialogsMainPeer peer;

    public UpdateSphereEmailsHandler(DialogsMainPeer peer) {
        this.peer = peer;
    }

    public String getProtocol() {
        return SSProtocolConstants.UPDATE_SPHERES_EMAILS;
    }

    public void handle(Hashtable update) {
        handleUpdateSphereEmails(update);
    }

    public void handleUpdateSphereEmails(final Hashtable update) {
        String sphereEmailsString = (String) update.get(SC.SPHERES_EMAIS);
        String sphere_id = (String) update.get(SC.SPHERE_ID);
        Boolean enabled = (Boolean) update.get(SC.SPHERE_EMAILS_ENABLED);
        Boolean addRoutingDefault = (Boolean) update.get(SC.ADD_ROUTING_DEFAULT);
        try {
            
            logger.info("starting updating emails for sphere_id: "+sphere_id);

            SphereEmail sphereEmail = new SphereEmail();
            sphereEmail.setSphereId(sphere_id);
            SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(sphereEmailsString);
            sphereEmail.setEmailNames(set);
            sphereEmail.setEnabled(enabled.booleanValue());
            sphereEmail.setIsMessageIdAdd(addRoutingDefault.booleanValue());
            
            Document supraSphere = this.peer.getXmldb().getUtils().addEmailSphereNode(sphereEmail);
            DialogsMainPeer.updateVerifyAuthForAll( supraSphere );
            
            SystemSpeaker.speakEmailAddressableSphereStateChanged(sphereEmail);

        } catch (DocumentException exc) {
            logger.error("Document Exception", exc);
        }
    }

}
