/**
 * 
 */
package ss.smtp.reciever;

import java.io.Serializable;
import java.util.List;

import org.dom4j.Document;

import ss.smtp.reciever.file.CreatedFileInfo;

/**
 * @author zobo
 *
 */
public class MailData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5441999091498779846L;
	
	private RecieveList recieveList;
	
	private Document body;
	
	private List<CreatedFileInfo> files;

	public MailData(RecieveList recieveList, Document body, List<CreatedFileInfo> files) {
		super();
		this.recieveList = recieveList;
		this.body = body;
		this.files = files;
	}
	
	public MailData(){
		super();
	}

	/**
	 * @return the body
	 */
	public Document getBody() {
		return this.body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(Document body) {
		this.body = body;
	}

	/**
	 * @return the files
	 */
	public List<CreatedFileInfo> getFiles() {
		return this.files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<CreatedFileInfo> files) {
		this.files = files;
	}

	/**
	 * @return the recieveList
	 */
	public RecieveList getRecieveList() {
		return this.recieveList;
	}

	/**
	 * @param recieveList the recieveList to set
	 */
	public void setRecieveList(RecieveList recieveList) {
		this.recieveList = recieveList;
	} 

}
