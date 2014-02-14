package ss.client.ui.viewers;

/*
 * Used to create a new password and membership
 *
 * */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.global.SSLogger;

import java.math.*;
import javax.crypto.*;
import javax.crypto.spec.*;
/**
 * @deprecated
 */
public class AddMembership extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8640911260144455224L;

	Hashtable session = new Hashtable();

    Hashtable contacts = new Hashtable();

    JEditorPane bodyEditor = null;

    private Cipher pbeCipher;

    protected BigInteger n = new BigInteger(
            "122401836212692490420534044542160652060089063288916259378992106298635997629004871449040863148842028332936921964140316531574725544295868627297697141329665138830185297018653829634397774436198765430305661897599443877203922836563409947444887027604848997125383124008931080480197775021431253630976071993683966590129");

    protected BigInteger g = new BigInteger(
            "164438241317367690823401305357370607328034430023459713340335187782878205902844516952738722877059454685668830995765089402230495206772108704026831761904445241536185572511706087329237156570564175356155697011727697175323622673030696046933358292860718413231660423149569049278930510073740541237052508995182528917163");

    PBEKeySpec pbeKeySpec;

    PBEParameterSpec pbeParamSpec;

    SecretKeyFactory keyFac;

    JList list = null;

    JList plist = null;

    MessagesPane mP = null;

    Vector memberlist = new Vector();

    String sphere_name = null;
    
    Logger logger = SSLogger.getLogger(this.getClass());
    
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_ADDMEMBERSHIP);
    
    private static final String SAVE = "ADDMEMBERSHIP.SAVE";
    private static final String SELECT_CONTACT = "ADDMEMBERSHIP.SELECT_CONTACT";
    private static final String CHOOSE_PASSPHRASE = "ADDMEMBERSHIP.CHOOSE_PASSPHRASE";
    private static final String ATTACH_NOTE = "ADDMEMBERSHIP.ATTACH_NOTE";
    private static final String CANCEL = "ADDMEMBERSHIP.CANCEL";


    public AddMembership(Hashtable session, MessagesPane mP,
            Hashtable contacts, String sphere_name) {

        this.sphere_name = sphere_name;
        this.session = session;
        this.contacts = contacts;
        this.mP = mP;

        buildFrame();
        initCrypt();

    }

    public AddMembership() {

        java.security.Security
                .addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        initCrypt();

    }

    public AddMembership(Hashtable session, MessagesPane mP) {

        this.session = session;

        this.mP = mP;

        initCrypt();

    }

    public void initCrypt() {
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
            this.pbeKeySpec = new PBEKeySpec(passphrase); // arbitrary
                                                            // plaintext, but
                                                            // must be the same
                                                            // at the server end
            // keyFac =
            // SecretKeyFactory.getInstance("PBEWithSHAAndTwoFish-CBC"); //we
            // are specifying public block encryption with MD5 and DES
            this.keyFac = SecretKeyFactory.getInstance(
                    "PBEWithSHAAndTwofish-CBC", "BC"); // we are specifying
                                                        // public block
                                                        // encryption with MD5
                                                        // and DES
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
            this.logger.error("Error setting up encryption objects", e);
        }

    }

    public BigInteger getHashOf(String s) {
        this.logger.info("STRING : " + s);
        // We use password based encrytption (with MD5 and DES) to hash s to a
        // large number
        byte[] cleartext = s.getBytes();
        byte[] ciphertext = { 0 };
        try {
            ciphertext = this.pbeCipher.doFinal(cleartext);
        } catch (Exception e) {
            this.logger.error("Error encrypting", e);
        }

        return new BigInteger(ciphertext).abs();
    }

    @SuppressWarnings("unchecked")
	private void buildFrame() {
        final JButton createButton = new JButton(this.bundle.getString(SAVE));
        Vector convert = new Vector();

        this.logger.info("before getmembersfor");
        this.memberlist = this.mP.client.getMembersFor(this.session);
        this.logger.info("after getmembersfor");

        for (int i = 0; i < this.memberlist.size(); i++) {

            String existing_member = (String) this.memberlist.get(i);
            for (Enumeration e = this.contacts.keys(); e.hasMoreElements();) {

                String key = (String) e.nextElement();

                // System.out.println("key in contacts: "+key);

                if ((key).equals(existing_member)) {

                    // System.out.println("REmoving contact:
                    // "+(String)contacts.get("key"));
                    this.contacts.remove(key);

                } else {

                    // System.out.println("THIS does not equal this: "+key+" :
                    // "+existing_member);

                }

            }

        }

        for (Enumeration e = this.contacts.keys(); e.hasMoreElements();) {

            String key = (String) e.nextElement();
            convert.add(key);

            // System.out.println("LOGIN NAME FOR CONCTAC:
            // "+(String)contacts.get(key));
        }

        this.list = new JList(convert.toArray());

        JScrollPane listScroll = new JScrollPane(this.list);

        // Vector personalist = mP.client.getAllPersonas(session);

        // System.out.println("personlistsize: "+personalist.size());

        // plist = new JList(personalist.toArray());
        // plist = new JList();

        // JScrollPane personaScroll = new JScrollPane(plist);
        JLabel c_label = new JLabel(this.bundle.getString(SELECT_CONTACT));

        // JLabel p_label = new JLabel("Select persona");
        JLabel passphrase = new JLabel(this.bundle.getString(CHOOSE_PASSPHRASE));

        final JPasswordField ppField = new JPasswordField(15);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        getContentPane().setLayout(gbl);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.NORTHWEST;

        gbl.setConstraints(c_label, c);
        getContentPane().add(c_label);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 2;
        // c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        gbl.setConstraints(listScroll, c);

        getContentPane().add(listScroll);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;

        gbl.setConstraints(passphrase, c);

        getContentPane().add(passphrase);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.0;
        c.weighty = 0.0;

        gbl.setConstraints(ppField, c);

        getContentPane().add(ppField);

        JLabel note = new JLabel(this.bundle.getString(ATTACH_NOTE));

        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 2;

        gbl.setConstraints(note, c);

        getContentPane().add(note);

        this.bodyEditor = new JEditorPane();
        JScrollPane bodyScroll = new JScrollPane(this.bodyEditor);
        bodyScroll.setPreferredSize(new Dimension(300, 200));

        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
        c.weighty = 0.0;

        gbl.setConstraints(bodyScroll, c);

        getContentPane().add(bodyScroll);

        JButton cancel = new JButton(this.bundle.getString(CANCEL));

        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
        c.weighty = 0.0;

        gbl.setConstraints(createButton, c);

        getContentPane().add(createButton);

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.0;
        c.weighty = 0.0;

        gbl.setConstraints(cancel, c);
        getContentPane().add(cancel);

        createButton.addMouseListener(new MouseAdapter() {
        	private Logger logger = SSLogger.getLogger(this.getClass());
            private AddMembership addMember = AddMembership.this;
            public void mousePressed(MouseEvent e) {

                Random r = new Random();

                int s = r.nextInt();
                if (s < 0)
                    s = -s;

                Integer int_s = new Integer(s);
                String password = new String(ppField.getPassword());
                this.logger.info("password: " + password);

                BigInteger x = getHashOf(new String(int_s.toString() + password));

                BigInteger v = this.addMember.g.modPow(x, this.addMember.n);

                this.logger.info("verifier: " + v.toString());
                this.logger.info("salt: " + int_s.toString());

                String contact_name = (String) this.addMember.list.getSelectedValue();
                String login_name = (String) this.addMember.contacts.get((String) this.addMember.list
                        .getSelectedValue());

                Document doc = XMLDoc(contact_name, login_name);

                doc.getRootElement().addElement("verifier").addAttribute(
                        "salt", int_s.toString()).setText(v.toString());

                this.addMember.session.put("delivery_type", "normal");

                // session.put("sphere_id",
                // mP.client.publishTerse(session,doc);
                // mP.client.registerMember(session, contact_name, login_name,
                // sphere_name);
                dispose();

            }

        });

    }

    public Document XMLDoc(String contact_name, String login_name) {

        String contactName = (String)this.session.get("contact_name");
        
        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("membership");

        email.addElement("status").addAttribute("value", "ratified");

        email.addElement("voting_model").addAttribute("type", "absolute")
                .addAttribute("desc", "Absolute without qualification");
        email.element("voting_model").addElement("tally").addAttribute(
                "number", "0.0").addAttribute("value", "0.0");

        email.addElement("giver").addAttribute("value", contactName);

        email.addElement("contact_name").addAttribute("value", contact_name);
        email.addElement("login_name").addAttribute("value", login_name);

        email.addElement("subject").addAttribute("value",
                "New member: " + contact_name);

        email.addElement("last_updated_by").addAttribute("value", contactName);
 
        email.addElement("type").addAttribute("value", "membership");
        email.addElement("thread_type").addAttribute("value", "membership");

        DefaultElement body = new DefaultElement("body");
        // body.setText(bodyEditor.getText());

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body");

        email.add(body);

        return createDoc;

    }

    public Document createMember(String contact_name, String login_name,
            String passphrase) {

        String newMemberIntegerString = getNewMemberRandomIntegerString();
        // String password = new String (ppField.getPassword());
        // System.out.println("password: "+password);

        BigInteger x = getHashOf(new String(newMemberIntegerString + passphrase));

        BigInteger v = this.g.modPow(x, this.n);

        // System.out.println("verifier: "+v.toString());
        // System.out.println("salt: "+int_s.toString());

        Document doc = XMLDoc(contact_name, login_name);

        doc.getRootElement().addElement("verifier").addAttribute("salt",
                newMemberIntegerString).setText(v.toString());

        return doc;
    }
    
    private String getNewMemberRandomIntegerString(){
        return (new Integer(Math.abs((new Random()).nextInt()))).toString();
    }

}
