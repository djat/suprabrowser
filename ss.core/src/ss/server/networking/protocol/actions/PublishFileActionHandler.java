/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.PublishFileAction;
import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.IAttachedFile;
import ss.common.StringUtils;
import ss.common.converter.SimpleFileDocumentConverter;
import ss.common.file.DefaultDataForSpecificFileProcessingProvider;
import ss.common.file.ParentStatementData;
import ss.common.file.SpecificFileProcessor;
import ss.domainmodel.FileStatement;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.processing.FileProcessor;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class PublishFileActionHandler extends AbstractActionHandler<PublishFileAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PublishFileActionHandler.class);
	
	@SuppressWarnings("unused")
	private static final String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private static final String fsep = System.getProperty("file.separator");
	
	public PublishFileActionHandler(final DialogsMainPeer peer) {
		super(PublishFileAction.class, peer);
	}
	
	@Override
	protected void execute(PublishFileAction action) {
		final String subject = action.getStringArg(SessionConstants.SUBJECT);
		final String body = action.getStringArg(SessionConstants.BODY);
		final String desiredMessageId = action.getStringArg(SessionConstants.MESSAGE_ID);
		final AttachedFileCollection files = (AttachedFileCollection)action.getObjectArg(SessionConstants.FILES); 
		final Hashtable session = (Hashtable)action.getSessionArg();
		final String sphereId = (String)session.get(SessionConstants.SPHERE_ID);
		
		final String realName = (String)session.get(SessionConstants.REAL_NAME);
		final String supraSphereName = (String)this.peer.getSession().get(SC.SUPRA_SPHERE);
		
		for (IAttachedFile file : files){
			try {
				Document fileDoc = FileProcessor.INSTANCE.processFile(null , "file", (AttachedFile)file, session, (String)session.get(SessionConstants.REAL_NAME), desiredMessageId, subject, body);
				
				SpecificFileProcessor.INSTANCE.process( getProvider(realName, supraSphereName, 
						file, fileDoc, sphereId, 
						new ParentStatementData( StringUtils.getNotNullString(body), StringUtils.getNotNullString(subject))));
				this.peer.getXmldb().insertDoc(fileDoc, sphereId);
				SimpleFileDocumentConverter.convert(this.peer.getSession(), supraSphereName, realName, sphereId, fileDoc);
				
			} catch (Throwable ex) {
				logger.error( "Error processing attached file to message: " + file.getName() ,ex);
			}
		}
	}
	
	private DefaultDataForSpecificFileProcessingProvider getProvider( final String realName, 
			final String supraSphereName, final IAttachedFile file, final Document fileDoc,
			final String sphereId, final ParentStatementData parentData){
		final DefaultDataForSpecificFileProcessingProvider provider = new DefaultDataForSpecificFileProcessingProvider();
		provider.setGiver( realName );
		final String systemFullName = bdir + fsep + "roots" + fsep	+ supraSphereName
			+ fsep + "File"	+ fsep + FileStatement.wrap(fileDoc).getDataId();
		provider.setSystemFullPath( systemFullName );
		provider.setFileName( file.getName() );
		provider.setPeer(this.peer);
		final List<String> sphereIds = new ArrayList<String>();
		sphereIds.add( sphereId );
		provider.setSphereIds( sphereIds );
		provider.setParentData( parentData );	
		return provider;
	}
}
