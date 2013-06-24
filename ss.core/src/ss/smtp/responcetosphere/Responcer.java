/**
 * 
 */
package ss.smtp.responcetosphere;

import ss.common.ThreadUtils;
import ss.server.SystemSpeaker;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.resendcontrol.PostpondedElement;

/**
 * @author zobo
 *
 */
public class Responcer {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Responcer.class);
	
	private final ResponceLine responceLine;
	
	private final Thread responcer;
	
	public static final Responcer INSTANCE = new Responcer();
	
	private Responcer(){
		this.responceLine = new ResponceLine();
		this.responcer = new Thread() {
			@Override
			public void run() {
				while (true) {
					responcePreform(getNextResponce());
				}
			}
		};
		ThreadUtils.startDemon(this.responcer, "Information responser of email sending");
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private ResponceElement getNextResponce() {
		return this.responceLine.take();
	}
	
	private void responcePreform(ResponceElement element) {
		try {
			ResponceStringInfo resp = ResponceElementFactory.createResponceString(element);
			if (resp != null){
				SystemSpeaker.speakRorwardingPerformed( resp.getType(), resp.getSubject(), resp.getBody(), element.getSphereId(), element.getMessageId() );
			} else {
				logger.warn("ResponceStringInfo is null");
			}
		} catch (Exception ex){
			logger.error(ex);
		}
	}

	public void responceSuccessfull(SendingElement sendingElement) {
		this.responceLine.addSuccessfull(sendingElement);
	}

	public void responceFailed(SendingElement sendingElement) {
		this.responceLine.addFailed(sendingElement);
	}
	
	public void initiateResponceElement(String messageId, String sphereId, int size){
		this.responceLine.initiate(messageId, sphereId, size);
	}
	
	public void postponedResponceSuccessfull(PostpondedElement element){
		if (element == null){
			logger.error("PostpondedElement is null");
			return;
		}
		ResponceElement respoce = new ResponceElement(element.getElement(), true);
		respoce.setFinalResponce(true);
		this.responceLine.put(respoce);	
	}
	
	public void postponedResponceFailed(PostpondedElement element) {
		if (element == null){
			logger.error("PostpondedElement is null");
			return;
		}
		ResponceElement respoce = new ResponceElement(element.getElement(), false);
		respoce.setFinalResponce(true);
		this.responceLine.put(respoce);		
	}
}
