package ss.server.domain.service;

import java.util.Hashtable;

import ss.domainmodel.LoginSphere;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;

public interface IReplaceUsernameInMembership extends ISupraSphereFeature {

	void replaceUsernameInMembership(Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier, String username, String supraSphere,
			String sSession, LoginSphere loginSphere);
	
	public void replaceUserNameInMembership2(XMLDB xmldb,
			LoginSphere loginSphere, final Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier, DialogsMainPeer cont);
	
}
