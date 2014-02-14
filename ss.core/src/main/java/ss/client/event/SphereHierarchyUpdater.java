/**
 * 
 */
package ss.client.event;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.processing.ForcedSpheresClosingProcessor;
import ss.common.VerifyAuth;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.FavouriteSphere;
import ss.domainmodel.FavouritesCollection;
import ss.domainmodel.Order;
import ss.domainmodel.OrderCollection;

/**
 * @author roman
 *
 */
public class SphereHierarchyUpdater implements Runnable {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHierarchyUpdater.class);
	
	private VerifyAuth oldVerify;
	
	private VerifyAuth newVerify;
	
	private final DialogsMainCli client;
	
	public SphereHierarchyUpdater(final DialogsMainCli cli, VerifyAuth oldVerify, VerifyAuth newVerify) {
		this.oldVerify = oldVerify;
		this.newVerify = newVerify;
		this.client = cli;
	}
	
	public void run() {
		List<String> added = getAddedSpheres();
		List<String> deleted = getDeletedSpheres();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Old VA " + this.oldVerify );
			logger.debug("New VA " + this.newVerify );
			logger.debug("added - "+added.size()+" spheres");
			logger.debug("removed - "+deleted.size()+" spheres");
		}
		
		handleSpheresDeleting(deleted);
		handleSpheresAdding(added);
		
		if(added.size()>0 || deleted.size()>0) {
			this.client.fireVerifyAuthChanged();
		}
	}

	/**
	 * @param deletedSpheres
	 */
	private void handleSpheresDeleting(List<String> deleted) {
		ContactStatement contact = this.newVerify.getContactStatement();
		if(!this.client.getClass().equals(DialogsMainCli.class)) {
			return;
		}
		ForcedSpheresClosingProcessor.INSTANCE.close(deleted);
		refreshFavouriteSpheres(deleted, contact);
		refreshTabOrder(deleted, contact);
		SupraSphereFrame.INSTANCE.getMenuBar().checkAddRemoveEnabled();
	}

	private void refreshFavouriteSpheres(List<String> deleted, ContactStatement contact) {
		FavouritesCollection favCollection = contact.getFavourites();
		
		for(FavouriteSphere favSphere : favCollection) {
			if(deleted.contains(favSphere.getSystemName())) {
				if(SupraSphereFrame.INSTANCE!=null) {
					SupraSphereFrame.INSTANCE.getMenuBar().removeFromFavourites(favSphere.getSystemName());
				}
				contact.removeSphereFromFavourites(favSphere);
				this.client.removeSphereFromFavourites(favSphere.getSystemName());
			}
		}
	}

	private void refreshTabOrder(List<String> deleted, ContactStatement contact) {
		boolean wasChanging = false;
		OrderCollection buildOrder = contact.getBuildOrder();
		for(Order order : buildOrder) {
			if(deleted.contains(order.getSystemName())) {
				buildOrder.remove(order);
				wasChanging = true;
			}
		}
		if(wasChanging) {
			Document orderDoc = DocumentHelper.createDocument();
			orderDoc.add(contact.getBindedDocument().getRootElement().element("build_order").createCopy());
			
			this.client.saveTabOrderToContact(this.client.session, orderDoc);
		}
	}

	/**
	 * @param addedSpheres
	 */
	private void handleSpheresAdding(List<String> added) {
		// TODO
	}

	/**
	 * @return
	 */
	private List<String> getDeletedSpheres() {
		List<String> oldSpheresId = this.oldVerify.getAvailableGroupSpheresId();
		List<String> newSpheresId = this.newVerify.getAvailableGroupSpheresId();
		
		List<String> deleted = new ArrayList<String>();
		for(String id : oldSpheresId) {
			if(!newSpheresId.contains(id)) {
				deleted.add(id);
			}
		}
		return deleted;
	}

	/**
	 * @return
	 */
	private List<String> getAddedSpheres() {
		List<String> oldSpheresId = this.oldVerify.getAvailableGroupSpheresId();
		List<String> newSpheresId = this.newVerify.getAvailableGroupSpheresId();
		
		List<String> added = new ArrayList<String>();
		for(String id : newSpheresId) {
			if(!oldSpheresId.contains(id)) {
				added.add(id);
			}
		}
		return added;
	}
}
