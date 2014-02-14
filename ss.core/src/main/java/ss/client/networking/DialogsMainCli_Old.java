/**
 * 
 */
package ss.client.networking;

import java.util.Hashtable;
import java.util.Vector;

import ss.common.VerifyAuth;
import ss.common.VerifyAuthOld;
import ss.util.SessionConstants;

/**
 *
 */
public class DialogsMainCli_Old {
	
	private VerifyAuth verifyAuth;
	
	/**
	 * Never used
	 */
	private VerifyAuthOld verifyAuthOld = null;


	/**
	 * Gets the createAssets attribute of the DialogsMainCli object
	 * 
	 * @deprecated
	 * 
	 * @return The createAssets value
	 */
	public Vector getCreateAssets() {
		Vector result = new Vector();
		if (this.verifyAuth != null) {
			result = this.verifyAuthOld.getCreateAssets();
		}
		return result;
	}
	

	/**
	 * Gets the generateAssets attribute of the DialogsMainCli object
	 * 
	 * @deprecated
	 * 
	 * @return The generateAssets value
	 */
	public Vector getGenerateAssets() {
		Vector result = new Vector();
		if (this.verifyAuth != null) {
			result = this.verifyAuthOld.getGenerateAssets();
		}
		return result;
	}
	

//	/**
//	 * Gets the modelOptions attribute of the DialogsMainCli object
//	 * 
//	 * @deprecated
//	 * @param session
//	 *            Description of the Parameter
//	 * @param apath
//	 *            Description of the Parameter
//	 * @return The modelOptions value
//	 */
//	public Hashtable getModelOptions(Hashtable session, String apath) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.GET_MODEL_OPTIONS);
//		return ((Get_model_optionsHandler) ph).getModelOptions(session, apath);
//	}
//
//	/**
//	 * Gets the modelThreshold attribute of the DialogsMainCli object
//	 * 
//	 * @param session
//	 *            Description of the Parameter
//	 * @param apath
//	 *            Description of the Parameter
//	 * @return The modelThreshold value
//	 */
//	public Vector getModelThreshold(Hashtable session, String apath) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.GET_MODEL_THRESHOLD);
//		return ((Get_model_thresholdHandler) ph).getModelThreshold(session,
//				apath);

	/**
	 * Gets the allPersonas attribute of the DialogsMainCli object
	 * 
	 * @deprecated
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The allPersonas value
	 */
	@SuppressWarnings("unchecked")
	public Vector getAllPersonas(Hashtable session) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, "get_personas");
		update.put(SessionConstants.SESSION, toSend);
		sendFromQueue(update);
		Vector personas = new Vector();
		return personas;
	}


//	/**
//	 * Description of the Method
//	 * 
//	 * @deprecated
//	 * 
//	 * @param contact_name
//	 *            Description of the Parameter
//	 * @param login
//	 *            Description of the Parameter
//	 * @param crossreference
//	 *            Description of the Parameter
//	 * @param enabledDoc
//	 *            Description of the Parameter
//	 * @param decisiveUsers
//	 *            Description of the Parameter
//	 */
//	public void crossreferenceSpheres(String contact_name, String login,
//			Hashtable crossreference, Document enabledDoc,
//			Hashtable decisiveUsers) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.CROSSREFERENCE_SPHERES);
//		((CrossreferenceSpheresHandler) ph).crossreferenceSpheres(contact_name,
//				login, crossreference, enabledDoc, decisiveUsers);
//	}

//	/**
//	 * Description of the Method
//	 * 
//	 * @deprecated
//	 * 
//	 * @param session
//	 *            Description of the Parameter
//	 * @param memDoc
//	 *            Description of the Parameter
//	 * @param old_login
//	 *            Description of the Parameter
//	 */
//	public void replaceMember(Hashtable session, Document memDoc,
//			String old_login) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.REPLACE_MEMBER);
//		((ReplaceMemberHandler) ph).replaceMember(session, memDoc, old_login);
//	}

//	/**
//	 * Description of the Method
//	 * 
//	 * @deprecated
//	 * 
//	 * @param members
//	 *            Description of the Parameter
//	 * @param system_name
//	 *            Description of the Parameter
//	 * @param display_name
//	 *            Description of the Parameter
//	 */
//	public void registerSphereWithMembers(Vector members, String system_name,
//			String display_name) {
//		ProtocolHandler ph = this.handlers
//				.getProtocolHandler(SSProtocolConstants.REGISTER_SPHERE_WITH_MEMBERS);
//		((RegisterSphereWithMembersHandler) ph).registerSphereWithMembers(
//				members, system_name, display_name);
//	}

	
	/**
	 * @param update
	 */
	private void sendFromQueue(Hashtable update) {
		// TODO Auto-generated method stub
		
	}
}
