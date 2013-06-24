/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.Hashtable;

/**
 * @author zobo
 *
 */
class DeliverersList {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeliverersList.class);
	
	private Hashtable<String, Deliverer> deliverers = new Hashtable<String, Deliverer>();
	
	Deliverer get(String sphereId){
		synchronized (this.deliverers) {
			if (sphereId == null){
				return null;
			}
			return this.deliverers.get(sphereId);
		}
	}

	/**
	 * @param d
	 */
	void remove(Deliverer d) {
		synchronized (this.deliverers) {
			d.kill();
			this.deliverers.remove(d.getSphereId());
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	Deliverer getOrCreate(String sphereId) {
		synchronized (this.deliverers) {
			if (sphereId == null){
				return null;
			}
			Deliverer d = this.deliverers.get(sphereId);
			if (d == null){
				d = new Deliverer(sphereId);
				this.deliverers.put(sphereId, d);
			}
			return d;
		}
	}
}
