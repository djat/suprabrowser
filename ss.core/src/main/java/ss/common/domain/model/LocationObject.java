/**
 * 
 */
package ss.common.domain.model;


/**
 * @author roman
 *
 */
public class LocationObject extends DomainObject {

	private String url;
	
	private String exSystem;
	
	private String exDisplay;
	
	private long exMessage;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the exSystem
	 */
	public String getExSystem() {
		return this.exSystem;
	}

	/**
	 * @param exSystem the exSystem to set
	 */
	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}

	/**
	 * @return the exDisplay
	 */
	public String getExDisplay() {
		return this.exDisplay;
	}

	/**
	 * @param exDisplay the exDisplay to set
	 */
	public void setExDisplay(String exDisplay) {
		this.exDisplay = exDisplay;
	}

	/**
	 * @return the exMessage
	 */
	public long getExMessage() {
		return this.exMessage;
	}

	/**
	 * @param exMessage the exMessage to set
	 */
	public void setExMessage(long exMessage) {
		this.exMessage = exMessage;
	}
	
	
}
