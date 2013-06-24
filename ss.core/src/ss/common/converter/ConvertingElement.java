/**
 * 
 */
package ss.common.converter;

import java.util.Hashtable;

import org.dom4j.Document;

/**
 * @author zobo
 * 
 */
public class ConvertingElement {

	private final Hashtable session;

	private final String threadId;

	private final String messageId;

	private final String name;

	private final boolean publish;

	private final String sphereId;

	private final Document doc;

	private final String contact;

	private boolean indexing = true;

	public ConvertingElement(final Hashtable session, final String threadId,
			final String messageId, final String name, final String sphereId,
			final Document fileDoc, final String contact, final boolean publish, final boolean indexing) {
		super();
		this.session = session;
		this.threadId = threadId;
		this.messageId = messageId;
		this.name = name;
		this.sphereId = sphereId;
		this.doc = fileDoc;
		this.contact = contact;
		this.publish = publish;
		this.indexing = indexing;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public String getName() {
		return this.name;
	}

	public Hashtable getSession() {
		return this.session;
	}

	public String getThreadId() {
		return this.threadId;
	}

	public boolean isPublish() {
		return this.publish;
	}

	/**
	 * @return
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @return
	 */
	public Document getDoc() {
		return this.doc;
	}

	/**
	 * @return
	 */
	public String getContact() {
		return this.contact;
	}

	/**
	 * @return
	 */
	public boolean isIndexing() {
		return this.indexing ;
	}
}
