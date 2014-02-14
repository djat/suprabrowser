package ss.server.networking.filetransfer;

import org.dom4j.Document;

import ss.client.networking.NetworkConnection;
import ss.common.ThreadUtils;
import ss.common.simplefiletransfer.DownloadFileInfo;
import ss.domainmodel.FileStatement;
import ss.framework.networking2.simple.SimpleProtocol;
import ss.framework.networking2.simple.SimpleProtocolException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.SC;

public class FileDownloadHandler {


	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileDownloadHandler.class);
	
	private final NetworkConnection connection;
	
	/**
	 * @param connection
	 */
	public FileDownloadHandler(final NetworkConnection connection) {
		super();
		this.connection = connection;
	}

	public void start() {
		ThreadUtils.start( new Runnable() {
			public void run() {
				safeSend();
			}		
			
		}, getClass() );
	}
	
	private void safeSend() {
		try {
			send();
		} catch (SimpleProtocolException ex) {
			logger.error( "Can't upload application update",  ex );
		}
		catch(Throwable ex ) {
			logger.error( "Can't upload application update",  ex );
		}
		
	}
	private void send() throws SimpleProtocolException {
		try {
			final SimpleProtocol protocol = new SimpleProtocol( this.connection.getDataIn(), this.connection.getDataOut() );
			DownloadFileInfo info = protocol.receive( DownloadFileInfo.class );
			String messageId = info.getMessageId();
			DialogsMainPeer adminPeer = null;
			for(DialogsMainPeer peer : DialogsMainPeerManager.INSTANCE.getHandlers()) {
				if(peer.getVerifyAuth().isAdmin()) {
					adminPeer = peer;
					break;
				}
			}
			if(adminPeer==null) {
				return;
			}
			Document doc = adminPeer.getXmldb().getSpecificMessage(messageId);
			if(doc==null) {
				return;
			}
			String dataId = FileStatement.wrap(doc).getDataId();
			String bdir = System.getProperty("user.dir");
			String fsep = System.getProperty("file.separator");
			
			final String fileName = bdir + fsep + "roots" + fsep
			+ (String) adminPeer.getSession().get(SC.SUPRA_SPHERE) + fsep + "File"
			+ fsep + dataId;
			
			protocol.uploadFile( fileName );
			protocol.close();
		} 
		finally {
			this.connection.close();
		}
		
	}

	/**
	 * @param networkConnection
	 */
	public static void createAndStart(NetworkConnection connection ) {
		new FileDownloadHandler( connection ).start();
	}

}
