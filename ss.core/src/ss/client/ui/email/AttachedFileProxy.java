/**
 * 
 */
package ss.client.ui.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.activation.DataSource;

import org.apache.log4j.Logger;

import ss.domainmodel.FileStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;

/**
 * @author roman
 *
 */
public class AttachedFileProxy implements Serializable, IAttachedFile {

	private static final Logger logger = SSLogger.getLogger(AttachedFileProxy.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2311051759203494065L;

	private String name;
	
	private int size;

	private String dataId;
	
	public AttachedFileProxy(final FileStatement file) {
		this.name = file.getFilename();
		this.dataId = file.getDataId();
		this.size = Integer.parseInt(file.getBytes());
	}
	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public final InputStream createDataStream() {
		return null;//new ByteArrayInputStream( this.data );
	}

	/**
	 * @return
	 */
	public DataSource createDataSource() {
		String fsep = System.getProperty("file.separator");
		DialogsMainPeer peer = DialogsMainPeerManager.INSTANCE.getHandlers()
				.iterator().next();
		File file = new File(System.getProperty("user.dir") + fsep + "roots"
				+ fsep + peer.getVerifyAuth().getSupraSphereName() + fsep
				+ "File" + fsep + getOriginalDataId());
		byte[] bytes = new byte[getSize()];
		try {
			FileInputStream in = new FileInputStream( file );
			in.read(bytes);
		} catch (FileNotFoundException ex) {
			logger.error("File "+getName()+" not found in roots", ex);
		} catch (IOException ex) {
			logger.error("IO exception occurs during read "+getName(), ex);
		}
		return new AttachedFileDataSource(this.name, bytes);
	}
	
	public String getOriginalDataId() {
		return this.dataId;
	}
}
