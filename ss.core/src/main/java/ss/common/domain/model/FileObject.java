/**
 * 
 */
package ss.common.domain.model;

/**
 * @author roman
 *
 */
public class FileObject extends DomainObject {

	private int bytes;
	
	private String dataId;

	/**
	 * @return the bytes
	 */
	public int getBytes() {
		return this.bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return the dataId
	 */
	public String getDataId() {
		return this.dataId;
	}

	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
	
}
