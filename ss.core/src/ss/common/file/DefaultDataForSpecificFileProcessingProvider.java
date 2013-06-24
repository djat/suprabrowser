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
public class DefaultDataForSpecificFileProcessingProvider implements IDataForSpecificFileProcessingProvider {
	
	private String systemFullPath;
	
	private String fileName;
	
	private String giver;
	
	private DialogsMainPeer peer;
	
	private List<String> sphereIds;
	
	private ParentStatementData parentData;

	public void setParentData(ParentStatementData parentData) {
		this.parentData = parentData;
	}

	public DefaultDataForSpecificFileProcessingProvider(final String systemFilePath, final String fileName, 
			final String giver, final DialogsMainPeer peer, final List<String> sphereIds, final ParentStatementData parentData) {
		super();
		this.systemFullPath = systemFilePath;
		this.fileName = fileName;
		this.giver = giver;
		this.peer = peer;
		this.sphereIds = sphereIds;
		this.parentData = parentData;
	}

	public DefaultDataForSpecificFileProcessingProvider() {
		this(null, null, null, null, null, null);
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getGiver() {
		return this.giver;
	}

	public void setGiver(String giver) {
		this.giver = giver;
	}

	public DialogsMainPeer getPeer() {
		return this.peer;
	}

	public void setPeer(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public List<String> getSphereIds() {
		return this.sphereIds;
	}

	public void setSphereIds(List<String> sphereIds) {
		this.sphereIds = sphereIds;
	}

	public String getSystemFullPath() {
		return this.systemFullPath;
	}

	public void setSystemFullPath(String systemFilePath) {
		this.systemFullPath = systemFilePath;
	}

	public ParentStatementData getParentData() {
		return this.parentData;
	}
}
