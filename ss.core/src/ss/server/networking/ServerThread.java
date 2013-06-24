/**
 * 
 */
package ss.server.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.client.networking.NetworkConnection;
import ss.common.CreateMembership;
import ss.common.EncryptedSocket;
import ss.common.InstallUtils;
import ss.common.SphereDefinitionCreator;
import ss.common.VerifyAuth;
import ss.common.XmlDocumentUtils;
import ss.common.debug.DebugUtils;
import ss.common.domainmodel2.AbstractClientDataProviderConnector;
import ss.common.networking2.ProtocolStartUpInformation;
import ss.common.presence.PresenceUtils;
import ss.common.simplefiletransfer.SimpleFileTransferUtils;
import ss.domainmodel.LoginSphere;
import ss.framework.errorreporting.LogConstants;
import ss.server.db.XMLDB;
import ss.server.debug.ServerDebugFactory;
import ss.server.domain.service.IReplaceUsernameInMembership;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.domainmodel2.SupraServerDataProviderFactory;
import ss.server.errorreporting.ClientLogHandler;
import ss.server.install.update.FilesUploader;
import ss.server.install.update.UpdateProtocolFactory;
import ss.server.networking.binary.AbstractBinaryServerTransmitter;
import ss.server.networking.binary.GetBinaryServerTransmitter;
import ss.server.networking.binary.MultipleDownloadBinaryServerTransmitter;
import ss.server.networking.binary.PutBinaryServerTransmitter;
import ss.server.networking.binary.SaveBinaryServerTransmitter;
import ss.server.networking.filetransfer.FileDownloadHandler;
import ss.server.presense.ServerPresence;
import ss.util.NameTranslation;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

/**
 *
 */
final class ServerThread extends Thread {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ServerThread.class);

	private final BigInteger n = new BigInteger(
			"122401836212692490420534044542160652060089063288916259378992106298635997629004871449040863148842028332936921964140316531574725544295868627297697141329665138830185297018653829634397774436198765430305661897599443877203922836563409947444887027604848997125383124008931080480197775021431253630976071993683966590129");

	private final BigInteger g = new BigInteger(
			"164438241317367690823401305357370607328034430023459713340335187782878205902844516952738722877059454685668830995765089402230495206772108704026831761904445241536185572511706087329237156570564175356155697011727697175323622673030696046933358292860718413231660423149569049278930510073740541237052508995182528917163");

	private Cipher pbeCipher;

	private PBEKeySpec pbeKeySpec;

	private PBEParameterSpec pbeParamSpec;

	private SecretKeyFactory keyFac;

	private final Socket client;

	private OutputStream out = null;

	private InputStream in = null;

	private DataInputStream datain = null;

	private DataOutputStream dataout = null;

	private Cipher cEncrypt = null;

	private Cipher cDecrypt = null;

	private Hashtable session = new Hashtable();

	private Document invitingContactDoc = null;

	public ServerThread(Socket client) {
		logger.info("Starting server");
		this.client = client;
	}

	@Override
	public void run() {
		try {
			setupEncryption();
			// dataout = new DataOutputStream(cout);
			// datain = new DataInputStream(cin);
			// protocol = datain.readInt();
			// System.out.println("GOt protocol: " + protocol);

		} catch (Throwable ex) {
			logger.fatal( "SupraClient handler failed", ex );
			if ( this.client != null ) {
				try {
					this.client.close();
				} catch (IOException closeEx) {
					logger.error( "Can't close socket", closeEx );
				}
			}
		}
	}

	private void setupEncryption() throws DocumentException {
		try {
			java.security.Security
					.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		} catch (Exception e) {
			logger.error( "Failed to registry ecurity provider", e );
		}
		// Salt for PBE
		byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
				(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };
		// Iteration count
		int count = 20;
		// Create PBE parameter set
		try {
			this.pbeParamSpec = new PBEParameterSpec(salt, count);
			// Convert password into SecretKey object,
			// using a PBE key factory
			String pass = "asdfasdf";
			char[] passphrase = pass.toCharArray();
			this.pbeKeySpec = new PBEKeySpec(passphrase);
			this.keyFac = SecretKeyFactory.getInstance(
					"PBEWithSHAAndTwofish-CBC", "BC");
			SecretKey pbeKey = this.keyFac.generateSecret(this.pbeKeySpec);
			// Create PBE Cipher
			this.pbeCipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC",
					"BC");
			this.pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, this.pbeParamSpec);
		} catch (Exception ex) {
			logger.error( "Can't initialize crypt", ex );
		}
		try {
			this.out = this.client.getOutputStream();
			this.in = this.client.getInputStream();
		} catch (IOException ex) {
			logger.error( "Can't get socket in/out", ex );
		}
		this.datain = new DataInputStream(this.in);
		this.dataout = new DataOutputStream(this.out);
		startZeroKnowledgeAuth();	
	}

	/**
	 * Initiates the server component of the zero knowledge auth. It performs it
	 * three times, the first for the server to verifyAuth the client, the
	 * second for the client to verifyAuth the server, and one final time to add
	 * to the bytes used to create the 3DES key.
	 * 
	 * @throws DocumentException
	 */
	@SuppressWarnings( { "deprecation", "unchecked" })
	private void startZeroKnowledgeAuth() throws DocumentException {
		logger.warn("Start zero knowledge auth in ssserver");
		
		int protocol = 0;
		try {
			protocol = this.datain.readInt();
			logger.info("data int: " + protocol);

			if (protocol == -1) {
				logger.warn( "Protocol is -1. Exit" );
				return;
			}

				logger.info("Starting zka in ss");
				String username = this.datain.readUTF();
				logger.info("username: " + username);
				String sphereName = this.datain.readUTF();
				logger.info("sphereName: " + sphereName);
				String supraSphereName = this.datain.readUTF();
				logger.info("supraSphereName: " + supraSphereName);
				String useMachineVerifier = this.datain.readUTF();
				logger.info("useMachineVerifier: " + useMachineVerifier);
				String invite = this.datain.readUTF();
				logger.info("isInviter: " + invite);
				String changePw = this.datain.readUTF();
				logger.info("changePw: " + changePw);
				String profileId = this.datain.readUTF();
				logger.info("profileId: " + profileId);
				String sphereURL = this.datain.readUTF();

				logger
						.info("username, spherename, ssspherename, usemachine, changePw: "
								+ username
								+ " , "
								+ sphereName
								+ " , "
								+ supraSphereName
								+ " , "
								+ useMachineVerifier
								+ " , " + invite + " , " + changePw);

				final XMLDB xmldb = new XMLDB();

				String inviteSphereName = null;
				String inviteSSName = null;
				String changeToLogin = null;
				String inviteURL = null;

				if (changePw.equals("true")) {

					final String tempUsername = this.datain.readUTF();
					final String newSalt = this.datain.readUTF();
					final String newVerifier = this.datain.readUTF();
					final String loginSphere = this.datain.readUTF();

					logger.info("Change membership for: " + tempUsername
							+ " salt: " + newSalt + " : " + loginSphere);

					final Document doc = xmldb.getMembershipDoc(loginSphere,
							tempUsername);

					final Document contactDoc = xmldb.getSpecificID(loginSphere,
							tempUsername);

					if (logger.isDebugEnabled()) {
						logger.debug("Contact is: "
								+ contactDoc.asXML());
					}

					doc.getRootElement().element("verifier").addAttribute(
							"salt", newSalt).setText(newVerifier);
					doc.getRootElement().element("login_name").addAttribute(
							"value", username);

					contactDoc.getRootElement().element("login").addAttribute(
							"value", username);

					try {

						final Vector list = new Vector(contactDoc.getRootElement()
								.element("locations").elements());

						for (int i = 0; i < list.size(); i++) {

							Element one = (Element) list.get(i);

							String systemName = one.attributeValue("ex_system");

							xmldb.replaceDoc(contactDoc, systemName);
						}
					} catch (Exception ex) {
						logger.error("Error in replacing contactDoc",ex);
					}
					xmldb.replaceDoc(doc, loginSphere);

					if (logger.isDebugEnabled()) {
						logger.info("BEFORE, here is suprasphere: "
								+ supraSphereName);
					}

					final ISupraSphereEditFacade supraSphere = xmldb.getEditableSupraSphere();
					// final Document supraSphereDoc = xmldb.getSupraSphereDocumentForXPath();
					supraSphere.updateUserLogin( tempUsername, username, loginSphere, contactDoc );
				} else if (invite.equals("true")) {

					logger.warn("START INVITE NOW");
					// This is after invite gui presented and before login
					// prompt for new username and passphrase presented....

					try {

						logger.info("Invite true!");

						final StringTokenizer st = new StringTokenizer(sphereName,
								".");
						final String sphereId = st.nextToken();
						final String messageId = st.nextToken();
						final Document doc = xmldb.getSpecificID(sphereId, messageId);

						final String existingLogin = doc.getRootElement().element(
								"login").attributeValue("value");

						boolean existsAndNotBlank = false;

						if (existingLogin != null) {
							if (existingLogin.length() > 0) {

								if (!existingLogin.equals(messageId)) {

									existsAndNotBlank = true;
									logger
											.warn("setting exists not blank true: "
													+ existingLogin);

								}

							}

						}

						// logger.warn("existing login: "+existingLogin);

						final String cname = NameTranslation
								.createContactNameFromContactDoc(doc);

						final String inviteContact = doc.getRootElement().element(
								"active_invitation").attributeValue("inviter");
						final String inviteUsername = doc.getRootElement().element(
								"active_invitation").attributeValue(
								"inviter_username");
						inviteSphereName = doc.getRootElement().element(
								"active_invitation").attributeValue(
								"invite_sphere_name");
						inviteSSName = doc.getRootElement().element(
								"active_invitation").attributeValue(
								"invite_supra_sphere_name");
						String inviteSphereId = doc.getRootElement().element(
								"active_invitation").attributeValue(
								"invite_sphere_id");
						String inviteSphereType = doc.getRootElement().element(
								"active_invitation").attributeValue(
								"invite_sphere_type");

						logger.info("invite sphere id :" + inviteSphereId);
						if (inviteSphereId.equals(inviteSSName)) {
							logger.info("inviteSphereId is equals to inviteSSName");
						}
						this.session.put("supra_sphere", inviteSSName);

						if (sphereURL.startsWith("invite")) {
							sphereURL = VariousUtils
									.convertInviteURLtoSphereURL(inviteSSName,
											sphereURL);
						}

						this.session.put("saveNewURL", "true");
						this.session.put("sphereURL", sphereURL);
						this.session.put("cliURL", sphereURL);

						logger.info("cname right here!: " + cname);

						final CreateMembership membership = new CreateMembership();

						final Element duplicate = doc.getRootElement().element(
								"active_invitation");

						if (duplicate != null) {

							final Document existingMemDoc = xmldb.getMembershipDoc(
									inviteSphereId, messageId);

							if (existingMemDoc != null) {

								if (logger.isDebugEnabled()) {
									logger
										.debug("there is an existing member in there!: "
												+ existingMemDoc.asXML());
								}
								try {
									xmldb.removeDoc(existingMemDoc,
											inviteSphereId);
								} catch (Throwable ex) {
									logger
											.error(
													"Failed in ZeroKnowledgeAuth removing existMemDoc",
													ex);
								}

							}

						}

						if (!existsAndNotBlank) {
							// Only prompt for change
							// passphrase next login if
							// existing login is
							// null...otherwise just
							// remove the invite
							// and prompt for username and
							// passphrase...change_passphrase_next_login can be
							// determined in another process...however, must
							// permanently rewrite
							// the sphere::// url in the dyn_client.xml file

							logger.warn("do this..");

							final Document memDoc = membership.createMember(cname,
									messageId, messageId);

							logger.info("membership doc: " + memDoc.asXML());

							final Element root = memDoc.getRootElement();
							final long longnum = System.currentTimeMillis();

							final String message_id = (Long.toString(longnum));

							final Date current = new Date();
							final String moment = DateFormat.getTimeInstance(
									DateFormat.LONG).format(current)
									+ " "
									+ DateFormat.getDateInstance(
											DateFormat.MEDIUM).format(current);
							root.addElement("original_id").addAttribute(
									"value", message_id);
							root.addElement("subject").addAttribute("value",
									"New Membership: " + username);
							root.addElement("giver").addAttribute("value",
									cname);
							root.addElement("message_id").addAttribute("value",
									message_id);
							root.addElement("thread_id").addAttribute("value",
									message_id);
							root.addElement("last_updated").addAttribute(
									"value", moment);
							root.addElement("moment").addAttribute("value",
									moment);

							String tempSupraSphere = null;
							if ((String) this.session.get("supra_sphere") == null) {

								tempSupraSphere = xmldb.getSphereName();
								if (logger.isDebugEnabled()) {
									logger
										.debug("supra_sphere was null in invite....here is name: "
												+ tempSupraSphere);
								}
							} else {

								tempSupraSphere = (String) this.session
										.get("supra_sphere");

							}
							supraSphereName = tempSupraSphere;

							xmldb.insertDoc(memDoc, inviteSphereId);

							logger.info("here...." + supraSphereName);
							this.session.put("sphere_id", inviteSphereId);

							final ISupraSphereEditFacade supraSphere = xmldb.getEditableSupraSphere();
							supraSphere.registerMember( supraSphereName, doc, inviteContact,
								inviteUsername, inviteSphereName,
								inviteSphereId, cname, username,
								inviteSphereType, this.session );
							changeToLogin = "false";

						} else {

							if (sphereURL.startsWith("invite")) {
								sphereURL = VariousUtils
										.convertInviteURLtoSphereURL(
												inviteSSName, sphereURL);
							}

							this.session.put("sphereURL", sphereURL);
							this.session.put("cliURL", sphereURL);

							username = existingLogin;
							changeToLogin = "true";

							String tempSupraSphere = null;
							if ((String) this.session.get("supra_sphere") == null) {

								tempSupraSphere = xmldb.getSphereName();
								if (logger.isDebugEnabled()) {
									logger
										.debug("supra_sphere was null in invite....here is name: "
												+ tempSupraSphere);
								}
							} else {

								tempSupraSphere = (String) this.session
										.get("supra_sphere");

							}
							supraSphereName = tempSupraSphere;
							this.session.put("sphere_id", inviteSphereId);
						}

					} catch (Throwable ex) {
						logger.error(ex);
					}

				} else if (invite.equals("acceptAndChange")) {
					logger.info("acceptAndChange in ServerThread");
				} else if (invite.equals("transferCredentials")) {

					final String machineSalt = this.datain.readUTF();

					final String machineVerifier = this.datain.readUTF();
					final String machineProfile = this.datain.readUTF();
					inviteURL = this.datain.readUTF();

					final StringTokenizer st = new StringTokenizer(sphereName, ".");
					final String sphereId = st.nextToken();
					final String messageId = st.nextToken();
					username = messageId;
					final Document doc = xmldb.getSpecificID(sphereId, messageId);
					logger.warn("doc : " + doc.asXML());

					String cname = null;
					// invite::127.0.0.1:3003,7525552547267882719.1111782142296
					if (doc.getRootElement().element("last_name")
							.attributeValue("value").length() > 0) {
						cname = doc.getRootElement().element("first_name")
								.attributeValue("value")
								+ " "
								+ doc.getRootElement().element("last_name")
										.attributeValue("value");
					} else {
						cname = doc.getRootElement().element("first_name")
								.attributeValue("value");
					}

					final String inviteContact = doc.getRootElement().element(
							"active_invitation").attributeValue("inviter");
					final String inviteUsername = doc.getRootElement().element(
							"active_invitation").attributeValue(
							"inviter_username");
					inviteSphereName = doc.getRootElement().element(
							"active_invitation").attributeValue(
							"invite_sphere_name");
					inviteSSName = doc.getRootElement().element(
							"active_invitation").attributeValue(
							"invite_supra_sphere_name");
					String inviteSphereId = doc.getRootElement().element(
							"active_invitation").attributeValue(
							"invite_sphere_id");
					String inviteSphereType = doc.getRootElement().element(
							"active_invitation").attributeValue(
							"invite_sphere_type");

					this.dataout.writeUTF(inviteSSName);
					this.dataout.writeUTF(username);

					logger.info("cname right here!: " + cname);

					logger.warn("check..."
							+ (String) this.session.get("supra_sphere"));
					logger.warn("removing..." + inviteSSName);

					this.session.remove("supra_sphere");
					this.session.put("supra_sphere", inviteSSName);

					CreateMembership membership = new CreateMembership();

					Document memDoc = membership.createMember(cname,
							machineProfile, machineProfile);

					memDoc.getRootElement().element("verifier").detach();

					memDoc.getRootElement().addElement("machine_verifier")
							.addAttribute("salt", machineSalt).addAttribute(
									"profile_id", machineProfile).setText(
									machineVerifier);

					memDoc.getRootElement().element("login_name").addAttribute(
							"value", username);

					logger.info("membership doc: " + memDoc.asXML());
					xmldb.insertDoc(memDoc, inviteSphereId);

					Element root = memDoc.getRootElement();

					String message_id = VariousUtils.createMessageId();

					Date current = new Date();
					String moment = DateFormat.getTimeInstance(DateFormat.LONG)
							.format(current)
							+ " "
							+ DateFormat.getDateInstance(DateFormat.MEDIUM)
									.format(current);
					root.addElement("original_id").addAttribute("value",
							message_id);
					root.addElement("subject").addAttribute("value",
							"New Membership: " + username);
					root.addElement("giver").addAttribute("value", cname);
					root.addElement("message_id").addAttribute("value",
							message_id);
					root.addElement("thread_id").addAttribute("value",
							message_id);
					root.addElement("last_updated").addAttribute("value",
							moment);
					root.addElement("moment").addAttribute("value", moment);

					String tempSupraSphere = null;
					if ((String) this.session.get("supra_sphere") == null) {

						tempSupraSphere = xmldb.getSphereName();
						logger
								.info("supra_sphere was null in invite....here is name: "
										+ tempSupraSphere);

					} else {
						tempSupraSphere = (String) this.session
								.get("supra_sphere");
					}

					supraSphereName = tempSupraSphere;

					logger.info("here...." + supraSphereName);

					Element toDetach = doc.getRootElement().element(
							"active_invitation");

					String inviteMoment = toDetach.attributeValue("moment");
					toDetach.detach();

					String invitedPath = "//events/invited[@moment=\""
							+ inviteMoment + "\"}";

					Element changeInvited = (Element) doc
							.selectObject(invitedPath);

					if (changeInvited != null) {

						changeInvited.addAttribute("status", "accepted");
					} else {
						logger.info("invited object was NULL!");
					}

					logger
							.warn("WILL NOW REPLACE CONTACT SO NO LONGER ACTIVE...transfer credentials...!!");

					xmldb.replaceDoc(doc, sphereId);

					username = messageId;
					sphereName = sphereId;

					final ISupraSphereEditFacade supraSphere = xmldb.getEditableSupraSphere();
					supraSphere.registerMember(supraSphereName, doc, inviteContact, inviteUsername,
						inviteSphereName, inviteSphereId, cname, username, inviteSphereType, this.session );
					
					final String invitingLogin = xmldb.getUtils().getLoginSphereSystemName(inviteUsername);
					this.invitingContactDoc = xmldb.getContactDoc(
							invitingLogin, inviteUsername);
				}

				LoginSphere loginSphere = null;
				
				logger.info("use machine verifier : "+useMachineVerifier);

				if (useMachineVerifier.equals("true")) {
					final String name = xmldb.getDBSphere();
					loginSphere = new LoginSphere( name, name);
				} else if (useMachineVerifier.equals("profile")) {

					try {
						loginSphere = xmldb.getUtils().findLoginSphereElement(username);
					} catch (Exception exc) {
						logger.error("Exception in useMachineVerifier", exc);
					}
				} else {

					logger.warn("username now: " + username);
					try {
						loginSphere = xmldb.getUtils().findLoginSphereElement(username);
					} catch (Exception exc) {
						logger.error("Exception in useMachineVerifier", exc);
					}
				}

				logger.warn("SupraSphereName: " + supraSphereName + " : "
						+ loginSphere);
				if (loginSphere == null) {
					logger.warn("loginsphere was null");
					if (useMachineVerifier.equals("true")) {
						this.dataout.writeUTF("unreal");
					} else {
						this.dataout.writeUTF("unreal");
					}

				} else {
					logger.info("LOGIN SPHERE: " + loginSphere );
				}

				String loginSphereId = loginSphere.getSystemName();
				if (loginSphereId == null) {
					if (useMachineVerifier.equals("true")) {
						loginSphereId = supraSphereName;

					}
				}

				logger.warn("loginsphereid: " + loginSphereId);
				
				logger.warn("verifier: " + useMachineVerifier);

				Hashtable loginInfo = null;
				if (useMachineVerifier.equals("false")) {
					logger.warn("was false");
					loginInfo = xmldb.getLoginSaltAndVerifier(supraSphereName,
							username, loginSphereId);

					logger.warn("logininfO: " + loginInfo.size());
				} else if (useMachineVerifier.equals("profile")) {
					logger.info("try this because its profile!!");

					loginInfo = xmldb.getMachineSaltAndVerifier(username,
							loginSphereId, profileId);

				} else {
					logger.info("use machine verifier..." + username + " : "
							+ loginSphereId);
					loginInfo = xmldb.getMachineSaltAndVerifier(username,
							loginSphereId);
				}

				final String realName = (String) loginInfo.get("contact");

				logger.warn("got here: " + realName);

				final String saltString = (String) loginInfo.get("salt");
				final String verifier = (String) loginInfo.get("verifier");

				String changePassphraseNextLogin = (String) loginInfo
						.get("changePassphraseNextLogin");
				
				String contactLocked = (String) loginInfo.get("contact_locked");

				logger.warn("Change passphrase next login..."
						+ changePassphraseNextLogin);

				if (changePw.equals("changePassphraseNextLogin")) {
					logger.info("Change passphrase next login was : "
							+ changePassphraseNextLogin
							+ ": ....must have changed: " + changePw);
					changePassphraseNextLogin = "false";
				}

				if (logger.isDebugEnabled()) {
					logger.info("SALT STRING: " + saltString);
					logger.info("Verifier: " + verifier);
				}

				if (changeToLogin == null) {
					changeToLogin = "false";
				}

				if (changeToLogin.equals("true")) {
					logger.warn("change to login was true somehow!!");
					this.dataout.writeUTF("promptLogin");
					logger.warn("INVITE URL " + inviteURL);

					// String newSphereURL =
					// VariousUtils.convertInviteURLtoSphereURL(inviteSSName,inviteURL);

					logger.warn("new sphere URL " + sphereURL);
					this.dataout.writeUTF(sphereURL);

				} else if (saltString == null || realName == null) {
					logger.warn("salt null, quit login");
					this.dataout.writeUTF("unreal");
				} else {
					logger.info("writing...");

					this.dataout.writeUTF(saltString);
					this.dataout.writeUTF(realName);

					logger.info("WRITING CHANGE PASSPHRASE NEXT: "
							+ changePassphraseNextLogin);

					this.dataout.writeUTF(changePassphraseNextLogin);

					final String A1 = this.datain.readUTF(); // used for server to
														// verify
					// client
					final String A2 = this.datain.readUTF(); // used for client to
														// verify
					// server
					final String A3 = this.datain.readUTF(); // used to augment
														// shared
					// secret in Diffie-Hellman

					// String verifier = xmldb.getVerifier(username,sphereName);

					final Hashtable returned1 = getMFromData(verifier, A1);

					final String m1 = (String) returned1.get("m");

					final Hashtable returned2 = getMFromData(verifier, A2);

					final String m2 = (String) returned2.get("m");

					final Hashtable returned3 = getMFromData(verifier, A3);

					final String m3 = (String) returned3.get("m");

					this.dataout.writeUTF(verifier);
					this.dataout.writeUTF((String) returned1.get("bString"));
					this.dataout.writeUTF((String) returned2.get("bString"));
					this.dataout.writeUTF((String) returned3.get("bString"));

					this.dataout.writeUTF((String) returned1.get("uString"));
					this.dataout.writeUTF((String) returned2.get("uString"));
					this.dataout.writeUTF((String) returned3.get("uString"));

					final String clientM1 = this.datain.readUTF();

					if (m1.equals(clientM1) && !contactLocked.equals("true")) {

						logger.info("equals on server");

						this.dataout.writeUTF(m2);

						final EncryptedSocket exchange = new EncryptedSocket(this.datain,
									this.dataout, m3);
						exchange.initServerCrypt();
						this.dataout.flush();
						
						this.cEncrypt = exchange.returncEncrypt();
						this.cDecrypt = exchange.returncDecrypt();

						final CipherOutputStream cos = new CipherOutputStream(this.out,
								this.cEncrypt);
						final CipherInputStream cis = new CipherInputStream(this.in, this.cDecrypt);

						final DataOutputStream cdataout = new DataOutputStream(
								cos);
						final DataInputStream cdatain = new DataInputStream(
								cis);

						int object_size = cdatain.readInt();
						byte[] objectBytes = new byte[object_size];
						cdatain.readFully(objectBytes);

						final Hashtable bootstrap = (Hashtable) bytesToObject(objectBytes);

						final String bootstrapProtocol = (String) bootstrap
								.get("protocol");

						logger.info("bootstrapprotocol: " + bootstrapProtocol);
						ConnectionCounter.INSTANCE.startUpServerPartOfProtocol(bootstrapProtocol);


						/*
						 * if (invite.equals("true")) {
						 * 
						 * bootstrapProtocol = "PromptPasswordChange"; }
						 */

						logger.info("read size: " + bootstrapProtocol);

						final Hashtable session = (Hashtable) bootstrap
								.get("session");

						logger
								.info("bootstrap protocol..."
										+ bootstrapProtocol);

						if (bootstrapProtocol.equals("DeliverMail")) {

							logger.info("delivering email");
							final DialogsMainPeer cont = new DialogsMainPeer(session,
									cdatain, cdataout);
							cont.deliverMail(session);
							cont.dispose();

						} else if (bootstrapProtocol.equals(PresenceUtils.PRESENCE_PROTOCOL_NAME)) {
							ServerPresence.createAndStart( new ProtocolStartUpInformation(session), cdatain, cdataout );
														
						} else if (bootstrapProtocol.equals( AbstractClientDataProviderConnector.DOMAIN_SPACE_PROTOCOL_NAME )) {
							SupraServerDataProviderFactory.INSTANCE.createAndStart( new ProtocolStartUpInformation(session), cdatain,
									cdataout );
							
						} else if (bootstrapProtocol.equals( DebugUtils.DEBUG_PROTOCOL_NAME )) {
							ServerDebugFactory.INSTANCE.createAndStart( new ProtocolStartUpInformation(session), cdatain,
									cdataout );
							
						} else if (bootstrapProtocol.equals( LogConstants.LOG_PROTOCOL_NAME )) {
							ClientLogHandler.createAndStart( new ProtocolStartUpInformation(session), cdatain, cdataout );
						} else if (bootstrapProtocol.equals( InstallUtils.UPDATE_PROTOCOL_NAME ) ) {										
							UpdateProtocolFactory.INSTANCE.createAndStart( new ProtocolStartUpInformation(session), cdatain, cdataout );
						} else if (bootstrapProtocol.equals( InstallUtils.UPDATE_FILE_TRANSFER_PROTOCOL_NAME ) ) {
							FilesUploader.createAndStart( new NetworkConnection( session, cdatain, cdataout )  );
						} else if (bootstrapProtocol.equals( SimpleFileTransferUtils.DOWNLOAD_PROTOCOL_NAME ) ) {
							FileDownloadHandler.createAndStart( new NetworkConnection( session, cdatain, cdataout ) );
						} else if (bootstrapProtocol
								.equals("OnlyOpenConnection")) {

							logger
									.info("was only open connection....this was always a problem");

							

						} else if (bootstrapProtocol.equals("StartByteRouter")) {

							logger.info("Start byte router in ss");

							Hashtable bootStrapInfo = (Hashtable) session
									.get("bootStrapInfo");
							session.remove("bootStrapInfo");

							String senderOrReceiver = (String) bootStrapInfo
									.get("senderOrReceiver");
							String extraInfo = (String) bootStrapInfo
									.get("extraInfo");

							if (extraInfo == null) {

								extraInfo = "none";
							}

							Document doc = (Document) bootStrapInfo.get("doc");

							logger.info("DOC in start byte router..."
									+ doc.asXML());
							logger.info("SENDER OR RECEIVER?? "
									+ senderOrReceiver);
							logger
									.info("have to start byte router on server twice...this time doing it for: "
											+ senderOrReceiver
											+ " and its session is "
											+ (String) session.get("session"));

							final ByteRouter br = new ByteRouter(session,
									(String) session.get("session"), doc
											.getRootElement().element(
													"physical_location")
											.attributeValue("value"), cdatain,
									cdataout, senderOrReceiver, doc, extraInfo);


						} else if (bootstrapProtocol.equals("PutBinary")) {

							AbstractBinaryServerTransmitter tr = new PutBinaryServerTransmitter(cdataout, cdatain, session);
							tr.transmit();

						} else if (bootstrapProtocol.equals("GetBinary")) {

							AbstractBinaryServerTransmitter tr = new GetBinaryServerTransmitter(cdataout, cdatain, session);
							tr.transmit();

						} else if (bootstrapProtocol
								.equals("DownloadMultipleFiles")) {
							
							AbstractBinaryServerTransmitter tr = new MultipleDownloadBinaryServerTransmitter(cdataout, cdatain, session);
							tr.transmit();

						} else if (bootstrapProtocol.equals("SaveBinary")) {

							AbstractBinaryServerTransmitter tr = new SaveBinaryServerTransmitter(cdataout, cdatain, session);
							tr.transmit();

						} else if (bootstrapProtocol.equals("DialogsMainCli") || bootstrapProtocol.equals("WebDialogsMainCli")) {
							sphereURL = setUpDialogsMain(sphereName, invite, changePw, sphereURL, xmldb, inviteSphereName, inviteSSName, changeToLogin, loginSphere, loginSphereId, changePassphraseNextLogin, m1, cdataout, cdatain, session);
							
						} else if (bootstrapProtocol.equals("SimplestClient")) {

							logger.error( "SimplestClient is not supported" );
							logger.info("Request for bootstrap: "
									+ bootstrapProtocol);

							logger.info("Setting bootstrap"
									+ (String) session.get("real_name"));

							/*
							 * EstServer server = new
							 * EstServer(session,cdatain,cdataout);
							 * 
							 * logger.info("in server right here");
							 * 
							 * VerifyAuth verifyAuth = new VerifyAuth(session);
							 * System.out.println("******* STETTTING SPHERE
							 * DOC");
							 * 
							 * verifyAuth.setSphereDocument(xmldb.getSphereDoc((String)
							 * session.get("supra_sphere")));
							 * 
							 * logger.info("in server here");
							 * 
							 * objectBytes = objectToBytes(verifyAuth);
							 * 
							 * SSLogger.getLogger().info("OBJECT SIZE:
							 * "+objectBytes.length);
							 * server.cdataout.writeInt(objectBytes.length);
							 * server.cdataout.write(objectBytes);
							 * 
							 * SSLogger.getLogger().info("second est server
							 * started"); break;
							 * 
							 */

						} else {
							logger.error("Unknown bootsup protocol: " + bootstrapProtocol + ", close socket" );
							this.client.close();
						}
					} else {
						if(contactLocked.equals("true")) {
							this.dataout.writeUTF("locked");
						} else {
							this.dataout.writeUTF("unreal");
						}
					}
				}
		} catch (IOException ex) {
			logger.error("ZKA failed", ex);
		} catch (ClassNotFoundException ex) {
			logger.error("ZKA failed",ex);
		}
	}

	/**
	 * @param sphereName
	 * @param invite
	 * @param changePw
	 * @param sphereURL
	 * @param xmldb
	 * @param inviteSphereName
	 * @param inviteSSName
	 * @param changeToLogin
	 * @param loginSphere
	 * @param loginSphereId
	 * @param changePassphraseNextLogin
	 * @param m1
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	private String setUpDialogsMain(String sphereName, String invite, String changePw, String sphereURL, XMLDB xmldb, String inviteSphereName, String inviteSSName, String changeToLogin, LoginSphere loginSphere, String loginSphereId, String changePassphraseNextLogin, String m1, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) throws IOException, DocumentException {
		logger.warn("setUpDialogsMain. changePw: " + changePw
				+ ", changePassphraseNextLogin: " + changePassphraseNextLogin + ", invite: " + invite );
		if (changePw.equals("changePassphraseNextLogin")) {
			setUpDialogsMainChangePwChangePassphraseNextLogin(sphereName, xmldb, loginSphere, cdataout, cdatain, session);
		} else if (changePassphraseNextLogin.equals("true")) {
			setUpDialogsMainChangePassphraseNextLoginTrue(sphereName, m1, cdataout, cdatain, session);
		} else if (invite.equals("true")) {
			setUpDialogsMainInviteTrue(sphereName, inviteSphereName, inviteSSName, changeToLogin, loginSphereId, cdataout, cdatain, session);
		}
		else if (invite.equals("transferCredentials")) {
			sphereURL = setUpDialogsMainInviteTransferCredentials(sphereURL, xmldb, inviteSSName, loginSphereId, cdataout, cdatain, session);				
		} else if (!invite.equals("true")) {
			setUpDialogsMainInviteNotTrue(sphereName, xmldb, cdataout, cdatain, session);		
		}
		else {
			logger.error( "Unknown setup args" );
		}
		logger.warn("Breaking...");
		return sphereURL;
	}

	/**
	 * @param sphereName
	 * @param xmldb
	 * @param loginSphere
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void setUpDialogsMainChangePwChangePassphraseNextLogin(String sphereName, XMLDB xmldb, LoginSphere loginSphere, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) throws IOException {
		String oldUsername = cdatain.readUTF();
		String newUsername = cdatain.readUTF();
		String passphraseChangeSession = cdatain
				.readUTF();
		logger.info("new username: " + newUsername);
		String newSalt = cdatain.readUTF();
		String newVerifier = cdatain.readUTF();

		logger.info("new creds..." + newSalt + " : "
				+ newVerifier);
		
		DialogsMainPeer cont = new DialogsMainPeer(
				session, cdatain, cdataout);

		cont.registryHandlerAndStart(sphereName, newUsername,
				(String) session.get("session"));

		final boolean isUserExist = cont
				.getVerifyAuth().isUserExist(
						newUsername);

		logger.info("EXISTING USERNAME WAS ALREADY THERE...."
						+ newUsername);
		// Consider changing to only change passphrase

		boolean sameUsername = false;
		try {
			if (oldUsername.equals(newUsername)) {
				if (cont.checkAndRemovePassphraseChangeSession(passphraseChangeSession)) {
					sameUsername = true;
				} else {
					logger.error("DID NOT CONTAIN: "
							+ passphraseChangeSession);
				}
			}
		} catch (NullPointerException ex) {
			logger.error( "user name compare failed", ex );
		}

		if (!isUserExist || sameUsername) {
			// oldUsername = username;
			SupraSphereProvider.INSTANCE.get( cont,IReplaceUsernameInMembership.class).replaceUserNameInMembership2(xmldb, loginSphere, session,
					oldUsername, newUsername, newSalt, newVerifier, cont);
			cont.sendBootstrapComplete();
			Document contactDoc = xmldb.getContactDoc(
					loginSphere.getSystemName(),
					oldUsername);
			if (logger.isDebugEnabled()) {
				logger.debug( "New contact document " + XmlDocumentUtils.toPrettyString( contactDoc ) );
			}
			cont.replaceAndUpdateAllLocations(session,
					contactDoc);
			
//			VerifyAuth newVerify = new VerifyAuth(
//					session);
//			newVerify.setSphereDocument(supraSphereDoc);
//			cont.sendDefinitionMessages(session,
//					supraSphereDoc, newVerify, "false");
//
//			cont.sendDefinitionMessages(session,
//					supraSphereDoc, newVerify, "false");
	
			

		} else {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue("protocol",
					"changePassphraseNextLogin");
			logger
					.info("Sending changePassphraseNextLogin"
							+ (String) session
									.get("username")
							+ (String) session
									.get("sphereURL"));

			dmpResponse.setStringValue("tempUsername",
					(String) session.get("username"));
			dmpResponse.setStringValue(
					"dontChangeOriginal", "true");
			logger
					.info("about to send changepassphraseNext login...");
			cont.sendFromQueue(dmpResponse);
			cont.dispose();
			cont = null;
		}
	}

	

	/**
	 * @param sphereName
	 * @param m1
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 */
	private void setUpDialogsMainChangePassphraseNextLoginTrue(String sphereName, String m1, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) {
		DialogsMainPeer cont = new DialogsMainPeer(
				session, cdatain, cdataout);

		cont.registryHandlerAndStart(sphereName,
				(String) session.get("username"),
				(String) session.get("session"));

		logger.info("set handler as!!!!: "
				+ cont.getName());

		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue("protocol",
				"changePassphraseNextLogin");
		logger.info("Sending changePassphraseNextLogin"
				+ (String) session.get("username")
				+ (String) session.get("sphereURL"));
		dmpResponse.setStringValue("tempUsername",
				(String) session.get("username"));
		logger.info("about to send changepassphraseNext login...");

		cont.registerSessionForPassphraseChange(m1);
		cont.sendFromQueue(dmpResponse);
		cont.dispose();
		cont = null;
	}

	/**
	 * @param sphereName
	 * @param inviteSphereName
	 * @param inviteSSName
	 * @param changeToLogin
	 * @param loginSphereId
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 */
	private void setUpDialogsMainInviteTrue(String sphereName, String inviteSphereName, String inviteSSName, String changeToLogin, String loginSphereId, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) {
		logger.info("starting peer");
		DialogsMainPeer cont = new DialogsMainPeer(
				session, cdatain, cdataout);

		cont.registryHandlerAndStart(sphereName,
				(String) session.get("username"),
				(String) session.get("session"));

		logger.info("set handler as!!!!: "
				+ cont.getName());

		if (changeToLogin != null) {

			if (changeToLogin.equals("false")) {

				logger.warn("prompt");

			} else {

				logger.warn("changed to changetologin");

			}

		} else {
			logger.warn("ctl was null");

		}
		String URL = (String) session.get("sphereURL");
		String sphere = (String) session
				.get("sphere_id");

		logger.info("the sphere is this : "
				+ inviteSphereName);
		logger.info("this is the supraspherename: "
				+ inviteSSName);

		URL = URL.replace("invite", "sphere");
		URL = URL.replace(sphere, inviteSSName);
		String tempUsername = (String) session
				.get("username");
		if ((changeToLogin != null)
				&& (changeToLogin.equals("true"))) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue("protocol",
					"changeToLogin");
			dmpResponse.setStringValue("tempUsername",
					tempUsername);
			dmpResponse
					.setStringValue("inviteURL", URL);
			dmpResponse.setStringValue("loginSphere",
					loginSphereId);
			cont.sendFromQueue(dmpResponse);
		} else {
			cont.sendPromptPassphraseChange(
					loginSphereId, URL, tempUsername);
		}

		cont.dispose();
		cont = null;
	}

	/**
	 * @param sphereURL
	 * @param xmldb
	 * @param inviteSSName
	 * @param loginSphereId
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	private String setUpDialogsMainInviteTransferCredentials(String sphereURL, XMLDB xmldb, String inviteSSName, String loginSphereId, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) throws DocumentException {
		String saveSphereURL = (String) session
				.get("sphereURL");

		if (sphereURL.startsWith("invite")) {
			sphereURL = VariousUtils
					.convertInviteURLtoSphereURL(
							inviteSSName, sphereURL);
		} else {
			sphereURL = VariousUtils
					.convertInviteURLtoSphereURL(
							inviteSSName,
							(String) session
									.get("inviteURL"));
		}
		session.put("sphereURL", sphereURL);
		logger
				.warn("right before transfer credentials...");

		try {

			logger
					.warn("right after transfer credentials..."
							+ this.invitingContactDoc
									.asXML());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// session.remove("invitingContactDoc");
		session.put("supra_sphere", inviteSSName);

		String wholeSphere = (String) session
				.get("sphere_id");

		// sphereURLToChange =
		// sphereURLToChange.replace("wholeSphere",inviteSSName);
		// session.put("sphereURL",sphereURLToChange);
		logger.warn("whole sphere: " + wholeSphere);
		try {
			StringTokenizer st = new StringTokenizer(
					wholeSphere, ".");
			String sphereId = st.nextToken();
			session.remove("sphere_id");
			session.put("sphere_id", sphereId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		String sphereCore = xmldb.getUtils()
				.getSphereCore(session);

		logger.warn("sphere core..." + sphereCore);

		// session.put("sphere_id",sphereCore)

		session.put("sphere_core", sphereCore);

		DialogsMainPeer cont = new DialogsMainPeer(
				session, cdatain, cdataout);

		cont.registryHandlerAndStart(inviteSSName,
				(String) session.get("username"),
				(String) session.get("session"));

		Document contactDoc = (Document) session
				.get("contactDoc");

		VerifyAuth verifyAuth = new VerifyAuth(session);
		logger.warn("sphere: "
				+ (String) session.get("sphere_id"));

		if (sphereCore == null) {
			sphereCore = (String) session
					.get("sphere_id");
		} else {

			session.put("sphere_id", sphereCore);
			logger
					.info("SPhere core was not null....need to set suprasphere somehow...."
							+ sphereCore
							+ " : "
							+ (String) session
									.get("supra_sphere"));
			// /session.put("supra_sphere",sphereCore);

		}

		Document sphereDefinition = xmldb
				.getSphereDefinition((String) session
						.get("supra_sphere"),
						sphereCore);

		/*
		 * Document contactDocument =
		 * xmldb.getMyContact( (String)
		 * session.get("real_name"), loginSphereId);
		 */

		Document contactDocument = xmldb.getContactDoc(
				loginSphereId, (String) session
						.get("username"));

		contactDocument.getRootElement().element(
				"home_sphere").addAttribute("value",
				saveSphereURL);
		contactDocument.getRootElement().addElement(
				"reciprocal_login").addAttribute(
				"value",
				this.invitingContactDoc
						.getRootElement().element(
								"message_id")
						.attributeValue("value"));

		xmldb
				.replaceDoc(contactDocument,
						loginSphereId);
		String contactDocumentMessageId = contactDocument
				.getRootElement().element("message_id")
				.attributeValue("value");

		SupraSphereProvider.INSTANCE.configureVerifyAuth(verifyAuth);
		verifyAuth.setContactDocument(contactDocument);

		cont.setVerifyAuth(verifyAuth);
		cont.updateAuthOfOtherPeers();

		try {

			Document personInvitedFromThemDoc = (Document) session
					.get("contactDoc");
			personInvitedFromThemDoc.getRootElement()
					.element("login").addAttribute(
							"value",
							(String) session
									.get("username"));
			personInvitedFromThemDoc
					.getRootElement()
					.addElement("reciprocal_login")
					.addAttribute(
							"value",
							this.invitingContactDoc
									.getRootElement()
									.element(
											"message_id")
									.attributeValue(
											"value"));
			personInvitedFromThemDoc.getRootElement()
					.element("home_sphere")
					.addAttribute("value",
							saveSphereURL);

			Document specificDoc = xmldb.getSpecificID(
					(String) session.get("sphere_id"),
					contactDocumentMessageId);

			if (specificDoc == null) {

				// logger.warn("NULL...");
				// System.exit(0);

			}

			cont.getXmldb().removeDoc(specificDoc,
					(String) session.get("sphere_id"));

			Document again = cont
					.getXmldb()
					.getSpecificID(
							(String) session
									.get("sphere_id"),
							specificDoc
									.getRootElement()
									.element(
											"message_id")
									.attributeValue(
											"value"));

			cont.getXmldb().insertDoc(contactDoc,
					(String) session.get("sphere_id"));

			session.put("firstDoc", contactDoc);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}

		if (sphereDefinition == null) {
			logger
					.warn("sphere definition is null.....not sure what it does otherwise "
							+ verifyAuth
									.getDisplayName(sphereCore)
							+ " : " + sphereCore);
			SphereDefinitionCreator sdc = new SphereDefinitionCreator();

			sphereDefinition = sdc
					.createDefinition(
							verifyAuth
									.getDisplayName(sphereCore),
							sphereCore);

			logger.info("DEF: "
					+ sphereDefinition.asXML());

		}

		logger
				.warn("got here...will now send update...send contact and can do all that then...");

		String contactLogin = this.invitingContactDoc
				.getRootElement().element("login")
				.attributeValue("value");
		String login = verifyAuth
				.getLoginSphere(contactLogin);
		Document memDoc = xmldb.getMembershipDoc(login,
				contactLogin);

		String s = memDoc.getRootElement().element(
				"machine_verifier").attributeValue(
				"salt");
		String v = memDoc.getRootElement().element(
				"machine_verifier").getText();

		this.invitingContactDoc = XMLSchemaTransform
				.removeLocations(this.invitingContactDoc);

		this.invitingContactDoc.getRootElement()
				.element("login").addAttribute("value",
						"");

		this.invitingContactDoc.getRootElement()
				.addElement("reciprocal_login")
				.addAttribute(
						"value",
						(String) session
								.get("username"));

		this.invitingContactDoc
				.getRootElement()
				.element("home_sphere")
				.addAttribute(
						"value",
						"sphere::"
								+ (String) session
										.get("address")
								+ ":"
								+ (String) session
										.get("port")
								+ ","
								+ (String) session
										.get("supra_sphere"));
		String messageId = this.invitingContactDoc
				.getRootElement().element("message_id")
				.attributeValue("value");

		cont.sendInviteComplete(
				this.invitingContactDoc, verifyAuth, s,
				v, messageId);
		
		cont.sendDefinitionMessages(session,
				sphereDefinition, verifyAuth, "false");
		logger.warn("sent update...");
		return sphereURL;
	}

	/**
	 * @param sphereName
	 * @param xmldb
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 */
	@SuppressWarnings("unchecked")
	private void setUpDialogsMainInviteNotTrue(String sphereName, XMLDB xmldb, final DataOutputStream cdataout, final DataInputStream cdatain, final Hashtable session) {
		logger.info("starting peer");
		DialogsMainPeer cont = new DialogsMainPeer(
				session, cdatain, cdataout);

		cont.registryHandlerAndStart(sphereName,
				(String) session.get("username"),
				(String) session.get("session"));

		logger.info("set handler as!!!!: "
				+ cont.getName());

		String wholeSphere = (String) session
				.get("sphere_id");
		StringTokenizer st = new StringTokenizer(
				wholeSphere, ".");
		String sphereId = st.nextToken();
		session.put("sphere_id", sphereId);

		String sphereCore = xmldb.getUtils()
				.getSphereCore(session);

		logger
				.info("SPHERE CORE....get summary from here and create blank def if null: "
						+ sphereCore
						+ (String) session
								.get("sphere_id"));

		if (sphereCore == null) {
			sphereCore = (String) session
					.get("sphere_id");
		} else {
 			session.put("sphere_core", sphereCore);
			session.put("sphere_id", sphereCore);
			logger
					.info("SPhere core was not null....need to set suprasphere somehow...."
							+ sphereCore
							+ " : "
							+ (String) session
									.get("supra_sphere"));

		}

		logger.info("got here...");
		cont.sendBootstrapComplete();
	}

	private Hashtable getMFromData(String verifier, String A) {

		Hashtable<String, String> returned = new Hashtable<String, String>();
		BigInteger v = new BigInteger(verifier);

		Random r = new Random();
		int b = r.nextInt();

		while (b <= 1) {
			b = r.nextInt();
		}
		Integer int_b = new Integer(b);

		BigInteger gtob = this.g.modPow(new BigInteger(int_b.toString()),
				this.n);

		BigInteger vprime = v.mod(this.n);
		BigInteger B = vprime.add(gtob);

		int u = r.nextInt();
		if (u < 0) {
			u = -u;
		}
		Integer intU = new Integer(u);

		/*
		 * Compute S
		 */
		BigInteger vtou = v.modPow(new BigInteger(intU.toString()), this.n);
		BigInteger prod = vtou.multiply(new BigInteger(A));
		BigInteger S = prod.modPow(new BigInteger(int_b.toString()), this.n);

		/*
		 * Finally verifyAuth our client
		 */
		BigInteger k = this.getHashOf(new String(S.toString()));

		BigInteger m = this.getHashOf(new String(A + B.toString()
				+ k.toString()));
		returned.put("m", m.toString());
		returned.put("bString", B.toString());
		returned.put("uString", intU.toString());

		return returned;
	}

	private BigInteger getHashOf(String s) {
		// We use password based encrytption (with MD5 and DES) to hash s to a
		// large number
		byte[] cleartext = s.getBytes();
		byte[] ciphertext = { 0 };
		try {
			ciphertext = this.pbeCipher.doFinal(cleartext);
		} catch (Exception e) {

		}
		return new BigInteger(ciphertext).abs();
	}

	@SuppressWarnings("unused")
	private byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		return baos.toByteArray();
	}

	/**
	 * Converts a byte array to a serializable object.
	 */
	private Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream is = new ObjectInputStream(bais);
		return is.readObject();
	}
}
