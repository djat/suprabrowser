/**
 * 
 */
package ss.server.debug.commands;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.dom4j.Document;

import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereStatement;
import ss.server.db.XMLDB;
import ss.server.db.suprasphere.SupraSphereSingleton;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;

/**
 * @author zobo
 *
 */
public class CleanUpSpheresEmailsInSuprasphereDoc implements IRemoteCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CleanUpSpheresEmailsInSuprasphereDoc.class);
	
	/* (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ForceIndex started");
		}
		final StringWriter responce = new StringWriter();
		final PrintWriter writer = new PrintWriter(responce);
		perform( writer );
		writer.flush();
		final String toReturn = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Command finished, responce: " + toReturn);
		}
		return toReturn;
	}

	/**
	 * @param writer
	 * @param args
	 */
	private void perform(PrintWriter writer) {
		SupraSphereStatement suprast = null;
		XMLDB xmldb = new XMLDB();
		try {
			suprast = SupraSphereSingleton.INSTANCE.getStatement();
		} catch(Exception ex) {
			logger.error("can't update verify :(", ex);
			writer.println("Exception in getting suprasphere statement");
		}		
		
		if(suprast==null) {
			logger.warn("supra doc from database is null");
			writer.println("supra doc from database is null");
			return;
		}
		
		SphereEmailCollection spheresEmails = suprast.getSpheresEmails();
		if ( spheresEmails == null ) {
			writer.println("spheresEmails is null");
			return;
		}
		Vector<Document> docs = xmldb.getAllSpheres();
		if ( docs == null) {
			writer.println("AllSpheres is null");
			return;
		}
		writer.println("------");
		for ( Document doc : docs ) {
			SphereStatement st = SphereStatement.wrap(doc);
			String id = st.getSystemName();
			writer.println("id: " + id + ":");
			if ( st.isDeleted() ) {
				writer.println(" - sphere deleted");
				SphereEmail email = spheresEmails.getSphereEmailBySphereId(st.getSystemName());
				if ( email != null ) {
					writer.println(" *** email record deleted");
					spheresEmails.remove( email );
				}
			} else {
				writer.println(" - sphere valid");
			}
			writer.println("------");
		}
		
		xmldb.updateSupraSphereDoc(suprast.getBindedDocument());	
	}

}
