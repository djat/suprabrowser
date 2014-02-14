package ss.common;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JEditorPane;
import javax.swing.JList;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.ui.MessagesPane;
import ss.global.SSLogger;


public class CreateMembership {
	


	/*
	 * Used to create a new password and membership
	 *
	 * */





	Hashtable session = new Hashtable();
	Hashtable contacts = new Hashtable();
	JEditorPane bodyEditor = null;
	private Cipher pbeCipher;
	
	private static final Logger logger = SSLogger.getLogger(CreateMembership.class);

	protected BigInteger n = new BigInteger("122401836212692490420534044542160652060089063288916259378992106298635997629004871449040863148842028332936921964140316531574725544295868627297697141329665138830185297018653829634397774436198765430305661897599443877203922836563409947444887027604848997125383124008931080480197775021431253630976071993683966590129");
	protected BigInteger g = new BigInteger("164438241317367690823401305357370607328034430023459713340335187782878205902844516952738722877059454685668830995765089402230495206772108704026831761904445241536185572511706087329237156570564175356155697011727697175323622673030696046933358292860718413231660423149569049278930510073740541237052508995182528917163");
		
		PBEKeySpec pbeKeySpec;
	        PBEParameterSpec pbeParamSpec;
	        SecretKeyFactory keyFac;
		JList list = null;
		JList plist = null;
		MessagesPane mP = null;
		Vector memberlist = new Vector();
		String sphere_name = null;

		public CreateMembership(Hashtable session, MessagesPane mP, Hashtable contacts, String sphere_name) {
			
			this.sphere_name = sphere_name;
			this.session = session;
			this.contacts = contacts;
			this.mP = mP;
			
	
			initCrypt();
			
			
			
			
			
			
			
		}
		public CreateMembership() {
		    
		    java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		    initCrypt();
		    
		}
		
		public CreateMembership(Hashtable session, MessagesPane mP) {
			
			this.session = session;
			
			this.mP = mP;
			
			initCrypt();
			
		}
		
		public void initCrypt() {
		byte[] salt = { 
	            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
	            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
	        };

	        // Iteration count
		
	        int count = 20;

	        // Create PBE parameter set
			try {
	              this.pbeParamSpec = new PBEParameterSpec(salt, count);

	              // Convert password into SecretKey object,
	              // using a PBE key factory
			String pass = "asdfasdf";
			char[] passphrase = pass.toCharArray();
			this.pbeKeySpec = new PBEKeySpec(passphrase); //arbitrary plaintext, but must be the same at the server end
	              //keyFac = SecretKeyFactory.getInstance("PBEWithSHAAndTwoFish-CBC");     //we are specifying public block encryption with MD5 and DES
			this.keyFac = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC", "BC");     //we are specifying public block encryption with MD5 and DES
		      //keyFac = SecretKeyFactory.getInstance("PBEWithHmacSHA1AndDESede");
	              SecretKey pbeKey = this.keyFac.generateSecret(this.pbeKeySpec);

	              // Create PBE Cipher
	              this.pbeCipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC", "BC");
	              //pbeCipher = Cipher.getInstance("PBEWithSHAAndTwoFish-CBC");
			//pbeCipher = Cipher.getInstance("PBEWithHmacSHA1AndDESede");
	              // Initialize PBE Cipher with key and parameters
	              this.pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, this.pbeParamSpec);
			} catch(Exception e) {
				logger.error("Error setting up encryption objects: " + e.getMessage(), e);
			}
			
		}
		
		public   BigInteger getHashOf(String s) {
			logger.info("STRING : "+s);
			//We use password based encrytption (with MD5 and DES) to hash s to a large number 
			byte[] cleartext = s.getBytes();
			byte[] ciphertext = { 0 };
			try {
				ciphertext = this.pbeCipher.doFinal(cleartext);		
			} catch (Exception e) {
				logger.error("Error encrypting: " + e);
			}

			return new BigInteger(ciphertext).abs();
		}

		
								
	
		
		public Document XMLDoc (String contact_name, String login_name) {
	       
		Document createDoc = DocumentHelper.createDocument();
		Element email = createDoc.addElement("membership");
		
	    email.addElement("status").addAttribute("value","ratified");

		email.addElement("voting_model").addAttribute("type","absolute").addAttribute("desc","Absolute without qualification");
		email.element("voting_model").addElement("tally").addAttribute("number","0.0").addAttribute("value","0.0");

	                            
	                                    
				    
	                            
	                            email.addElement("giver").addAttribute("value",(String)this.session.get("contact_name"));
				    
				    
				    email.addElement("contact_name").addAttribute("value",contact_name);
				    email.addElement("login_name").addAttribute("value",login_name);
				    
				    email.addElement("subject").addAttribute("value","New member: "+contact_name);
	                            
	        
				    email.addElement("last_updated_by").addAttribute("value",(String)this.session.get("contact_name"));
	                            
				    email.addElement("type").addAttribute("value","membership");
				    email.addElement("thread_type").addAttribute("value","membership");
				    
	                            
	        DefaultElement body = new DefaultElement("body");
		//body.setText(bodyEditor.getText());
		
		
		
		
		body.addElement("version").addAttribute("value", "3000");
		
		
		
		body.addElement("orig_body");
		
		
		
		
		email.add(body);
		


	        
		

	        return createDoc;
	        
	    }
	    
	    public Document createMember(String contact_name, String login_name, String passphrase) {
		    
		    
		    
		    Random r = new Random();

										int s = r.nextInt();
										if (s < 0)
											s = -s;	

										Integer int_s = new Integer(s);
										//String password = new String (ppField.getPassword());
										//	System.out.println("password: "+password);

										BigInteger x = getHashOf(new String(int_s.toString() + passphrase));

										BigInteger v = this.g.modPow(x,this.n);
										
										
										//System.out.println("verifier: "+v.toString());
										//System.out.println("salt: "+int_s.toString());
										
										Document doc = XMLDoc(contact_name, login_name);
										
										doc.getRootElement().addElement("verifier").addAttribute("salt",int_s.toString()).setText(v.toString());
										
										doc.getRootElement().addElement("change_passphrase_next_login");
										
									
		    return doc;
	    }






	}

	


