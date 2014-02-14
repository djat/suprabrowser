/**
 * 
 */
package ss.smtp.sender.resendcontrol;

import java.util.List;

import ss.common.ThreadUtils;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.resendcontrol.PostpondedElement.PostpondedElementType;

/**
 * @author zobo
 *
 */
public class PostpondedMailProvider {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PostpondedMailProvider.class);
	
	public static final PostpondedMailProvider INSTANCE = new PostpondedMailProvider();
	
	private final Thread provider;
	
	private static final int SLEEP_TIME = 20*1000;
	
	private boolean alive = true; 
	
	private final PostpondedList list;
	
	private PostpondedMailProvider(){
		this.list = new PostpondedList();
		this.provider = new Thread(){
			@Override
			public void run() {
				while (PostpondedMailProvider.this.alive){
					try {
						sleep(SLEEP_TIME);
						provide();
					} catch (InterruptedException ex) {
						logger.error(ex);
					}
				}
			}
		};
		ThreadUtils.startDemon(this.provider, "Postponeded emails provider");
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private void provide() {
		synchronized (this.list) {
			if (logger.isDebugEnabled()){
				logger.debug("Time to look for postponed emails");
			}
			List<PostpondedElement> elements = this.list.get();
			if (elements.isEmpty()){
				return;
			}	
			for (PostpondedElement element : elements){
				Mailer.INSTANCE.send(element.getElement());
			}
		}
	}
	
	public void postpone(SendingElement sendingElement, PostpondedElementType type){
		synchronized (this.list) {
			this.list.put(new PostpondedElement(sendingElement, type));
		}
	}
	
	public void elapsed(PostpondedElement element) {
		if (logger.isDebugEnabled()){
			logger.debug("Elapsed element with messageId: " + element.getElement().getMessageId());
		}
		Responcer.INSTANCE.postponedResponceFailed(element);
	}
	
	public void sent(SendingElement sendingElement){
		synchronized (this.list) {
			Responcer.INSTANCE.postponedResponceSuccessfull(this.list.remove(sendingElement));
		}
	}
	
	public List<PostpondedElement> getCurrentStateData(){
		List<PostpondedElement> elements;
		synchronized (this.list) {
			 elements = this.list.getAllInfo();
		}
		return elements;
	}
}
