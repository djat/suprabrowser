package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class SearchSphereHandler extends AbstractOldActionBuilder {
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(SearchSphereHandler.class);
	
    private final DialogsMainCli cli;

    public SearchSphereHandler(DialogsMainCli cli) {
    	this.cli = cli;
    }

    public String getProtocol() {
	return SSProtocolConstants.SEARCH_SPHERE;
    }

    @SuppressWarnings("unchecked")
	public void searchSphere(Hashtable session, Document sphere_definition,
	    String openBackground) {
	
    this.cli.addPendingToSupraSphereFrame(((String) session.get(SessionConstants.SPHERE_ID)));

	try {
	    Hashtable toSend = (Hashtable) session.clone();

	    Hashtable search = new Hashtable();
	    search.put(SessionConstants.PROTOCOL,
		    SSProtocolConstants.SEARCH_SPHERE);
	    search.put(SessionConstants.OPEN_BACKGROUND, openBackground);

	    search.put(SessionConstants.SESSION, toSend);

	    // search.put("sphere_type", session.get("sphere_type"));
	    if (sphere_definition != null) {
		search.put(SessionConstants.SPHERE_DEFINITION,
			sphere_definition);
	    }

	    this.cli.sendFromQueue(search);
	} catch (NullPointerException exc) {
	    logger.error("session or sendFromQueue throw a NPE", exc);
	}
    }

    @SuppressWarnings("unchecked")
	public void searchSphere(String sphereId, String messageId,String keywords,
			String openBackground) {
    	if(this.cli.getClass().equals(DialogsMainCli.class)) {
    		this.cli.getSF().getPendingSpheres().addPending(sphereId);
    	}
    	try {
    		Hashtable search = new Hashtable();
    		search.put(SessionConstants.PROTOCOL,
    				SSProtocolConstants.SEARCH_SPHERE);
    		search.put(SessionConstants.OPEN_BACKGROUND, openBackground);
    		search.put(SessionConstants.SPHERE_ID2, sphereId);
    		search.put(SessionConstants.MESSAGE_ID, messageId);
    		search.put(SessionConstants.SUPRA_SPHERE_SEARCH, messageId);
    		search.put(SessionConstants.KEYWORD_ELEMENT, keywords);
    		this.cli.sendFromQueue(search);
    	} catch (NullPointerException exc) {
    		logger.error("session or sendFromQueue throw a NPE", exc);
    	}
    }

}
