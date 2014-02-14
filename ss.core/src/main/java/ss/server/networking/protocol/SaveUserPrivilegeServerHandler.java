package ss.server.networking.protocol;

import java.util.Hashtable;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.common.VerifyAuthOld;
import ss.common.privileges.Permission;
import ss.common.privileges.Privilege;
import ss.common.privileges.PrivilegesManager;
import ss.common.protocolobjects.SaveUserPrivilegeProtocolObject;
import ss.server.networking.DialogsMainPeer;

public class SaveUserPrivilegeServerHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveUserPrivilegeServerHandler.class);
	
	public SaveUserPrivilegeServerHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_USER_PRIVILEGS;
	}

	public void handle(Hashtable update) {
		PrivilegesManager privilegesManager = this.peer.getVerifyAuth().getPrivilegesManager();
		if ( !privilegesManager.getSetDeliveryOptionPrivilege().canModifyPermissionForOtherUsers() ) {
			logger.warn( "Unauthorized trying to change user privileges" );
			return;
		}
		
		VerifyAuth verifyAuth = this.peer.getVerifyAuth();
		SaveUserPrivilegeProtocolObject saveUserPrivilegesProtocolObject = new SaveUserPrivilegeProtocolObject( update );
		String userLoginName = saveUserPrivilegesProtocolObject.getUserLogin();;
		String userContactName = verifyAuth.getRealName( userLoginName );
		Privilege setDeliveryOptionPrivilege = privilegesManager.getSetDeliveryOptionPrivilege();
		Permission userPermission = setDeliveryOptionPrivilege.getPermissions().Parse( saveUserPrivilegesProtocolObject.getUserPermission() );
		logger.info( String.format( "New user permission %s", userPermission.getLevel() ) );
		privilegesManager.getSetDeliveryOptionPrivilege().setUserPermission( userContactName, userLoginName, userPermission );
		saveDataToDb();
	}

	
	private void saveDataToDb() {
		final VerifyAuthOld verifyAuth = VerifyAuthOld.requiredOldVerifyAuth( this.peer.getVerifyAuth() );
		this.peer.getXmldb().replaceDoc(verifyAuth.getSupraSphereDocument(), verifyAuth.getSupraSphereName() );
		notifyDbChanges();
	}
	
	private void notifyDbChanges() {
		final VerifyAuthOld verifyAuth = VerifyAuthOld.requiredOldVerifyAuth( this.peer.getVerifyAuth() );
		this.peer.sendUpdateVerify( verifyAuth.getSupraSphereDocument(), verifyAuth.getSupraSphereName() );
	}


}
