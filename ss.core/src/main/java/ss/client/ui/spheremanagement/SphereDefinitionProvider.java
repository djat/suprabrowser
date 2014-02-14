/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.UserSession;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;


/**
 *
 */
public class SphereDefinitionProvider extends AbstractOutOfDateable implements
		ISphereDefinitionProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereDefinitionProvider.class);
	
	protected final DialogsMainCli clientProtocol;
	
	private List<SphereStatement> allSpheres;

	private VerifyAuth verifyAuth = null;
	
	private SupraSphereMember ownerUser = null;

	private boolean adminUser = false;
	
	/**
	 * @param clientProtocol
	 */
	public SphereDefinitionProvider(final DialogsMainCli clientProtocol) {
		super();
		this.clientProtocol = clientProtocol;
	}

	/**
	 * 
	 */
	@Override
	protected final void reload() {
		beforeReload();
		if (logger.isDebugEnabled()){
			logger.debug("AbstractSphereDefinitionProvider rebuild started");
		}
		final Vector<Document> allSpheresDocuments = this.clientProtocol.getAllSpheres();
		this.allSpheres = new ArrayList<SphereStatement>();
		for( Document doc : allSpheresDocuments  ) {
			final SphereStatement sphere = SphereStatement.wrap(doc);
			this.allSpheres.add( sphere );
		}
		if (logger.isDebugEnabled()){
			logger.debug("ClientSphereDefinitionProvider rebuild finished, spheres recieved: " + this.allSpheres.size() );
		}
	}

	/**
	 * 
	 */
	private void beforeReload() {
		this.verifyAuth = this.clientProtocol.getVerifyAuth();
		final UserSession userSession = this.verifyAuth.getUserSession();
		final String userLogin = userSession.getUserLogin();
		this.ownerUser = this.verifyAuth.getSupraSphere().getSupraMemberByLoginName( userLogin );
		if ( this.ownerUser == null ) {
			throw new IllegalStateException( "Can't find supra member by " + userLogin );
		}
		this.adminUser = this.verifyAuth.isAdmin();
	}

	public final Collection<SphereStatement> getAllSpheres() {
		return this.allSpheres;
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#getRootId()
	 */
	public String getRootId() {
		return this.clientProtocol.getVerifyAuth().getLoginSphere(
				this.clientProtocol.getLogin());
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#isSphereVisible(ss.domainmodel.SphereStatement)
	 */
	public boolean isSphereVisible(SphereStatement sphere) {
		if ( this.ownerUser.getSpheres().isEnabled( sphere.getSystemName() ) ) {
			return true;
		}
		else {
			return this.isAdminUser() && !sphere.isEmailBox();
		}	
	}

	/**
	 * @return the adminUser
	 */
	protected boolean isAdminUser() {
		return this.adminUser;
	}
	
	
	
}
