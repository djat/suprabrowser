package ss.client.networking;

/*
 SimplestClient.java
 Created on March 10, 2001, 3:01 PM
 The client for transferring files and a couple of other small things relating to administration with spheres
 and personas. Used to be used for almost everything.
 */

/**
 * @author     administrator
 * @version
 */

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.dom4j.Document;
import org.eclipse.swt.widgets.Shell;

import ss.client.networking.binary.AbstractBinaryClientTransmitter;
import ss.client.networking.binary.GetBinaryClientTransmitter;
import ss.client.networking.binary.MultipleDowunloadBinaryClientTransmitter;
import ss.client.networking.binary.PutBinaryClientTransmitter;
import ss.client.networking.binary.SaveBinaryClientTransmitter;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.LockInfoDialog;
import ss.common.CreateMembership;
import ss.common.EncryptedSocket;
import ss.common.InstallUtils;
import ss.common.MapUtils;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.common.debug.DebugUtils;
import ss.common.domainmodel2.AbstractClientDataProviderConnector;
import ss.common.presence.PresenceUtils;
import ss.common.simplefiletransfer.SimpleFileTransferUtils;
import ss.framework.errorreporting.LogConstants;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * Description of the Class
 * 
 * @author david
 * @created April 1, 2004
 */

public class SupraClient {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraClient.class);

	private static Hashtable encryptedSockets = new Hashtable();

	/**
	 * Description of the Field
	 */
	protected static final BigInteger n = new BigInteger(
			"122401836212692490420534044542160652060089063288916259378992106298635997629004871449040863148842028332936921964140316531574725544295868627297697141329665138830185297018653829634397774436198765430305661897599443877203922836563409947444887027604848997125383124008931080480197775021431253630976071993683966590129");

	/**
	 * Description of the Field
	 */
	protected static final BigInteger g = new BigInteger(
			"164438241317367690823401305357370607328034430023459713340335187782878205902844516952738722877059454685668830995765089402230495206772108704026831761904445241536185572511706087329237156570564175356155697011727697175323622673030696046933358292860718413231660423149569049278930510073740541237052508995182528917163");

	private Cipher cEncrypt;

	private Cipher cDecrypt;

	private DataOutputStream dataout = null;

	private DataInputStream datain = null;

	private DataOutputStream cdataout = null;

	private DataInputStream cdatain = null;

	private InputStream in = null;

	private OutputStream out = null;

	private Socket clientSocket;

	private Hashtable update = new Hashtable();

	private PBEKeySpec pbeKeySpec;

	private PBEParameterSpec pbeParamSpec;

	private SecretKeyFactory keyFac;

	private Cipher pbeCipher;

	private SupraSphereFrame sF = null;

	private String passphrase = null;

	private Shell shellToDispose;
	
	private boolean firstZkaRun = true;

	public SupraClient() {
	}

	/**
	 * Constructor for the SimplestClient object
	 * 
	 * @param netAddress
	 *            Description of the Parameter
	 * @param port
	 *            Description of the Parameter
	 * @param userSession
	 *            Description of the Parameter
	 */
	public SupraClient(String address, String portString) { // Used for
		// inserting
		// directly on the
		// same system

		int port = new Integer(portString).intValue();

		logger.warn("Starting client to : " + address + " on : " + port);

		java.security.Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Provider sunJCE = new com.sun.crypto.provider.SunJCE();
		// Security.addProvider(sunJCE);

		// Salt for our encryption

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
			// arbitrary plaintext, but must be the same at the server end
			// keyFac =
			// SecretKeyFactory.getInstance("PBEWithSHAAndTwoFish-CBC"); //we
			// are specifying public block encryption with MD5 and DES
			this.keyFac = SecretKeyFactory.getInstance(
					"PBEWithSHAAndTwofish-CBC", "BC");
			// we are specifying public block encryption with MD5 and DES
			// keyFac =
			// SecretKeyFactory.getInstance("PBEWithHmacSHA1AndDESede");
			SecretKey pbeKey = this.keyFac.generateSecret(this.pbeKeySpec);

			// Create PBE Cipher
			this.pbeCipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC",
					"BC");
			// pbeCipher = Cipher.getInstance("PBEWithSHAAndTwoFish-CBC");
			// pbeCipher = Cipher.getInstance("PBEWithHmacSHA1AndDESede");
			// Initialize PBE Cipher with key and parameters
			this.pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, this.pbeParamSpec);
		} catch (Exception ex) {
			logger.error("Can't initialize crypt", ex);
		}
		try {
			try {
				this.clientSocket = new Socket(address, port);
				this.clientSocket.setKeepAlive(true);
			} catch (Throwable uhe) {
				logger.error( "Can't create socket", uhe);
			}
			if ( this.clientSocket != null ) {
				this.out = this.clientSocket.getOutputStream();
				this.in = this.clientSocket.getInputStream();
			}
		} catch (IOException ioe) {
			logger.error( "Can't establish connection", ioe );
		}
		this.dataout = new DataOutputStream(this.out);
		this.datain = new DataInputStream(this.in);
	}

	public BigInteger getHashOf(String s) {
		// We use password based encrytption (with MD5 and DES) to hash s to a
		// large number
		byte[] cleartext = s.getBytes();
		byte[] ciphertext = { 0 };
		try {
			ciphertext = this.pbeCipher.doFinal(cleartext);
		} catch (Exception e) {
			// logger.info("Error encrypting in cli: " + e);
		}
		return new BigInteger(ciphertext).abs();
	}


	public static void main(String[] args) {
		SupraClient sc = new SupraClient();

		Hashtable loginSession = sc.loadMachineServerAuthProperties();
		sc.startZeroKnowledgeAuth(loginSession, "DeliverMail");

	}

	private void init(String address, String portString) {
		int port = new Integer(portString).intValue();

		logger.info("Starting client to : " + address + " on : " + port);

		java.security.Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Provider sunJCE = new com.sun.crypto.provider.SunJCE();
		// Security.addProvider(sunJCE);

		// Salt for our encryption

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
			// arbitrary plaintext, but must be the same at the server end
			// keyFac =
			// SecretKeyFactory.getInstance("PBEWithSHAAndTwoFish-CBC"); //we
			// are specifying public block encryption with MD5 and DES
			this.keyFac = SecretKeyFactory.getInstance(
					"PBEWithSHAAndTwofish-CBC", "BC");
			// we are specifying public block encryption with MD5 and DES
			// keyFac =
			// SecretKeyFactory.getInstance("PBEWithHmacSHA1AndDESede");
			SecretKey pbeKey = this.keyFac.generateSecret(this.pbeKeySpec);

			// Create PBE Cipher
			this.pbeCipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC",
					"BC");
			// pbeCipher = Cipher.getInstance("PBEWithSHAAndTwoFish-CBC");
			// pbeCipher = Cipher.getInstance("PBEWithHmacSHA1AndDESede");
			// Initialize PBE Cipher with key and parameters
			this.pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, this.pbeParamSpec);
		} catch (Exception e) {

		}

		try {

			try {

				this.clientSocket = new Socket(address, port);
				this.clientSocket.setKeepAlive(true);

			} catch (Exception uhe) {
				logger.error(uhe.getMessage(), uhe);
			}

			// synchronized (clientSocket) {
			this.out = this.clientSocket.getOutputStream();
			this.in = this.clientSocket.getInputStream();

			// }

		} catch (IOException ioe) {

		}
		this.dataout = new DataOutputStream(this.out);
		this.datain = new DataInputStream(this.in);
	}

	public Hashtable loadMachineServerAuthProperties() {
		return loadMachineServerAuthProperties( new File(System.getProperty("user.dir") ) );	
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable loadMachineServerAuthProperties(final File configDir )  {
		if (configDir == null) {
			throw new NullPointerException("configDir");
		}
		logger.debug("Here in startzeroknowledgemachingserverauth");
		String port = null;
		String supraSphereName = null;
		File dynServerFile = new File(configDir, "dyn_server.xml" );
		File serverLoginFile = new File(configDir, "server_login.xml");
		try {
			final Document dynServerDoc = XmlDocumentUtils.load( dynServerFile);
			port = dynServerDoc.getRootElement().element("port").attributeValue("value");
			final Document serverLoginDoc = XmlDocumentUtils.load( serverLoginFile);
			supraSphereName = serverLoginDoc.getRootElement().element("prev_logins")
					.element("login").attributeValue("username");
			setPassphrase(serverLoginDoc.getRootElement().element("prev_logins").element(
					"login").attributeValue("passphrase"));
		} catch (Exception ex) {
			logger.error("Can't find ", ex);
		}
		init("127.0.0.1", port);

		Hashtable loginSession = new Hashtable();

		loginSession.put("address", "127.0.0.1");
		loginSession.put("username", supraSphereName);
		loginSession.put("passphrase", getPassphrase());
		loginSession.put("sphere_id", supraSphereName);
		loginSession.put("supra_sphere", supraSphereName);
		loginSession.put("port", port);
		loginSession.put("use_machine_verifier", "true");
		loginSession.put("changePw", "false");

		loginSession.put("invite", "false");

		String sphereURL = "sphere::127.0.0.1:" + port + "," + supraSphereName;
		loginSession.put("sphereURL", sphereURL);
		return loginSession;
	}

	public void setShellToDispose(Shell shell) {
		this.shellToDispose = shell;
	}

	@SuppressWarnings("unchecked")
	public Object startZeroKnowledgeAuth(final Hashtable session,
			String networkingClassName) {
		
		synchronized( this ) {
			if ( !this.firstZkaRun ) {
				throw new IllegalStateException( "Not first ZKA call" );
			}
			this.firstZkaRun = false;
		}
		
		logger.warn("Start zka with ncn: " + networkingClassName);
		logger.info("INVITE/SPHERE URL BEGINNING: "
				+ (String) session.get("sphereURL"));

		String sphereName = (String) session.get("sphere_id");
		
		logger.info( "start zka for sphere : " + sphereName);

		String username = (String) session.get("username");
		logger.warn("username when starting: " + username);

		setPassphrase((String) session.get("passphrase"));

		if (getPassphrase() == null && this.sF!=null) {
			setPassphrase(this.sF.getTempPasswords().getTempPW(
					((String) session.get("supra_sphere"))));
		}

		String useMachineVerifier = (String) session
				.get("use_machine_verifier");

		String supraSphereName = (String) session.get("supra_sphere");

		final String invite = (String) session.get("invite");

		String changePw = (String) session.get("changePw");

		final String profileId = MapUtils.getValue( session, "profile_id", "0000000000000000000" );

		String sphereURL = (String) session.get("sphereURL");


		if (getPassphrase() != null && this.sF != null) {

			logger.warn("SS This far: " + (String) session.get("supra_sphere"));
			this.sF.getTempPasswords().setTempPW(
					((String) session.get("supra_sphere")), getPassphrase());

		}

		try {
			session.remove("passphrase");
		} catch (Exception e) {

		}

		Object returnObject = null;
		

		try {
			try {
				this.dataout.writeInt(0);
			} catch (NullPointerException npe) {
				String externalConnection = (String) session
						.get("externalConnection");

				boolean restartLogin = true;

				if (externalConnection != null) {
					if (externalConnection.equals("true")) {
						restartLogin = false;
					}
				}

				if (restartLogin && this.sF!=null) {
					this.sF.getWelcomeScreen().showMessage(
							"Cannot connect to server!");

					this.sF.disposeButDontRemove();

					new Thread() {
						@Override
						public void run() {
							try {
								sleep(1500);

								SupraClient.this.sF
										.getWelcomeScreen()
										.setPromptPassphrase(
												"Call 617-407-5149 if repeated attempts fail");
								SupraClient.this.sF.getWelcomeScreen()
										.loadVerifierOnly();
								SupraClient.this.sF.getWelcomeScreen()
										.layoutGUI();

							} catch (InterruptedException ex) {
								logger.error(ex);
							}

						}

					}.start();
				}

				logger.error( "Nullpointer from dataout", npe);
				return null;
			}

				logger.info("starting");

				this.dataout.writeUTF(username);

				this.dataout.writeUTF(sphereName);
				logger.warn("wrote : " + sphereName);
				this.dataout.writeUTF(supraSphereName);
				this.dataout.writeUTF(useMachineVerifier);

				this.dataout.writeUTF(invite);
				if (changePw == null) {
					changePw = "false";
				}
				this.dataout.writeUTF(changePw);
				this.dataout.writeUTF(profileId);
				if (sphereURL != null) {
					this.dataout.writeUTF(sphereURL);
				} else {
					this.dataout.writeUTF("SupraSphere");
				}

				if (changePw.equals("true")) {

					logger.info("Changing pw true");
					CreateMembership membership = new CreateMembership();
					// AddMembership membership = new AddMembership();

					Document memDoc = membership.createMember(username,
							username, getPassphrase());

					logger.info("memdoc; " + memDoc.asXML());

					this.dataout.writeUTF((String) session.get("tempUsername"));
					this.dataout.writeUTF(memDoc.getRootElement().element(
							"verifier").attributeValue("salt"));
					this.dataout.writeUTF(memDoc.getRootElement().element(
							"verifier").getText());
					this.dataout.writeUTF((String) session.get("loginSphere"));

				} else if (changePw.equals("changePassphraseNextLogin")) {

					/*
					 * logger.info("Changing passphrase next login true");
					 * CreateMembership membership = new CreateMembership();
					 * //AddMembership membership = new AddMembership();
					 * 
					 * String newUsername = (String)session.get("newUsername");
					 * String newPassphrase =
					 * (String)session.get("newPassphrase");
					 * 
					 * 
					 * 
					 * session.remove("newUsername");
					 * session.remove("newPassphrase");
					 * 
					 * 
					 * Document memDoc =
					 * membership.createMember(username,newUsername,newPassphrase);
					 * 
					 * logger.info("memdoc; "+memDoc.asXML());
					 * 
					 * 
					 * dataout.writeUTF((String)session.get("username"));
					 * 
					 * 
					 * dataout.writeUTF(memDoc.getRootElement().element("verifier").attributeValue("salt"));
					 * dataout.writeUTF(memDoc.getRootElement().element("verifier").getText());
					 */

				} else {

					logger.info("Change pw not true....");
					logger.info("VALUES: " + changePw + " : " + invite);
				}

				if (invite.equals("acceptAndChange")) {

				} else if (invite.equals("transferCredentials")) { // Not
					// really
					// used
					// yet...for
					// accepting
					// invitation
					// url
					// inside
					// the
					// application

					logger.warn("starting transfer credentials");

					String machineVerifier = (String) session
							.get("machineVerifier");
					String machineSalt = (String) session.get("machineSalt");
					String machineProfile = (String) session
							.get("machineProfile");
					String inviteURL = (String) session.get("inviteURL");

					this.dataout.writeUTF(machineSalt);
					this.dataout.writeUTF(machineVerifier);
					this.dataout.writeUTF(machineProfile);
					this.dataout.writeUTF(inviteURL);
					String inviteSSName = this.datain.readUTF();
					String sessionUsername = this.datain.readUTF();
					logger.warn("WILL REPLACE WITH THIS USERNAME: "
							+ sessionUsername);

					String mainSS = this.sF.getSurpaSphereId();

					if (getPassphrase() == null) {
						logger.warn("Passphrase was null...");

						setPassphrase(this.sF.getTempPasswords().getTempPW(
								mainSS));

					}

					session.remove("username");
					session.put("username", sessionUsername);
					logger.warn("transferred : " + inviteSSName);
					session.put("supra_sphere", inviteSSName);

				} else {

					logger.info("was not accept and change");
				}

				String salt = this.datain.readUTF();
				logger.warn("salt: " + salt);

				if (salt.equals("promptLogin")) {

					this.sF.closeFromWithin();
					final String newSphereURL = this.datain.readUTF();

					logger.warn("GOT THIS newsphereurl: " + newSphereURL);

					new Thread() {
						private SupraClient client = SupraClient.this;

						public void run() {

							this.client.sF.getWelcomeScreen()
									.disposeInvitation();

							this.client.sF.getWelcomeScreen()
									.setFirstSessionId(
											(String) session.get("session"));

							this.client.sF.getWelcomeScreen().saveNewUrl(
									newSphereURL);

							this.client.sF.getWelcomeScreen().setInitialSphereUrl(
									newSphereURL);

							this.client.sF.getWelcomeScreen()
									.setSupraSphereFrame(this.client.sF);
							this.client.sF
									.getWelcomeScreen()
									.setPromptPassphrase(
											"Please provide the username and passphrase given to you");

							this.client.sF.getWelcomeScreen().layoutGUI();
						}
					}.start();

				} else if (!salt.equals("unreal") && !salt.equals("locked")) {
					// Succeeded

					logger.warn("was not unreal");
					String realName = this.datain.readUTF();

					logger.warn("real name: " + realName);

					String changePassphraseNextLogin = this.datain.readUTF();

					logger.warn("change passphrase: "
							+ changePassphraseNextLogin);
					logger.warn("username: " + realName);

					BigInteger x = getHashOf(new String(salt + getPassphrase()));

					Random r = new Random();
					int a = r.nextInt();
					while (a <= 1) {
						a = r.nextInt();
					}
					Integer intA1 = new Integer(a);

					r = new Random();
					a = r.nextInt();
					while (a <= 1) {
						a = r.nextInt();
					}

					Integer intA2 = new Integer(a);

					r = new Random();
					a = r.nextInt();
					while (a <= 1) {
						a = r.nextInt();
					}

					Integer intA3 = new Integer(a);

					BigInteger A1 = g.modPow(new BigInteger(intA1.toString()),
							n); // used for server to verifyAuth client

					BigInteger A2 = g.modPow(new BigInteger(intA2.toString()),
							n); // used for client to verifyAuth server

					BigInteger A3 = g.modPow(new BigInteger(intA3.toString()),
							n); // used to augment Diffie-Hellman shared key
					// generation

					this.dataout.writeUTF(A1.toString());
					this.dataout.writeUTF(A2.toString());
					this.dataout.writeUTF(A3.toString());

					String verifier = this.datain.readUTF();
					String bString1 = this.datain.readUTF();
					String bString2 = this.datain.readUTF();
					String bString3 = this.datain.readUTF();

					String uString1 = this.datain.readUTF();
					String uString2 = this.datain.readUTF();
					String uString3 = this.datain.readUTF();

					BigInteger m1 = getMFromData(x, uString1, bString1, intA1,
							A1);
					BigInteger m2 = getMFromData(x, uString2, bString2, intA2,
							A2);
					BigInteger m3 = getMFromData(x, uString3, bString3, intA3,
							A3);

					String firstM1 = m1.toString();
					String firstM2 = m2.toString();
					String firstM3 = m3.toString();

					session.put("real_name", realName);
					session.put("session", firstM1);

					this.dataout.writeUTF(firstM1);

					String server2 = this.datain.readUTF();

					logger.debug("-------------- server : "+server2);
					logger.debug("-------------- first : "+firstM2);
					if (server2.equals(firstM2)) { // Authentication successful

						StartUpArgsHook.INSTANCE.notifyValidSetUpArgs( session, getPassphrase(), profileId );
						logger.info("Its mutual");

						if (changePassphraseNextLogin.equals("true")) {

							logger.warn("MUST CHANGE PASSPHRASE NOW: "
									+ networkingClassName);

						} else {

							logger.warn("not needing change passphrase");

						}

						EncryptedSocket exchange = new EncryptedSocket(
								this.datain, this.dataout, firstM3);
						exchange.initClientCrypt();
						this.dataout.flush();
						this.cEncrypt = exchange.returncEncrypt();
						this.cDecrypt = exchange.returncDecrypt();

						CipherOutputStream cos = new CipherOutputStream(this.out,
								this.cEncrypt);
						CipherInputStream cis = new CipherInputStream(this.in, this.cDecrypt);

						encryptedSockets.put((String) session.get("username"),
								exchange);

						this.cdatain = new DataInputStream(cis);
						this.cdataout = new DataOutputStream(cos);

						this.update = new Hashtable();

						this.update.put(SessionConstants.SESSION, session);

						this.update.put(SessionConstants.PROTOCOL,
								networkingClassName);

						logger.warn("here we go: " + networkingClassName);

						if (networkingClassName.equals("SupraClient")) {

							byte[] objectBytes = objectToBytes(this.update);
							this.cdataout.writeInt(objectBytes.length);
							this.cdataout.write(objectBytes, 0,
									objectBytes.length);

						} else if (networkingClassName.equals("DeliverMail")) {

							byte[] objectBytes = objectToBytes(this.update);
							this.cdataout.writeInt(objectBytes.length);
							this.cdataout.write(objectBytes, 0,
									objectBytes.length);

						} else if (networkingClassName.equals(PresenceUtils.PRESENCE_PROTOCOL_NAME)  
								   || networkingClassName.equals(AbstractClientDataProviderConnector.DOMAIN_SPACE_PROTOCOL_NAME)  
								   || networkingClassName.equals(DebugUtils.DEBUG_PROTOCOL_NAME ) 
								   || networkingClassName.equals(LogConstants.LOG_PROTOCOL_NAME ) 
								   || networkingClassName.equals(InstallUtils.UPDATE_PROTOCOL_NAME) 
								   || networkingClassName.equals(InstallUtils.UPDATE_FILE_TRANSFER_PROTOCOL_NAME ) 
								   || networkingClassName.equals( SimpleFileTransferUtils.DOWNLOAD_PROTOCOL_NAME ) ) {
							returnObject = new NetworkConnectionProvider( this.sF, session, this.cdatain, this.cdataout, this.update );
						} else if (networkingClassName.equals("PutBinary")) {

							AbstractBinaryClientTransmitter tr = new PutBinaryClientTransmitter(this.cdataout, this.cdatain, this.update, session);
							tr.transmit();

						} else if (networkingClassName.equals("GetBinary")) {

							AbstractBinaryClientTransmitter tr = new GetBinaryClientTransmitter(this.cdataout, this.cdatain, this.update, session);
							tr.transmit();

						} else if (networkingClassName.equals("SaveBinary")) {

							AbstractBinaryClientTransmitter tr = new SaveBinaryClientTransmitter(this.cdataout, this.cdatain, this.update, session, this.shellToDispose);
							tr.transmit();

						} else if (networkingClassName
								.equals("DownloadMultipleFiles")) {
							
							AbstractBinaryClientTransmitter tr = new MultipleDowunloadBinaryClientTransmitter(this.cdataout, this.cdatain, this.update, session);
							tr.transmit();

						} else if (networkingClassName
								.equals("OnlyOpenConnection")) {

							logger.info("Starting only open");

							byte[] objectBytes = objectToBytes(this.update);
							this.cdataout.writeInt(objectBytes.length);

							this.cdataout.write(objectBytes, 0,
									objectBytes.length);

						} else if (networkingClassName.equals("DialogsMainCli") ||
								networkingClassName.equals("WebDialogsMainCli")) {

							logger.warn("starting : " + changePw + " : "
									+ changePassphraseNextLogin);

							if (changePw.equals("changePassphraseNextLogin")) {
								logger.info("Creating dialogsmaincli");

								CreateMembership membership = new CreateMembership();

								String newUsername = (String) session
										.get("newUsername");
								String newPassphrase = (String) session
										.get("newPassphrase");

								session.remove("newUsername");
								session.remove("newPassphrase");

								Document memDoc = membership.createMember(
										username, newUsername, newPassphrase);

								byte[] objectBytes = objectToBytes(this.update);
								this.cdataout.writeInt(objectBytes.length);
								this.cdataout.write(objectBytes);
								String oldUsername = (String) session
										.get("username");
								this.cdataout.writeUTF(oldUsername);
								
								this.cdataout.writeUTF(newUsername);

								this.cdataout.writeUTF((String) session
										.get("firstSessionId"));

								this.cdataout.writeUTF(memDoc.getRootElement()
										.element("verifier").attributeValue(
												"salt"));
								this.cdataout.writeUTF(memDoc.getRootElement()
										.element("verifier").getText());

								session.remove("username");
								session.remove("changePw");
								session.remove("changePassphraseNextLogin");
								session.remove("use_machine_verifier");
								session.put("use_machine_verifier", "false");
								session.put("username", newUsername);

								if ( this.sF != null ) {
									this.sF.getTempPasswords().setTempPW(
										((String) session.get("supra_sphere")),
										newPassphrase);
									this.sF.setSession(session);
								}

								DialogsMainCli dialogsMainCli = createDialogsMainCli(session, this.cdatain, this.cdataout);
								if ( this.sF != null ) {		
									dialogsMainCli.setSupraSphereFrame(this.sF);
									this.sF.registerSession("DialogsMainCli",
											session);
								}

								StartUpArgsHook.INSTANCE.notifyValidSetUpArgs( session, newPassphrase, profileId );
								returnObject = dialogsMainCli;

							} else {
								logger.warn("invite : "+invite);
								if (!invite.equals("transferCredentials")) {

									final byte[] objectBytes = objectToBytes(this.update);
									this.cdataout.writeInt(objectBytes.length);
									this.cdataout.write(objectBytes,
											0, objectBytes.length);
									
									DialogsMainCli dialogsMainCli = createDialogsMainCli(session, this.cdatain,
											this.cdataout);
									try {
										if ( this.sF != null ) {
											dialogsMainCli.setSupraSphereFrame(this.sF);
											this.sF.registerSession("DialogsMainCli",
													session);
										}
									} catch (Throwable ex){
										logger.error("Cannot register session", ex);
									}
									returnObject = dialogsMainCli;
								} else {
									session.remove("pasphrase");
									String supraSphere = (String) session
											.get("supra_sphere");
									String isActuallyInvite = (String) session
											.get("inviteURL");
									if ( this.sF != null ) {
										this.sF.registerSession("DialogsMainCli",
												session);
									}
									String converted = VariousUtils
											.convertInviteURLtoSphereURL(
													supraSphere,
													isActuallyInvite);
									// session.put("sphereURL",converted);
									
									byte[] objectBytes = objectToBytes(this.update);
									this.cdataout.writeInt(objectBytes.length);
									this.cdataout.write(objectBytes);
									
									DialogsMainCli dialogsMainCli = createDialogsMainCli(session, this.cdatain,
											this.cdataout);

									if(this.sF!=null) {
										dialogsMainCli.setSupraSphereFrame(this.sF);
										this.sF.getActiveConnections()
												.putActiveConnection(converted,
														dialogsMainCli);
									}
									logger.warn("PUT ACTIVE CONNECTION : "
											+ (String) session.get("sphereURL")
											+ " : " + converted);

									returnObject = dialogsMainCli;
								}
							}

						} else if (networkingClassName.equals("SimplestClient")) {

							logger.error("Simplest client is not supported");
							/*
							 * SimplestClient simpleClient = new
							 * SimplestClient(session,cdatain,cdataout);
							 * 
							 * byte[] objectBytes = objectToBytes(update);
							 * simpleClient.cdataout.writeInt(objectBytes.length);
							 * 
							 * simpleClient.cdataout.write(objectBytes, 0,
							 * objectBytes.length);
							 * 
							 * logger.info("Requesting simplest client now2");
							 * 
							 * //simpleClient.cdatain.read int objectSize =
							 * simpleClient.cdatain.readInt();
							 * 
							 * SSLogger.getLogger().info("trying hilwe2:
							 * "+objectSize); byte[] inobjectBytes = new
							 * byte[objectSize];
							 * simpleClient.cdatain.readFully(inobjectBytes);
							 * 
							 * VerifyAuth verifyAuth =
							 * (VerifyAuth)bytesToObject(inobjectBytes);
							 * 
							 * SSLogger.getLogger().info("got verifyAuth:
							 * "+verifyAuth.getSphereCore());
							 * 
							 * simpleClient.setVerifyAuth(verifyAuth);
							 * 
							 * 
							 * returnObject = simpleClient;
							 */

						} else if (networkingClassName
								.equals("StartByteRouter")) {
							try {

								byte[] objectBytes = objectToBytes(this.update);
								this.cdataout.writeInt(objectBytes.length);

								this.cdataout.write(objectBytes, 0,
										objectBytes.length);

								Hashtable bootStrapInfo = (Hashtable) session
										.get("bootStrapInfo");

								session.remove("bootStrapInfo");

								String senderOrReceiver = (String) bootStrapInfo
										.get("senderOrReceiver");
								String extraInfo = (String) bootStrapInfo
										.get("extraInfo");

								Document doc = (Document) bootStrapInfo
										.get("doc");
								Document rootDoc = (Document) bootStrapInfo
										.get("rootDoc");

								if (doc == null) {

									logger.info("thats why right here");
								}

								String sessionId = (String) session
										.get("session");
								logger.info("sessionID: " + sessionId);
								logger.info("doc; " + doc.asXML());
								String physical = doc.getRootElement().element(
										"physical_location").attributeValue(
										"value");
								logger.info("senderorreceiver: "
										+ senderOrReceiver);
								if (this.cdatain == null) {
									logger.info("cdatainnull");
								}
								if (this.cdataout == null) {
									logger.info("cdataoutnull");
								}
								try {

									logger
											.info("Starting byte router client!!: "
													+ physical);

									ByteRouterClient br = null;
									if (extraInfo == null) {
										extraInfo = "none";

									}
									if (!extraInfo.equals("transferOnly")) {

										br = new ByteRouterClient(this.sF,
												session, sessionId, physical,
												this.cdatain, this.cdataout,
												senderOrReceiver, doc, rootDoc,
												extraInfo);

									} else {

										br = new ByteRouterClient(this.sF,
												session, bootStrapInfo,
												sessionId, physical,
												this.cdatain, this.cdataout,
												senderOrReceiver, doc, rootDoc,
												extraInfo);

									}
									
									
									this.sF.registerSession("ByteRouterClient",
											session);
									this.sF.getActiveByteRouters()
											.putActiveByteRouter(sessionId, br);

								} catch (NullPointerException npe) {
									logger.error(npe);
								}

							} catch (Exception e) {
								logger.error("StartByteRouter failed", e);

							}
						} else {

						}

					}  else {
						if(server2.equals("locked")) {
							returnObject = new MembershipLockedObject();
							openMessageAboutBan();
							this.dataout.close();
						} else {
							returnObject = new IncorrectPassphraseObject();
							askNewLoginScreen();
							this.dataout.close();
						}
					}
				} else {
					returnObject = new IncorrectPassphraseObject();
					askNewLoginScreen();
					this.dataout.close();
				}

		} catch (Exception e) {
			logger.error( "ZKA failed", e );
		}

		return returnObject;

	}
	
	/**
	 * 
	 */
	private void openMessageAboutBan() {
		if(this.sF==null) {
			return;
		}
		logger.error("User has been locked!");
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				LockInfoDialog dialog = new LockInfoDialog(SDisplay.display.get().getActiveShell());
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});		
	}

	/**
	 * @param session
	 * @param cdatain2
	 * @param cdataout2
	 * @return
	 */
	protected DialogsMainCli createDialogsMainCli(Hashtable session,
			DataInputStream cdatain, DataOutputStream cdataout) {
		return new DialogsMainCli(session, cdatain, cdataout);
	}

	private void askNewLoginScreen(){
		//logger.info("was not equal, show another prompt");
		//this.sF.getWelcomeScreen().showMessage(
		//		"Username or passphrase incorrect!");
		logger.error("Username or passphrase incorrect!");
		//this.sF.disposeButDontRemove();
		final int SLEEP_TIME_BEFORE_SECOND_REQUEST = 1000;

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					sleep(SLEEP_TIME_BEFORE_SECOND_REQUEST);

					if (SupraClient.this.sF != null) {
						SupraClient.this.sF
								.getWelcomeScreen()
								.setPromptPassphrase(
										"Incorect! Enter a different username or passphrase");

						if (SupraClient.this.sF.getWelcomeScreen().getShell() != null) {
							SupraClient.this.sF.getWelcomeScreen().layoutGUI();
						}
					}

				} catch (InterruptedException e) {
					logger.error(e);
				}

			}

		};
		ThreadUtils.startDemon(thread, "New login screen starter");
	}

	private BigInteger getMFromData(BigInteger x, String uString,
			String bString, Integer intA, BigInteger A) {

		final BigInteger B = new BigInteger(bString);

		// logger.info("Just received B: " +
		// B.toString());
		final Integer intU = new Integer(uString);
		// logger.info("Just received u: " +
		// int_u.toString());
		/*
		 * Compute S
		 */
		final BigInteger bprime = B.mod(n);
		final BigInteger gtox = g.modPow(x, n);
		final BigInteger diff = bprime.subtract(gtox);
		final BigInteger ux = (new BigInteger(intU.toString())).multiply(x);
		final BigInteger exponent = ux.add(new BigInteger(intA.toString()));
		final BigInteger S = diff.modPow(exponent, n);
		// logger.info("S is: " + S.toString());
		final BigInteger k = getHashOf(S.toString());
		final BigInteger m1 = getHashOf(new String(A.toString() + B.toString()
				+ k.toString()));
		return m1;
	}

	/**
	 * Constructor for the SimplestClient object
	 * 
	 * @param netAddress
	 *            Description of the Parameter
	 * @param port
	 *            Description of the Parameter
	 * @param mP
	 *            Description of the Parameter
	 */

	/**
	 * Description of the Method
	 * 
	 * @param object
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception IOException
	 *                Description of the Exception
	 */
	private static byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		return baos.toByteArray();
	}

	/**
	 * @param frame
	 */
	public void setSupraSphereFrame(SupraSphereFrame sF) {
		this.sF = sF;
	}

	/**
	 * @param passphrase
	 *            the passphrase to set
	 */
	private void setPassphrase(String passphrase) {
		//  logger.info("setPassphrase " + passphrase);
		this.passphrase = passphrase;
	}

	/**
	 * @return the passphrase
	 */
	private String getPassphrase() {
		return this.passphrase;
	}
}
