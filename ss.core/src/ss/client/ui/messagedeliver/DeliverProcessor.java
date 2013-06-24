/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ss.client.ui.InsertStatementListOperation;
import ss.client.ui.MessagesPane;
import ss.client.ui.messagedeliver.AbstractDeliveringElement.DeliveringElementType;
import ss.common.operations.OperationProgressEvent;

/**
 * @author zobo
 *
 */
class DeliverProcessor {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeliverProcessor.class);
	
	private final List<AbstractDeliveringElementProcessor> processors = new ArrayList<AbstractDeliveringElementProcessor>();
		
	@SuppressWarnings("unused")
	private AbstractDeliveringElementProcessor find( AbstractDeliveringElement element ) {
		final Class elementClass = element.getClass();
		for( AbstractDeliveringElementProcessor processor : this.processors ) {
			if ( processor.getDeliveringElementClass() == elementClass ) {
				return processor;
			}
		}
		return null;
	}
	
	static void process(AbstractDeliveringElement element, List<MessagesPane> panes){
		if ((panes == null)||(panes.isEmpty())){
			if (logger.isDebugEnabled()){
				//logger.debug("Messages panes is NULL for current element with sphereId: " + element.getSphereId());
			}
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("type of delevering element : "+element.getType());
		}
//		AbstractDeliveringElementProcessor<AbstractDeliveringElement> processor = findProccessor( element );
//		processor.process(element);		
		
		if (element.getType() == DeliveringElementType.SIMPLE){
			process(SimpleDeliveringElement.class.cast(element), panes);
		} else if (element.getType() == DeliveringElementType.LIST){
			process(ListDeliveringElement.class.cast(element), panes);
		} else if (element.getType() == DeliveringElementType.REPLACE){
			process(ReplaceDeliveringElement.class.cast(element), panes);
		} else {
			logger.warn("No such delivering element" + element);
		}
	}

	static private void process(ListDeliveringElement element, List<MessagesPane> panes){
		if (logger.isDebugEnabled()){
			logger.debug("Processing next ListDeliveringElement " + DeliverersManager.FACTORY.getLogInfo(element) + " for panes: " + panes.size());
		}
		for (MessagesPane pane : panes){
			if (logger.isDebugEnabled()){
				logger.debug("Processing next ListDeliveringElement for " + pane.getSystemName());
			}
			
			Hashtable allMessages = element.getAllMessages();
		
	    	InsertStatementListOperation doInsert = new InsertStatementListOperation( pane, allMessages, true, element.getHighligth() );
	    	final Lock lock = new ReentrantLock();
	    	final Condition teardowned = lock.newCondition();
	    	doInsert.addProgressListener(new InsertListOperationProgressAdaptor(){

				public void teardowned(OperationProgressEvent e) {
					lock.lock();
					try {
						teardowned.signalAll();
					}
					finally {
						lock.unlock();
					}
				}
	    		
	    	});
	    	doInsert.start();
	    	lock.lock();
	    	try {
				teardowned.await();
				if (logger.isDebugEnabled()){
					logger.debug("InsertStatementListOperation finished");
				}
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
			finally{
				lock.unlock();
			}
		}
	}
	
	static private void process(SimpleDeliveringElement element, List<MessagesPane> panes){
		if (logger.isDebugEnabled()){
			logger.debug("Processing next SimpleDeliveringElement " + DeliverersManager.FACTORY.getLogInfo(element));
		}
		for (MessagesPane pane : panes){
			pane.performInsert(element.getDoc(), element.getTypeOfUpdate(), element.isInsertToSelectedOnly(), 
					element.isOpenTreeToMessageId(), element.getSphereId());
		}
	}
		
	static private void process(ReplaceDeliveringElement element, List<MessagesPane> panes){
		for (MessagesPane pane : panes){
			if (element.getOldDoc() != null) {
				if (logger.isDebugEnabled()){
					logger.debug("Processing Replace Doc with next ReplaceDeliveringElement " + DeliverersManager.FACTORY.getLogInfo(element));
				}
				pane.replaceDocWith(element.getOldDoc(), element.getNewDoc());
			} else {
				if (logger.isDebugEnabled()){
					logger.debug("Processing Remove then insert next ReplaceDeliveringElement " + DeliverersManager.FACTORY.getLogInfo(element));
				}
				pane.removeThenInsert(element.getNewDoc(), true);
			}
		}
	}
}
