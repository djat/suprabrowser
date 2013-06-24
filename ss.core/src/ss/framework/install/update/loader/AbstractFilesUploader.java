/**
 * 
 */
package ss.framework.install.update.loader;


import ss.client.networking.NetworkConnection;
import ss.common.ThreadUtils;
import ss.framework.install.QualifiedVersion;
import ss.framework.networking2.simple.SimpleProtocol;
import ss.framework.networking2.simple.SimpleProtocolException;

/**
 *
 */
public abstract class AbstractFilesUploader {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractFilesUploader.class);
	
	private final NetworkConnection connection;
	
	/**
	 * @param connection
	 */
	public AbstractFilesUploader(final NetworkConnection connection) {
		super();
		this.connection = connection;
	}

	public void start() {
		ThreadUtils.start( new Runnable() {
			public void run() {
				safeUpload();
			}		
			
		}, getClass() );
	}
	
	private void safeUpload() {
		try {
			upload();
		} catch (SimpleProtocolException ex) {
			logger.error( "Can't upload application update",  ex );
		}
		catch(Throwable ex ) {
			logger.error( "Can't upload application update",  ex );
		}
		
	}
	private void upload() throws SimpleProtocolException {
		try {
			final SimpleProtocol protocol = new SimpleProtocol( this.connection.getDataIn(), this.connection.getDataOut() );
			final DownloadFilesHello hello = protocol.receive( DownloadFilesHello.class );
			final QualifiedVersion applicationVersion = hello.getTargetApplicationVersion();
			IFilePathResolver filesResolver = findFilesResolver( applicationVersion );
			if ( filesResolver == null ) {
				protocol.send( DownloadFilesReply.createFailedReply( "Can't find installation version for " + applicationVersion  ) );
			}
			else {
				protocol.send( DownloadFilesReply.createSucceededReply() );
				DownloadFileHeader fileHeader;
				while( ( fileHeader = protocol.tryReceive( DownloadFileHeader.class ) ) != null ) {
					final String path = filesResolver.resolve( fileHeader );
					if ( path != null ) {
						protocol.uploadFile(path);
					}
					else {
						protocol.cantUpload( "File not found " + fileHeader.getRemotePath() );
					}					
				}
			}			
			protocol.close();
		} 
		finally {
			this.connection.close();
		}
		
	}

	/**
	 * @param applicationName
	 * @param applicationVersion
	 * @return
	 */
	protected abstract IFilePathResolver findFilesResolver( QualifiedVersion applicationVersion);	
	 
}
