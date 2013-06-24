package ss.client.networking.filetransfer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking.NetworkConnectionProvider;
import ss.common.simplefiletransfer.DownloadFileInfo;
import ss.common.simplefiletransfer.SimpleFileTransferUtils;
import ss.framework.networking2.blob.CantTransferBlobException;
import ss.framework.networking2.simple.SimpleProtocol;
import ss.framework.networking2.simple.SimpleProtocolException;

public class SimpleFileDownloader {

	final Hashtable<String,String> startupSession;

	/**
	 * @param startupSession
	 */
	public SimpleFileDownloader(Hashtable<String,String> startupSession) {
		super();
		this.startupSession = startupSession;
	}

	public void downloadAndClose(DownloadFileInfo fileInfo, OutputStream outputStream)
			throws CantDownloadFileException {
		NetworkConnectionProvider connectionProvider = NetworkConnectionFactory.INSTANCE
				.createProvider(SimpleFileTransferUtils.DOWNLOAD_PROTOCOL_NAME,
						this.startupSession);
		SimpleProtocol client;
		try {
			client = connectionProvider.openSimpleProtocol();
		} catch (IOException ex) {
			throw new CantDownloadFileException("Can't establish connection",
					ex);
		}
		try {
			try {
				client.send(fileInfo);
			} catch (SimpleProtocolException ex) {
				throw new CantDownloadFileException(
						"Can't send download request", ex);
			}
			try {
				client.getFileDownloader().download(outputStream);
			} catch (CantTransferBlobException ex) {
				throw new CantDownloadFileException("Can't download file data",
						ex);
			}
		} finally {
			client.close();
		}
	}
}
