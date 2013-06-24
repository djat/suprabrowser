/**
 * 
 */
package ss.client.ui.messagedeliver;

import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class DeliverersManager {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeliverersManager.class);
	
	public static final DeliverersManager INSTANCE = new DeliverersManager();
	
	public static final IDeliveringElementsFactory FACTORY = new DeliveringElementFactory();
	
	private DeliverersList list;
	
	private static int countPanes = 0;
	
	private DeliverersManager(){
		this.list = new DeliverersList();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * insert current AbstractDeliveringElement to all messagesPanes opened for element.getSphereId()
	 * 
	 */
	public void insert(AbstractDeliveringElement element){
		if (logger.isDebugEnabled()){
			logger.debug("Inserting element " + element.getClass().getName() + " in " + element.getSphereId());
		}
		this.list.getOrCreate(element.getSphereId()).deliver(element);
	}
	
	/**
	 * insert current AbstractDeliveringElement only to current specificPane
	 * 
	 */
	public void insert(AbstractDeliveringElement element, MessagesPane specificPane) {
		if (logger.isDebugEnabled()){
			logger.debug("Inserting element " + element.getClass().getName() + " into specific " + element.getSphereId());
		}
		this.list.getOrCreate(element.getSphereId()).block(specificPane);
		SpecificElementCommonDeliverer.INSTANCE.put(element, specificPane);
	}
	
	/**
	 * Each MessagesPane created should register itself.
	 * @param pane
	 */
	public void register(MessagesPane pane){
		if (logger.isDebugEnabled()){
			logger.debug("Registering Messages Pane " + pane.getSystemName());
		}
		countPanes++;
		this.list.getOrCreate(pane.getSystemName()).addMessagesPane(pane);
	}
	
	/**
	 * Each MessagesPane closed should unregister itself.
	 * @param pane
	 */
	public void unregister(MessagesPane pane){
		if (logger.isDebugEnabled()){
			logger.debug("Unregistering Messages Pane" + pane.getSystemName());
		}
		Deliverer d = this.list.get(pane.getSystemName());
		if (d != null){
			d.removeMessagesPane(pane);
			countPanes--;
			if (d.isEmptyMessagesPanes()){
				if (logger.isDebugEnabled()){
					logger.debug("Messages pane list is empty for sphere " + pane.getSystemName() + ", deleting Deliverer");
				}
				d.stop();
				this.list.remove(d);
			}
		}
	}

	boolean check(AbstractDeliveringElement element, MessagesPane pane) {
		Deliverer d = this.list.getOrCreate(element.getSphereId());
		if (d.isMessagesPane(pane)){
			d.deliver(element, pane);
			return true;
		} else {
			return false;
		}
	}
	
	public int countPanes(){
		return countPanes;
	}
}
