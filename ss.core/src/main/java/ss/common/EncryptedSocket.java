package ss.common;
/*
 * 
 * A Diffie-Hellman key exchange to establish an encrypted socket.
 *  
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jce.provider.JCEDHPublicKey;
import org.bouncycastle.jce.provider.JCESecretKeyFactory;
//import org.bouncycastle.crypto.params.DHParameters;
//import cryptix.jce.provider.*;
public class EncryptedSocket {
	
	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EncryptedSocket.class);
	
	//Socket socket = null;
	@SuppressWarnings("unused")
	private PrivateKey private_key = null;
	private SecretKeySpec secret_key_spec = null;
	@SuppressWarnings("unused")
	private JCEDHPublicKey other_public = null;
	private byte[] public_key_bytes = null;
	@SuppressWarnings("unused")
	private KeyPairGenerator kpgencrypt;
	private AsymmetricCipherKeyPair keypair = null;
	private byte[] other_key_bytes = null;
	@SuppressWarnings("unused")
	private JCESecretKeyFactory kf = null;
	@SuppressWarnings("unused")
	private KeyAgreement ka = null;
	@SuppressWarnings("unused")
	private SecretKeyFactory skf = null;
	@SuppressWarnings("unused")
	private DESedeKeySpec DESspec = null;
	
	private BigInteger bigInt3 = null;
	//private BigInteger g1024 = new
	// BigInteger("1db17639cdf96bc4eabba19454f0b7e5bd4e14862889a725c96eb61048dcd676ceb303d586e30f060dbafd8a571a39c4d823982117da5cc4e0f89c77388b7a08896362429b94a18a327604eb7ff227bffbc83459ade299e57b5f77b50fb045250934938efa145511166e3197373e1b5b1e52de713eb49792bedde722c6717abf",
	// 16);
	//private BigInteger p1024 = new
	// BigInteger("a00e283b3c624e5b2b4d9fbc2653b5185d99499b00fd1bf244c6f0bb817b4d1c451b2958d62a0f8a38caef059fb5ecd25d75ed9af403f5b5bdab97a642902f824e3c13789fed95fa106ddfe0ff4a707c85e2eb77d49e68f2808bcea18ce128b178cd287c6bc00efa9a1ad2a673fe0dceace53166f75b81d6709d5f8af7c66bb7",
	// 16);
	private final BigInteger g512 = new BigInteger(
			"153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc",
			16);
	private final BigInteger p512 = new BigInteger(
			"9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b",
			16);
	
	private SecureRandom rand = null;
	private DHKeyPairGenerator kpg_my_public = null;
	private DataInputStream datain = null;
	private DataOutputStream dataout = null;
	@SuppressWarnings("unused")
	private CipherInputStream cin = null;
	@SuppressWarnings("unused")
	private CipherOutputStream cout = null;
	@SuppressWarnings("unused")
	private DataInputStream cdatain = null;
	@SuppressWarnings("unused")
	private DataOutputStream cdataout = null;
	private javax.crypto.Cipher cEncrypt = null;
	private javax.crypto.Cipher cDecrypt = null;
	private static final String MODULUS_STRING = "F52AFF3CE1B12940"
		+ "18118D7C84A70A72" + "D686C40319C80729" + "7ACA950CD9969FAB"
		+ "D00A509B0246D308" + "3D66A45D419F9C7C" + "BD894B221926BAAB"
		+ "A25EC355E92A055F";
	private static final BigInteger MODULUS = new BigInteger(MODULUS_STRING, 16);
	private static final BigInteger BASE = BigInteger.valueOf(2);
	@SuppressWarnings("unused")
	private static final DHParameterSpec parameterSpec = new DHParameterSpec(
			MODULUS, BASE);
	
	private boolean generate_done = false;
	
	
	public EncryptedSocket(DataInputStream datain, DataOutputStream dataout, String m3) {
		this.bigInt3 = new BigInteger(m3);
		this.datain = datain;
		this.dataout = dataout;				
	}
	
	public void initServerCrypt() {
		try {
			this.rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		/*
		 * try {
		 * 
		 * File f = new File("SecureRandom.rnd"); ObjectOutputStream obout = new
		 * ObjectOutputStream(new FileOutputStream(f));
		 * 
		 * obout.writeObject(rand); obout.flush();
		 *  } catch (Exception e) {
		 * 
		 * logger.info("problme");
		 *  }
		 */
		java.security.Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		DHParameters dhParams = new DHParameters(this.p512, this.g512);
		DHKeyGenerationParameters params = new DHKeyGenerationParameters(this.rand,
				dhParams);
		this.kpg_my_public = new DHKeyPairGenerator();
		this.kpg_my_public.init(params);
		this.keypair = this.kpg_my_public.generateKeyPair();
		DHPublicKeyParameters pub_params = null;
		DHPrivateKeyParameters priv_params = null;
		try {
			pub_params = (DHPublicKeyParameters) this.keypair.getPublic();
			priv_params = (DHPrivateKeyParameters) this.keypair.getPrivate();
		} catch (ClassCastException npe) {
			logger.info("johns great idea sucked on the server");
		}
		Date current = new Date();
		String moment = DateFormat.getDateInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getTimeInstance(DateFormat.LONG).format(current);
		logger.info("done generating pair in server: " + moment);
		//        java.security.Security.addProvider(new
		// cryptix.jce.provider.CryptixCrypto());
		// Receive public key as encoded bytes, use to create PublicKey similar
		// to type in getPublic() method{
		try {
			this.other_key_bytes = new byte[this.datain.readInt()];
			this.datain.readFully(this.other_key_bytes);
			//  write my public key
			BigInteger y = pub_params.getY();
			this.public_key_bytes = y.toByteArray();
			this.dataout.writeInt(this.public_key_bytes.length);
			this.dataout.write(this.public_key_bytes);
		} catch (IOException ioe) {
			logger.info("IOException");
		}
		try {
			DHPublicKeyParameters other_pub_param = new DHPublicKeyParameters(
					new BigInteger(this.other_key_bytes), priv_params
							.getParameters());
			// Create key agreement with my private and other side's public
			DHBasicAgreement dhagree = new DHBasicAgreement();
			dhagree.init(priv_params);
			//BigInteger m1 = dhagree.calculateMessage();
			BigInteger shared_cli = dhagree.calculateAgreement(other_pub_param);
			
			shared_cli = shared_cli.add(this.bigInt3);
			//BigInteger shared_cli = new BigInteger("6867024026453515150717753064785888864590614824157943078876724373454633581050616012276200642384817268901389443022878707184810765330191215222160834509011700958720730275262249098743258905029338474346887099323432715501766473762152258146536096943784841363729963235086324369252173978118682241052868949162563416709145494733441423007050033389113619864185820900865947108667656799678590499428875357650434344105915596292634429975054864286459647789995962886531788504618603635986797618545749397337928879540775442026992623328654415447590930957209863517694645591653277657462006886765484527391178139625392788453723437942718150779230207980870025499518351149449494428966913344926340078869482635874929141831013520659485888862028006721957113349529636441134716980561156474959516589339364053608482006004712641241194073526146550434713252174177083109446740777949642519217274060227629252089707672680660905721401062818453732724758438498233715458394398140254121801600559849763807925304788617416702115149060636675121776152006392134768134118632834834785269358046312141923987145294489085366640622889083361951979715039830933796762116850839059268666004526336539070351203240722571613561190054171663082835275523737402119623415025961818041171074976799081062593410398292181657136355369868328996852274418709681420904478097785302730239039630088244228615825595660484690641979189218904287938617291261415166406790379538656933971606402466174161535166800077465607969822041217802435476829433600046606622819174867253426684023300754871864922805657128762005122847852048833611881836696032821636302870578663318651324186027165667758088313076156686220283326272176530446819266986471679723973333275673162336509833150298688722171160043727004880709970823244897037519332825485427880135749230658908754601360990878798890576698597182611504246047353941764489581574877699050231141960942790566035066604095836146834514177029059957784571248784291669711269977248940971556497696518694149402760607309082416390430866212601251428965840041176878079825138622548453555485718080881106258556011075259214804553264121471613084194814238640175465261659692085946724644449654225318844169676792780588549833224840508589493342281225839527497622538492862643500897601051293217672366398082500817117355634213391156869406489459608571781473626095306346102690398618935412099504443561897426919006099760923026737183037449581694764788659830342570269732039475660922645143777161106814949072127531936291705939553612965367888680620971680302961193494241543800612731554355182972163160453190511757612533238511402451342572388553606652981700241484953276791837847020672869127138326735418673005068238913499617507648340184746174390766605878585363694573230209262504331046234302565147731647549439622755773005574578045349497528108014927896811543579743956083348860591512924518937001069228981006819753537320038733673235526605315564124224382491270603139584045152331616600317604915330459554450760883496009190620593957776210618351874518709476638661756030254797178405849922196603521513340071609536550475495328294706917755850072032306089498388167550394262107466729691171303281686241022816607986762233076556220029535331460264258782838455684910233352689263995167561423246045286211259038265467432823960349792615002888414338591468744106070969945491984790014847340669751923508112771936882695709389346795518932125670390145344516579105079655756605413628557818571808529447254");
			byte[] shared_secret_key_bytes = shared_cli.toByteArray();
			current = new Date();
			moment = DateFormat.getDateInstance(DateFormat.LONG)
					.format(current)
					+ " "
					+ DateFormat.getTimeInstance(DateFormat.LONG).format(
							current);
			logger.info("after generate shared on serv: " + moment);
			try {
				this.DESspec = new DESedeKeySpec(shared_secret_key_bytes);
			} catch (InvalidKeyException ike) {
				logger.info(ike.getMessage(), ike);
			}
			this.secret_key_spec = new SecretKeySpec(shared_secret_key_bytes,
					"DESede");
			
			IvParameterSpec DESspec = null;
			IvParameterSpec enspec = null;
			try {
				try {
					// Generate parameter spec to initialize my encoding, send
					// the bytes for that parameter spec
					// to other side
					byte[] enspecbytes = new byte[8];
					this.rand.nextBytes(enspecbytes);
					enspec = new IvParameterSpec(enspecbytes);
					// read in bytes to create parameter spec used to decode
					// cipherinputstream
					byte[] specbytes = new byte[8];
					try {
						this.dataout.write(enspecbytes);
						//logger.info("writing bytes on serv
						// "+enspecbytes.toString());
						this.datain.readFully(specbytes);
						//logger.info("read bytes on serv:
						// "+specbytes.toString());
						DESspec = new IvParameterSpec(specbytes);
					} catch (IOException ioe) {
						logger.info(ioe.getMessage(), ioe);
					}
					//logger.info("specbyteslengthserv:
					// "+specbytes.length);
					this.cDecrypt = Cipher
							.getInstance("DESede/CFB8/NoPadding", "BC");
				} catch (NoSuchPaddingException nspe) {
					logger.info(nspe.getMessage(), nspe);
				}
			} catch (NoSuchAlgorithmException nsae) {
				logger.info(nsae.getMessage(), nsae);
			}
			try {
				try {
					this.cDecrypt
							.init(Cipher.DECRYPT_MODE, this.secret_key_spec, DESspec);
				} catch (InvalidAlgorithmParameterException iape) {
					logger.info(iape.getMessage(), iape);
				}
			} catch (InvalidKeyException ike) {
				logger.info(ike.getMessage(), ike);
			}
			//  try
			try {
				try {
					this.cEncrypt = Cipher
							.getInstance("DESede/CFB8/NoPadding", "BC");
				} catch (NoSuchPaddingException nspe) {
					logger.info(nspe.getMessage(), nspe);
				}
			} catch (NoSuchAlgorithmException nsae) {
				logger.info(nsae.getMessage(), nsae);
			}
			try {
				try {
					this.cEncrypt.init(Cipher.ENCRYPT_MODE, this.secret_key_spec, enspec);
				} catch (InvalidAlgorithmParameterException iape) {
					logger.info(iape.getMessage(), iape);
				}
			} catch (InvalidKeyException ike) {
				logger.info(ike.getMessage(), ike);
			}
		} catch (java.security.NoSuchProviderException nse) {
			logger.info("caught no such provider in server");
		}
		/*
		 * try { // cdataout.writeUTF("Hello encrypted world1"); //
		 * cdataout.writeUTF("Hello encrypted world2");
		 * 
		 * logger.info(cdatain.readUTF()); } catch (IOException ioe) {
		 * logger.info("IOException"); }
		 */
		if (this.cEncrypt == null) {
			logger.info("init of cencrypt made it nulls");
		} else {
			logger.info("init of cencrypt not null yets");
		}
		if (this.cDecrypt == null) {
			logger.info("init of cdecrypt made it nulls");
		} else {
			logger.info("init of cdecrypt not null yets");
		}
		// End crypto stuff, session created
	}
	public void initClientCrypt() {

		
			this.rand = new SecureRandom();
		
		

		java.security.Security
				.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		logger.info("after add provider");
		DHParameters dhParams = new DHParameters(this.p512, this.g512);
		
		DHKeyGenerationParameters params = new DHKeyGenerationParameters(this.rand,
				dhParams);
		
		this.kpg_my_public = new DHKeyPairGenerator();
		
		//kpg_my_public.initialize(parameterSpec);
		this.kpg_my_public.init(params);
		
		//DHKeyPairGenerator kpg_my_public = new DHKeyPairGenerator();
		//kpg_my_public.initialize(parameterSpec);
		//byte[] ivspecbytes = new byte[8];
		//   SecureRandom sr = new SecureRandom();
		//sr.nextBytes(ivspecbytes);
		//kpg_my_public.init(new KeyGenerationParameters(sr,1024));
		//AsymmetricCipherKeyPair == keypair
//		Date current = new Date();
		
//		String moment = DateFormat.getDateInstance(DateFormat.LONG).format(
//				current)
//				+ " "
//				+ DateFormat.getTimeInstance(DateFormat.LONG).format(current);
		
		this.keypair = this.kpg_my_public.generateKeyPair();
//		current = new Date();
//		moment = DateFormat.getDateInstance(DateFormat.LONG).format(current)
//				+ " "
//				+ DateFormat.getTimeInstance(DateFormat.LONG).format(current);
		
		this.generate_done = true;
		
		DHPublicKeyParameters pub_params = null;
		DHPrivateKeyParameters priv_params = null;
		try {
			pub_params = (DHPublicKeyParameters) this.keypair.getPublic();

			priv_params = (DHPrivateKeyParameters) this.keypair.getPrivate();

		} catch (ClassCastException npe) {
		
		}
		// Now write my public key
		try {
			BigInteger y = pub_params.getY();
			this.public_key_bytes = y.toByteArray();
			this.dataout.writeInt(this.public_key_bytes.length);
			this.dataout.write(this.public_key_bytes);
			// Receive public key as encoded bytes, use to create PublicKey
			// similar to type in getPublic() method
			this.other_key_bytes = new byte[this.datain.readInt()];
			this.datain.readFully(this.other_key_bytes);
			//logger.info("read bytes: "+other_key_bytes);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		}
		try {
			DHPublicKeyParameters other_pub_param = new DHPublicKeyParameters(
					new BigInteger(this.other_key_bytes), priv_params
							.getParameters());
			// Create key agreement with my private and other side's public
			DHBasicAgreement dhagree = new DHBasicAgreement();
			dhagree.init(priv_params);
			//BigInteger m1 = dhagree.calculateMessage();
//			current = new Date();
//			moment = DateFormat.getDateInstance(DateFormat.LONG)
//					.format(current)
//					+ " "
//					+ DateFormat.getTimeInstance(DateFormat.LONG).format(
//							current);
			//logger.info("before calculate agreement: "+moment);
			BigInteger shared_cli = dhagree.calculateAgreement(other_pub_param);
			shared_cli = shared_cli.add(this.bigInt3);
			//BigInteger shared_cli = new BigInteger("6867024026453515150717753064785888864590614824157943078876724373454633581050616012276200642384817268901389443022878707184810765330191215222160834509011700958720730275262249098743258905029338474346887099323432715501766473762152258146536096943784841363729963235086324369252173978118682241052868949162563416709145494733441423007050033389113619864185820900865947108667656799678590499428875357650434344105915596292634429975054864286459647789995962886531788504618603635986797618545749397337928879540775442026992623328654415447590930957209863517694645591653277657462006886765484527391178139625392788453723437942718150779230207980870025499518351149449494428966913344926340078869482635874929141831013520659485888862028006721957113349529636441134716980561156474959516589339364053608482006004712641241194073526146550434713252174177083109446740777949642519217274060227629252089707672680660905721401062818453732724758438498233715458394398140254121801600559849763807925304788617416702115149060636675121776152006392134768134118632834834785269358046312141923987145294489085366640622889083361951979715039830933796762116850839059268666004526336539070351203240722571613561190054171663082835275523737402119623415025961818041171074976799081062593410398292181657136355369868328996852274418709681420904478097785302730239039630088244228615825595660484690641979189218904287938617291261415166406790379538656933971606402466174161535166800077465607969822041217802435476829433600046606622819174867253426684023300754871864922805657128762005122847852048833611881836696032821636302870578663318651324186027165667758088313076156686220283326272176530446819266986471679723973333275673162336509833150298688722171160043727004880709970823244897037519332825485427880135749230658908754601360990878798890576698597182611504246047353941764489581574877699050231141960942790566035066604095836146834514177029059957784571248784291669711269977248940971556497696518694149402760607309082416390430866212601251428965840041176878079825138622548453555485718080881106258556011075259214804553264121471613084194814238640175465261659692085946724644449654225318844169676792780588549833224840508589493342281225839527497622538492862643500897601051293217672366398082500817117355634213391156869406489459608571781473626095306346102690398618935412099504443561897426919006099760923026737183037449581694764788659830342570269732039475660922645143777161106814949072127531936291705939553612965367888680620971680302961193494241543800612731554355182972163160453190511757612533238511402451342572388553606652981700241484953276791837847020672869127138326735418673005068238913499617507648340184746174390766605878585363694573230209262504331046234302565147731647549439622755773005574578045349497528108014927896811543579743956083348860591512924518937001069228981006819753537320038733673235526605315564124224382491270603139584045152331616600317604915330459554450760883496009190620593957776210618351874518709476638661756030254797178405849922196603521513340071609536550475495328294706917755850072032306089498388167550394262107466729691171303281686241022816607986762233076556220029535331460264258782838455684910233352689263995167561423246045286211259038265467432823960349792615002888414338591468744106070969945491984790014847340669751923508112771936882695709389346795518932125670390145344516579105079655756605413628557818571808529447254");
			byte[] shared_secret_key_bytes = shared_cli.toByteArray();
			
			
//			current = new Date();
//			moment = DateFormat.getDateInstance(DateFormat.LONG)
//					.format(current)
//					+ " "
//					+ DateFormat.getTimeInstance(DateFormat.LONG).format(
//							current);
			
			try {
				this.DESspec = new DESedeKeySpec(shared_secret_key_bytes);
			} catch (InvalidKeyException ike) {
				logger.info(ike.getMessage(), ike);
			}
			this.secret_key_spec = new SecretKeySpec(shared_secret_key_bytes,
					"DESede");
			// 
			IvParameterSpec DESspec = null;
			IvParameterSpec enspec = null;
			try {
				try {
					// Generate parameter spec to initialize my encoding, send
					// the bytes for that parameter spec
					// to other side
					byte[] enspecbytes = new byte[8];
					this.rand.nextBytes(enspecbytes);
					enspec = new IvParameterSpec(enspecbytes);
					// read in bytes to create parameter spec used to decode
					// cipherinputstream
					byte[] specbytes = new byte[8];
					try {
						this.datain.readFully(specbytes);
						//logger.info("read bytes on cli:
						// "+specbytes.toString());
						this.dataout.write(enspecbytes);
						//logger.info("wrote bytes on cli
						// "+enspecbytes.toString());
						DESspec = new IvParameterSpec(specbytes);
						//logger.info("specbyteslengthcli:
						// "+specbytes.length);
					} catch (IOException ioe) {
						logger.info(ioe.getMessage(), ioe);
					}
					this.cDecrypt = Cipher
							.getInstance("DESede/CFB8/NoPadding", "BC");
				} catch (NoSuchPaddingException nspe) {
					logger.info(nspe.getMessage(), nspe);
				}
			} catch (NoSuchAlgorithmException nsae) {
				logger.info(nsae.getMessage(), nsae);
			}
			try {
				try {
					this.cDecrypt
							.init(Cipher.DECRYPT_MODE, this.secret_key_spec, DESspec);
				} catch (InvalidAlgorithmParameterException iape) {
					logger.info(iape.getMessage(), iape);
				}
			} catch (InvalidKeyException ike) {
				logger.info(ike.getMessage(), ike);
			}
			//  try {
			try {
				this.cEncrypt = Cipher.getInstance("DESede/CFB8/NoPadding", "BC");
			} catch (Exception e) {
				//logger.info("trouble creating centrypt:
				// "+e.getMessage());
			}
			try {
				this.cEncrypt.init(Cipher.ENCRYPT_MODE, this.secret_key_spec, enspec);
			} catch (Exception e) {
				logger.info("trouble init centrypt: " + e.getMessage(), e);
			}
			
		} catch (java.security.NoSuchProviderException nse) {
		}
	}
	public synchronized Cipher returncEncrypt() {
		while (this.cEncrypt == null) {
		}
		return this.cEncrypt;
	}
	public synchronized Cipher returncDecrypt() {
		while (this.cDecrypt == null) {
		}
		return this.cDecrypt;
	}
}