/**
 * 
 */
package ss.client.ui.email;

import java.io.*;
import javax.activation.*;
import ss.common.PathUtils;

/**
 * 
 */
public class AttachedFile implements Serializable, IAttachedFile {
    
	private static final long serialVersionUID = 3407437194644164954L;

	protected String name;
	
	protected byte [] data;
	
	public AttachedFile( String name, byte[] data ) {
		this.name = name;		
		this.data = data;
	}
	
	public AttachedFile(String localFullFileName ) throws IOException {
		FileInputStream in = new FileInputStream( localFullFileName );
		final int size = in.available();
		this.data = new byte[ size ];
		in.read( this.data );
		this.name = PathUtils.getFileNameOnly( localFullFileName );
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.data.length;
	}
	
	public final InputStream createDataStream() {
		return new ByteArrayInputStream( this.data );
	}

	/**
	 * @return
	 */
	public DataSource createDataSource() {
		return new AttachedFileDataSource( this.name, this.data );
	}
	
}
