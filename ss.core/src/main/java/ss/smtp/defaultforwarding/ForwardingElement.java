/**
 * 
 */
package ss.smtp.defaultforwarding;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
public class ForwardingElement {
	private Document doc;
	
	private String sphereId;
	
	private ForsedForwardingData forcedForwardingData; 

	public Document getDoc() {
		return this.doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getSphereId() {
		return this.sphereId;
	}

	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	public ForwardingElement(Document doc, String sphereId) {
		this(doc, sphereId, null);
	}
	
	public ForwardingElement(Document doc, String sphereId, ForsedForwardingData forcedForwardingData) {
		this.doc = doc;
		this.sphereId = sphereId;
		this.forcedForwardingData = forcedForwardingData;
	}

	public ForsedForwardingData getForcedForwardingData() {
		return this.forcedForwardingData;
	}

	public void setForcedForwardingData(ForsedForwardingData forcedForwardingData) {
		this.forcedForwardingData = forcedForwardingData;
	}
}