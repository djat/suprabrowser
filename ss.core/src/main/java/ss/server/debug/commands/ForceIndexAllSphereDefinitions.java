/**
 * 
 */
package ss.server.debug.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.domainmodel.SphereStatement;
import ss.search.SphereIndex;
import ss.server.db.XMLDB;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;

/**
 * @author zobo
 *
 */
public class ForceIndexAllSphereDefinitions implements IRemoteCommand {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForceIndexAllSphereDefinitions.class);

	/* (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ForceIndexAllSphereDefinitions started");
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
	
	int i = 0;
	
	private void perform( final PrintWriter writer ){
		writer.println();
		writer.println("ForceIndexAllSphereDefinitions started");
		if (logger.isDebugEnabled()) {
			logger.debug("ForceIndexAllSphereDefinitions started");
		}
		writer.println();
		performForSpheres( writer );
		writer.println();
		writer.println("ForceIndexAllSphereDefinitions finished successfully");
		writer.println();
	}

	/**
	 * @param writer
	 */
	private void performForSpheres( final PrintWriter writer ) {
		final XMLDB xmldb = new XMLDB();
		final Vector<Document> spheres = xmldb.getAllSpheres(); 
		if ( (spheres == null) || (spheres.isEmpty())) {
			writer.println("There are no spheres definitions at all...");
		}
		int count = 0;
		for (Document doc : spheres) {
			if ( doc == null ) {
				continue;
			}
			SphereStatement st = SphereStatement.wrap(doc);
			count++;
			writer.println("--- sphere " + count + ": " + st.getDisplayName() + " ---");
			try {
				if (checkSphereInItsOwn(xmldb, st)) {
					performForSingleSphere( writer, st );
				} else {
					writer.println("Not in own sphere");
				}
			} catch (Exception e) {
				logger.error("", e);
				writer.println("(" +st.getDisplayName() + "," + st.getSystemName() + 
						") " + "error in inserting");
			}
			writer.println(".");
		}
	}
	
	private boolean checkSphereInItsOwn( final XMLDB xmldb, final SphereStatement st ){
		return (xmldb.getDocByMessageId(st.getMessageId(), st.getSystemName()) != null);
	}
	
	private void performForSingleSphere( final PrintWriter writer, final SphereStatement st ) {
		final String sphereId = st.getSystemName();
		
		if ( StringUtils.isBlank(sphereId) ) {
			writer.println("Sphere id is blank");
			return;
		}
		SphereIndex sphereIndex = null;
		try {
			sphereIndex = SphereIndex.get(sphereId);
		} catch (IOException ex) {
			logger.error( "TODO error message",ex);
		}
		if ( sphereIndex == null ) {
			writer.println("sphere index is null, can not obtain index");
			return;
		}
		final boolean existed = sphereIndex.removeDoc( st.getBindedDocument() );
		writer.println("Document existed in lucene: " + existed);
		writer.println("Document will be force added to lucene");
		final boolean operationResult = sphereIndex.addDoc( st.getBindedDocument(), true );
		writer.println("Operation perfomed: " + operationResult);
	}
}
