/**
 * 
 */
package ss.framework.io.checksum;

/**
 *
 */
public abstract class AbstractCheckSumBuilder {

	/**
	 * @return
	 */
	public abstract String getResult();

	/**
	 * @param buff
	 * @param numRead
	 */
	public abstract void add(byte[] buff, int length);
	
	protected final static String toHex(byte[] buff) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buff.length; i++) {
			sb.append(Integer.toString((buff[i] & 0xff) + 0x100, 16).substring(
					1));
		}
		return sb.toString();
	}
	
}
