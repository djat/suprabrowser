/**
 * 
 */
package ss.client.ui.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.mail.internet.InternetAddress;


/**
 * @author zobo
 *
 */

public class EmailAddressesContainer implements Serializable{

	private static final long serialVersionUID = -3958775034504226510L;

	private static final String PARSE_BRAKER = ",";
    
    private final String originalCC;
    
    private final String originalBCC;

    private final ArrayList<InternetAddress> ccAddresses;
    
    private final ArrayList<InternetAddress> bccAddresses;
    
    private final String sendTo;
    
    private final String from;
    
    private final ArrayList<InternetAddress> replyTo;
    
    public EmailAddressesContainer(String sendTo, String from, String replyTo, String ccAddresses , String bccAddresses){
    	this.sendTo = sendTo;
        this.from = from;
        if (ccAddresses == null){
        	this.originalCC = "";
        } else {
        	this.originalCC = ccAddresses;
        }
        if (bccAddresses == null){
        	this.originalBCC = "";
        } else {
        	this.originalBCC = bccAddresses;
        }
        this.ccAddresses = parse(this.originalCC);
        this.bccAddresses = parse(this.originalBCC);
        this.replyTo = parse( (replyTo != null) ? replyTo : "" );
    }
    
    public EmailAddressesContainer(String sendTo, String from){
    	this( sendTo, from, null, "", "" );
    }
    
    private static ArrayList<InternetAddress> parse(String addresses){
        ArrayList<InternetAddress> array = new ArrayList<InternetAddress>();
        StringTokenizer t = new StringTokenizer(addresses, PARSE_BRAKER);
        try {
            while (t.hasMoreElements()){
                String str = t.nextToken();
                array.add(new InternetAddress(str.trim()));
            }
        } catch (Exception e) {
        }
        return array.size() > 0 ? array : null;
    }
    
    public InternetAddress[] getCCAdresses(){
        return (this.ccAddresses.toArray(new InternetAddress[this.ccAddresses.size()]));
    }
    
    public InternetAddress[] getBCCAdresses(){
        return (this.bccAddresses.toArray(new InternetAddress[this.bccAddresses.size()]));
    }

    /**
     * @return the originalBCC
     */
    public String getOriginalBCC() {
        return this.originalBCC;
    }

    /**
     * @return the originalCC
     */
    public String getOriginalCC() {
        return this.originalCC;
    }
    
    public boolean isCCExists(){
        if (this.ccAddresses == null)
            return false;
        if (this.ccAddresses.isEmpty())
        	return false;
        return true;
    }
    
    public boolean isBCCExists(){
        if (this.bccAddresses == null)
            return false;
        if (this.bccAddresses.isEmpty())
        	return false;
        return true;
    }
    
    public boolean isReplyToExists(){
        if (this.replyTo == null)
            return false;
        if (this.replyTo.isEmpty())
        	return false;
        return true;
    }
    
    public String getSendTo(){
        return this.sendTo;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * @return the replyTo
     */
    public InternetAddress[] getReplyTo() {
        return (this.replyTo.toArray(new InternetAddress[this.replyTo.size()]));
    }

	/**
	 * @return
	 */
	public Iterable<SendList> getSendLists() {
		SendListBuilder sendListBuilder = new SendListBuilder();
		sendListBuilder.addAddresses(parse(this.sendTo));
		sendListBuilder.addAddresses(this.bccAddresses );
		sendListBuilder.addAddresses(this.ccAddresses );
		return sendListBuilder.getResult();
	}
}
