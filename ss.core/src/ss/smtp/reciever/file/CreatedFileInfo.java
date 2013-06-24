/**
 * 
 */
package ss.smtp.reciever.file;

import java.io.Serializable;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
public class CreatedFileInfo implements Serializable {

	private static final long serialVersionUID = 7538751011036564537L;

	private final String systemFileName;
	
	private final String originalFileName;
	
	private final int fileSize;
	
	private Document fileDocument = null;

	public CreatedFileInfo(final String systemFileName, final String originalFileName, final int fileSize) {
		super();
		this.systemFileName = systemFileName;
		this.originalFileName = originalFileName;
		this.fileSize = fileSize;
	}

	public int getFileSize() {
		return this.fileSize;
	}

	public String getOriginalFileName() {
		return this.originalFileName;
	}

	public String getSystemFileName() {
		return this.systemFileName;
	}

	public Document getFileDocument() {
		return this.fileDocument;
	}

	public void setFileDocument(Document fileDocument) {
		this.fileDocument = fileDocument;
	}
}
