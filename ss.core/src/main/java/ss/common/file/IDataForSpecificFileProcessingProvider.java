/**
 * 
 */
package ss.common.file;

import java.util.List;

import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public interface IDataForSpecificFileProcessingProvider {

	public String getFileName();
	
	public String getGiver();
	
	public DialogsMainPeer getPeer();
	
	public List<String> getSphereIds();
	
	public String getSystemFullPath();

	public ParentStatementData getParentData();
}
