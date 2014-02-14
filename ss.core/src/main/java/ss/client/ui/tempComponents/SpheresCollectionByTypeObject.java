/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Collection;

import ss.client.networking.DialogsMainCli;

/**
 * @author roman
 *
 */
public class SpheresCollectionByTypeObject {
	
	private Collection<String> groupSpheres;
	
	private Collection<String> privateSpheres;
	
	private Collection<String> personalSpheres;
	
	private final DialogsMainCli clientProtocol;
	
	public SpheresCollectionByTypeObject(final DialogsMainCli client) {
		super();
		this.clientProtocol = client;
		initSpheresCollections();	
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initSpheresCollections() {
		
		this.groupSpheres = this.clientProtocol.getVerifyAuth().getAvailableGroupSpheres();
		
		this.privateSpheres = this.clientProtocol.getVerifyAuth().getAvailablePrivateSpheres();
		
		this.personalSpheres = this.clientProtocol.getVerifyAuth().getOwnSpheres();
	}
	
	public Collection<String> getGroupSpheres() {
		return this.groupSpheres;
	}
	
	public Collection<String> getPrivateSpheres() {
		return this.privateSpheres;
	}
	
	public Collection<String> getPersonalSpheres() {
		return this.personalSpheres;
	}

}
