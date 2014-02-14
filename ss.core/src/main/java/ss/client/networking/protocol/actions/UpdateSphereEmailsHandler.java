/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.domainmodel.SphereEmail;

import ss.server.networking.SC;
import ss.util.SessionConstants;

/**
 * @author zobo
 *
 */
public class UpdateSphereEmailsHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UpdateSphereEmailsHandler.class);

    private final DialogsMainCli cli;

    public UpdateSphereEmailsHandler(DialogsMainCli cli) {
        this.cli = cli;
    }

    public String getProtocol() {
        return SSProtocolConstants.UPDATE_SPHERES_EMAILS;
    }

    @SuppressWarnings("unchecked")
	public void handleUpdateSphereEmails(final SphereEmail sphereEmail) {
        Hashtable update = new Hashtable();
        update.put(SC.SPHERES_EMAIS, sphereEmail.getEmailNames().getSingleStringEmails());
        update.put(SC.SPHERE_ID, sphereEmail.getSphereId());
        update.put(SC.SPHERE_EMAILS_ENABLED, new Boolean(sphereEmail.getEnabled()));
        update.put(SC.ADD_ROUTING_DEFAULT, new Boolean(sphereEmail.getIsMessageIdAdd()));
        update.put(SessionConstants.PROTOCOL, SSProtocolConstants.UPDATE_SPHERES_EMAILS);
        this.cli.sendFromQueue(update);
    }
}
