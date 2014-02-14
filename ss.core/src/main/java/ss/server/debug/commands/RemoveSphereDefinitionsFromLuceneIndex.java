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
public class RemoveSphereDefinitionsFromLuceneIndex implements IRemoteCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RemoveSphereDefinitionsFromLuceneIndex.class);
	
	/* (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command RemoveSphereDefinitionsFromLuceneIndex started");
		}
		final StringWriter responce = new StringWriter();
		final PrintWriter writer = new PrintWriter(responce);
		perform(writer);
		writer.flush();
		final String toReturn = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Command finished, responce: " + toReturn);
		}
		return toReturn;
	}

	private void perform( final PrintWriter writer ) {
		try {
			writer.println();
			writer.println("RemoveSphereDefinitionsFromLuceneIndex started");
			if (logger.isDebugEnabled()) {
				logger.debug("RemoveSphereDefinitionsFromLuceneIndex started");
			}
			writer.println();
			performForSpheres( (new XMLDB()).getAllSpheres(), writer );
			writer.println();
			writer.println("RemoveSphereDefinitionsFromLuceneIndex finished successfully");
			writer.println();
		} catch (Throwable ex) {
			logger.error("RemoveSphereDefinitionsFromLuceneIndex failed", ex);
			writer.println("RemoveSphereDefinitionsFromLuceneIndex failed : "
					+ ex.toString());
		}
	}

	/**
	 * @param spheres
	 */
	private void performForSpheres( final Vector<Document> spheres, final PrintWriter writer ) {
		if ( (spheres == null) || (spheres.isEmpty())) {
			writer.println("There are no spheres definitions at all...");
		}
		int count = 0;
		for (Document doc : spheres) {
			if ( doc == null ) {
				continue;
			}
			count++;
			SphereStatement st = SphereStatement.wrap(doc);
			String sphereCoreId = st.getSphereCoreId();
			if (StringUtils.isBlank(sphereCoreId)) {
				writer.println("" + count + ": (" +st.getDisplayName() + "," + st.getSystemName() + 
						") has no sphereCoreId, will be skipped");
				continue;
			}
			try {
				boolean result = performForSingleSphere( doc, sphereCoreId );
				writer.println("" + count + ": (" +st.getDisplayName() + "," + st.getSystemName() + 
						") " + (result ? "is removed from ": "not found in ") + " sphereCoreId: " + sphereCoreId);
			} catch (IOException e) {
				logger.error("", e);
				writer.println("" + count + ": (" +st.getDisplayName() + "," + st.getSystemName() + 
						") " + "error for " + " sphereCoreId: " + sphereCoreId + "; message: " + e.getMessage());
			}
		}
	}

	private boolean performForSingleSphere( final Document doc, final String sphereCoreId ) throws IOException{
		SphereIndex index = SphereIndex.get(sphereCoreId);
		return index.removeDoc(doc);
	}
}
