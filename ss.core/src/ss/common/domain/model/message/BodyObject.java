/**
 * 
 */
package ss.common.domain.model.message;

import ss.common.domain.model.DomainObject;


/**
 * @author roman
 *
 */
public class BodyObject extends DomainObject {

	private String version;
	
	private String bodyString;
	
	private String originalBodyString;

	/**
	 * @return the bodyString
	 */
	public String getBodyString() {
		return this.bodyString;
	}

	/**
	 * @param bodyString the bodyString to set
	 */
	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	/**
	 * @return the originalBodyString
	 */
	public String getOriginalBodyString() {
		return this.originalBodyString;
	}

	/**
	 * @param originalBodyString the originalBodyString to set
	 */
	public void setOriginalBodyString(String originalBodyString) {
		this.originalBodyString = originalBodyString;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}
