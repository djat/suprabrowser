/**
 * 
 */
package ss.smtp.sender.resendcontrol;

import java.util.ArrayList;
import java.util.List;

import ss.smtp.sender.SendingElement;

/**
 * @author zobo
 * 
 */
class PostpondedList {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PostpondedList.class);
	
	private final List<PostpondedElement> list = new ArrayList<PostpondedElement>();

	PostpondedList() {

	}

	public List<PostpondedElement> get() {
		synchronized (this.list) {

			long time = System.currentTimeMillis();
			List<PostpondedElement> returnlist = new ArrayList<PostpondedElement>();
			List<PostpondedElement> removelist = new ArrayList<PostpondedElement>();
			for (PostpondedElement element : this.list) {
				if (element.getNextTrialTime() < time) {
					PostpondedElement removedElement = operate(element);
					if (removedElement != null) {
						removelist.add(removedElement);
					} else {
						returnlist.add(element);
					}
				}
			}
			if (!removelist.isEmpty()){
				this.list.removeAll(removelist);
			}
			if (logger.isDebugEnabled()){
				logger.debug("Returning list of nedded to be sended elements, size: " + returnlist.size());
			}
			return returnlist;
		}
	}

	/**
	 * @param element
	 * @return
	 */
	private PostpondedElement operate(PostpondedElement element) {
		if (element.isContinue()) {
			if (logger.isDebugEnabled()){
				logger.debug("Resending element with messageId: " + element.getElement().getMessageId());
			}
			return null;
		} else {
			PostpondedMailProvider.INSTANCE.elapsed(element);
			return element;
		}
	}

	/**
	 * @param element
	 */
	public void put(PostpondedElement element) {
		synchronized (this.list) {
			if (logger.isDebugEnabled()){
				logger.debug("new postponed element with messageId: " + element.getElement().getMessageId());
			}
			this.list.add(element);
		}
	}

	public PostpondedElement remove(SendingElement el) {
		synchronized (this.list) {
			if (logger.isDebugEnabled()){
				logger.debug("Removing element with messageId: " + el.getMessageId());
			}
			PostpondedElement toremove = null;
			for (PostpondedElement post : this.list){
				if (post.getElement() == el){
					toremove = post;
					break;
				}
			}
			if (toremove != null){
				this.list.remove(toremove);
				if (logger.isDebugEnabled()){
					logger.debug("Element removed");
				}
			}
			return toremove;
		}
	}

	/**
	 * For Debug command specific function
	 */
	public List<PostpondedElement> getAllInfo() {
		synchronized (this.list) {
			List<PostpondedElement> toRet = new ArrayList<PostpondedElement>();
			toRet.addAll(this.list);
			return toRet;
		}
	}
}
