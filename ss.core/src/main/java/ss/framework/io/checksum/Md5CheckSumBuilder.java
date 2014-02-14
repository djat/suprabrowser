/**
 * 
 */
package ss.framework.io.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 */
public class Md5CheckSumBuilder extends AbstractCheckSumBuilder {

	private final MessageDigest checkSum;
	
	/**
	 * @throws NoSuchAlgorithmException 
	 */
	public Md5CheckSumBuilder() throws NoSuchAlgorithmException {
		super();
		this.checkSum = MessageDigest.getInstance("MD5");
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.io.checksum.AbstractCheckSumBuilder#add(byte[], int)
	 */
	@Override
	public void add(byte[] buff, int length) {
		this.checkSum.update( buff, 0, length );		
	}

	/* (non-Javadoc)
	 * @see ss.framework.io.checksum.AbstractCheckSumBuilder#getResult()
	 */
	@Override
	public String getResult() {
		return toHex(this.checkSum.digest());
	}

	
}
