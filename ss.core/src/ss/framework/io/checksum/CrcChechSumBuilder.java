/**
 * 
 */
package ss.framework.io.checksum;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 *
 */
public class CrcChechSumBuilder extends AbstractCheckSumBuilder {

	private final Checksum checksum;
	
	/**
	 * 
	 */
	public CrcChechSumBuilder() {
		super();
		this.checksum = new CRC32();
	}

	/* (non-Javadoc)
	 * @see ss.framework.io.checksum.AbstractCheckSumBuilder#add(byte[], int)
	 */
	@Override
	public void add(byte[] buff, int length) {
		this.checksum.update(buff, 0, length);
	}

	/* (non-Javadoc)
	 * @see ss.framework.io.checksum.AbstractCheckSumBuilder#getResult()
	 */
	@Override
	public String getResult() {
		return String.valueOf( this.checksum.getValue() );
	}
	
}
