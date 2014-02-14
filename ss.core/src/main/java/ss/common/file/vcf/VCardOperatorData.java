/**
 * 
 */
package ss.common.file.vcf;

import java.util.List;

import ss.common.file.AbstractSpecificFileData;
import ss.common.file.IDataForSpecificFileProcessingProvider;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class VCardOperatorData extends AbstractSpecificFileData {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardOperatorData.class);
	
	/**
	 * @param provider
	 */
	public VCardOperatorData( final IDataForSpecificFileProcessingProvider provider ) {
		super( provider );
	}

	private String systemFileName;
	
	private List<String> sphereIds;
	
	private DialogsMainPeer peer;
	
	private String giver;
	
	private NoteInfo noteToContact;

	public String getGiver() {
		return this.giver;
	}

	public DialogsMainPeer getPeer() {
		return this.peer;
	}

	public List<String> getSphereIds() {
		return this.sphereIds;
	}

	public String getSystemFileName() {
		return this.systemFileName;
	}

	@Override
	protected void fillUpData( final IDataForSpecificFileProcessingProvider provider ) {
		this.systemFileName = provider.getSystemFullPath();
		this.sphereIds = provider.getSphereIds();
		this.peer = provider.getPeer();
		this.giver = provider.getGiver();
		this.noteToContact = new NoteInfo( provider.getParentData().getSubject(), provider.getParentData().getBody() );
	}

	public NoteInfo getNote() {
		return this.noteToContact;
	}
}
