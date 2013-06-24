/**
 * 
 */
package ss.client.networking.protocol.actions;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
public class SaveClubDealsCommand extends AbstractAction {

	private static final long serialVersionUID = -3221163419062682195L;

	public SaveClubDealsCommand(Document data) {
		super();
		putArg("bundledata", data);
	}
	
	public Document getData(){
		return (Document) getObjectArg("bundledata");
	}
}
