/**
 * 
 */
package ss.framework.install.update.loader;

import java.util.ArrayList;
import java.util.List;


import ss.client.networking.NetworkConnection;
import ss.common.ArgumentNullPointerException;
import ss.common.InstallUtils;
import ss.framework.install.QualifiedVersion;
import ss.framework.install.update.CantUpdateApplicationException;
import ss.framework.install.update.IFilesDownloader;
import ss.framework.networking2.blob.BlobLoaderListener;
import ss.framework.networking2.simple.SimpleProtocol;
import ss.framework.networking2.simple.SimpleProtocolException;

/**
 *
 */
public abstract class AbstractFilesDownloader implements IFilesDownloader {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractFilesDownloader.class);
	

	
	private final List<DownloadFile> downloadsQueue = new ArrayList<DownloadFile>();
		
	private QualifiedVersion targetApplicationVersion;

	/* (non-Javadoc)
	 * @see ss.framework.install.update.IFilesDownloader#initialize(java.lang.String, ss.framework.install.QualifiedVersion)
	 */
	public final void initialize(QualifiedVersion targetApplicationVersion) {
		if (targetApplicationVersion == null) {
			throw new ArgumentNullPointerException("targetApplicationVersion");
		}
		this.downloadsQueue.clear();
		this.targetApplicationVersion = targetApplicationVersion;
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.install.update.IFilesDownloader#add(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void addToQueue(String destinationPath, String resourceName, String resourceHash) {
		this.downloadsQueue.add( new DownloadFile( destinationPath, resourceName,resourceHash) ); 		
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.install.update.IFilesDownloader#download()
	 */
	public final void downloadAll() throws CantUpdateApplicationException {
		final NetworkConnection connection = openConnection( InstallUtils.UPDATE_FILE_TRANSFER_PROTOCOL_NAME );
		try {
			final SimpleProtocol protocol = new SimpleProtocol( connection.getDataIn(), connection.getDataOut() );
			BlobLoaderListener listener = createFilesDownloadListener();
			if ( listener != null ) {
				protocol.getFileDownloader().addListener(listener);
			}
			protocol.send( new DownloadFilesHello( this.targetApplicationVersion ) );
			DownloadFilesReply reply = protocol.receive(DownloadFilesReply.class);
			if ( reply.isDownloadAllowed() )
			{
				for( DownloadFile file : this.downloadsQueue ) {
					final DownloadFileHeader downloadFileHeader = file.createDownloadFileHeader();
					if ( logger.isInfoEnabled() ) {
						logger.info( "try to download " + downloadFileHeader );
					}
					protocol.send( downloadFileHeader );
					final String destinationPath = file.getDestinationPath();
					protocol.downloadFile( destinationPath );
					if ( logger.isInfoEnabled() ) {
						logger.info( "downloaded " + destinationPath );
					}
				}
			}
			else {
				throw new CantUpdateApplicationException( "Download denied. " + reply.getMessage() );
			}
			protocol.close();
		} catch (SimpleProtocolException ex) {
			final String message = "Download communication failed";
			logger.warn( message );
			throw new CantUpdateApplicationException( message, ex );
		}
		finally {
			connection.close();
		}
	}
	
	/**
	 * @return
	 */
	protected BlobLoaderListener createFilesDownloadListener() {
		return null;
	}

	/**
	 * @return
	 * @throws CantUpdateApplicationException 
	 */
	protected abstract NetworkConnection openConnection(String protocolName) throws CantUpdateApplicationException;

	static class DownloadFile {
		
		private final String destinationPath;
		
		private final String resourceName;
		
		private final String resourceHash;

		/**
		 * @param destinationPath
		 * @param resourceName
		 * @param resourceHash
		 */
		public DownloadFile(String destinationPath, String resourceName, String resourceHash) {
			super();
			this.destinationPath = destinationPath;
			this.resourceName = resourceName;
			this.resourceHash = resourceHash;
		}

		/**
		 * @return
		 */
		public DownloadFileHeader createDownloadFileHeader() {
			return new DownloadFileHeader( this.resourceName, this.resourceHash );
		}

		/**
		 * @return the destinationPath
		 */
		public String getDestinationPath() {
			return this.destinationPath;
		}

		/**
		 * @return the resourceName
		 */
		public String getResourceName() {
			return this.resourceName;
		}

		/**
		 * @return the resourceHash
		 */
		public String getResourceHash() {
			return this.resourceHash;
		}
		
	}
	
}
