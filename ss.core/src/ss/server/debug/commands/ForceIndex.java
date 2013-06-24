/**
 * 
 */
package ss.server.debug.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.search.SphereIndex;
import ss.server.db.XMLDB;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;

/**
 * @author zobo
 *
 */
public class ForceIndex implements IRemoteCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForceIndex.class);
	
	/* (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ForceIndex started");
		}
		final StringWriter responce = new StringWriter();
		final PrintWriter writer = new PrintWriter(responce);
		perform( writer, context.getArgs() );
		writer.flush();
		final String toReturn = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Command finished, responce: " + toReturn);
		}
		return toReturn;
	}

	/**
	 * @param writer
	 */
	private void perform( final PrintWriter writer, final String args) {
		if ( StringUtils.isBlank( args ) ) {
			writer.println("args is blank");
			return;
		}
		String messageId = null;
		String sphereId = null;
		try {
			final String data = args.trim();
			StringTokenizer token = new StringTokenizer(data, " ");
			messageId = token.nextToken();
			sphereId = token.nextToken();
		} catch ( Exception ex ) {
			writer.println("could not parse messageId and sphereId");
			return;
		}
		if (StringUtils.isBlank(messageId)) {
			writer.println("messageId is blank");
			return;
		}
		if (StringUtils.isBlank(sphereId)) {
			writer.println("sphereId is blank");
			return;
		}
		writer.println("MessageId from args: " + messageId);
		writer.println("sphereId from args: " + sphereId);
		XMLDB xmldb = new XMLDB();
		final Document doc = xmldb.getSpecificMessage(messageId);
		if ( doc == null ) {
			writer.println("Document is null");
			return;
		} else {
			writer.println("---------------");
			writer.println("Document: " + doc.asXML());
			writer.println("---------------");
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
		final boolean existed = sphereIndex.removeDoc(doc);
		writer.println("Document existed in lucene: " + existed);
		writer.println("Document will be force added to lucene");
		final boolean operationResult = sphereIndex.addDoc( doc, true );
		writer.println("Operation perfomed: " + operationResult);
	}

}
