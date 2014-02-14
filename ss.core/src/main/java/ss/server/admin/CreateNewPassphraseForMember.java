package ss.server.admin;

import java.text.DateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.CreateMembership;
import ss.server.db.XMLDB;

public class CreateNewPassphraseForMember {
	
	
	public static void main(String[] args) {
		
		CreateNewPassphraseForMember cm = new CreateNewPassphraseForMember ();
		cm.changePassphrase("Punya","Punya");
		
	}
	
	
	public void changePassphrase(String sphere, String loginName) {
		
		XMLDB xmldb = new XMLDB();
		
		Document doc = xmldb.getMembershipDoc(sphere,loginName);
		
		CreateMembership cm = new CreateMembership();
		//Document memDoc = cm.createMember(contactName,username,passphrase);
        
        Document throwAway = cm.createMember("asdf","asdf","asdf");
        String verifier = throwAway.getRootElement().element("verifier").getText();
        
        Document forMachine = cm.createMember(sphere,sphere,verifier);
        
        String machineVerifier = forMachine.getRootElement().element("verifier").getText();
        String machineSalt = forMachine.getRootElement().element("verifier").attributeValue("salt");
        
        forMachine.getRootElement().addElement("machine_verifier").addAttribute("salt",machineSalt).setText(machineVerifier);
        forMachine.getRootElement().addElement("machine_pass").setText(verifier);
        
        Element root= forMachine.getRootElement();
        long longnum = System.currentTimeMillis();

		String message_id = (Long.toString(longnum));
		
		Date current = new Date();
		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(current) + " " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("subject").addAttribute("value","New Membership: "+loginName);
        root.addElement("giver").addAttribute("value",sphere);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);
		
        
		try {
        xmldb.removeDoc(doc,sphere);
		} catch (Exception e) {
			
		}
        
		
		xmldb.insertDoc(forMachine,sphere);
        
		//System.out.println("now what? "+doc.asXML());
		
		
	}
	
	
	

}
