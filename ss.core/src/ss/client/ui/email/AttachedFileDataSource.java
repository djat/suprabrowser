/**
 * 
 */
package ss.client.ui.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import ss.common.UnexpectedRuntimeException;

/**
 *
 */
public class AttachedFileDataSource implements DataSource {
	
	private FileTypeMap typeMap;	 
	 
	private final String name;
	
	private final byte [] data;

	/**
	 * @param name
	 * @param data
	 */
	public AttachedFileDataSource(String name, byte [] data) {
		super();
		this.name = name;
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		if ( this.typeMap == null ) {
			this.typeMap = FileTypeMap.getDefaultFileTypeMap();
		}
		return this.typeMap.getContentType( getName() );
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream( this.data );
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new UnexpectedRuntimeException( "Method not implemented" );
	}
	
	
}
