/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.networking.protocol.getters.UpdateAdminsCommand;
import ss.client.ui.clubdealmanagement.admin.UserAdmin;
import ss.common.StringUtils;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.admin.AdminsCollection;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class UpdateAdminsCommandHandler extends AbstractGetterCommandHandler<UpdateAdminsCommand, String> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateAdminsCommandHandler.class);

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public UpdateAdminsCommandHandler(DialogsMainPeer peer) {
		super(UpdateAdminsCommand.class, peer);
		// TODO Auto-generated constructor stub
	}	
	

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected String evaluate(UpdateAdminsCommand command)
			throws CommandHandleException {
		try {
			final UserAdmin ua = command.getUserAdmin();
			if (ua == null) {
				return "UserAdmin is null";
			}
			if (!this.peer.getVerifyAuth().isPrimaryAdmin()) {
				return "Current user is not primary admin";
			}
			Document supraDoc = null;
			try {
				supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			} catch(DocumentException ex) {
				return "Error with document occured";
			}
			final SupraSphereStatement supraSt = SupraSphereStatement.wrap(supraDoc);
			final String result = process(supraSt, ua);
			if (StringUtils.isNotBlank(result)) {
				return result;
			}
			this.peer.getXmldb().updateSupraSphereDoc(supraSt.getBindedDocument());
			updateVerifyAuth();
			return "";
		} catch (Throwable ex) {
			logger.error("Error", ex);
			return "Unknown error occured";
		}
	}
	
	private String process(SupraSphereStatement supraSt, UserAdmin ua) {
		final AdminsCollection admins = supraSt.getAdmins();
		if (ua.isAdmin()) {
			if (ua.isPrimary()) {
				if (admins.isPrimaryAdmin(ua.getLogin(), ua.getContact())) {
					return "Already primary admin";
				}
				if (!admins.isAdmin(ua.getLogin(), ua.getContact())){
					return "Not admin to make it primary";
				}
				admins.makePrimary(ua.getLogin(), ua.getContact());
			} else {
				if (admins.isAdmin(ua.getLogin(), ua.getContact())){
					return "Already Admin";
				}
				admins.addAdmin(ua.getLogin(), ua.getContact());
			}
		} else {
			if (!admins.isAdmin(ua.getLogin(), ua.getContact())){
				return "It is not admin, can not demote";
			}
			if (admins.isPrimaryAdmin(ua.getLogin(), ua.getContact())) {
				return "Can not demote primary admin";
			}
			admins.removeAdmin(ua.getLogin(), ua.getContact());
		}
		return "";
	}

	private void updateVerifyAuth() {
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			
		} catch(DocumentException ex) {
			logger.error("can't update verify:(", ex);
		}		
		
		if(supraDoc==null) {
			logger.warn("supra doc from database is null");
			return;
		}
		
		this.peer.getVerifyAuth().setSphereDocument(supraDoc);
		DialogsMainPeer.updateVerifyAuthForAll(supraDoc);
	}
}
