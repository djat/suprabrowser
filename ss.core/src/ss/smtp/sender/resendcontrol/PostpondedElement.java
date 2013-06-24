/**
 * 
 */
package ss.smtp.sender.resendcontrol;

import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElement.SendingElementMode;

/**
 * @author zobo
 *
 */
public class PostpondedElement {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PostpondedElement.class);
	
	public enum PostpondedElementType {
		SHORT, LONG
	}
	
	private static final int SHORT_NUMBER = 10;
	
	private static final int LONG_NUMBER = 50;
	
	private static final long OFFSET[] = {60*1000,
		60*1000, 60*1000, 60*1000, 60*1000, 10*60*1000, 45*60*1000, 60*60*1000};

	private final SendingElement element;
	
	private final long initialTime;
	
	private long nextTrialTime;
	
	private int numberOfRetries;

	private final PostpondedElementType type;

	public PostpondedElement(final SendingElement element, final PostpondedElementType type) {
		super();
		this.element = element;
		this.element.setMode(SendingElementMode.POSTPONED);
		this.initialTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()){
			logger.debug("Initial time for element with messagesId: " + element.getMessageId() + ", is: " + this.initialTime);
		}
		this.nextTrialTime = this.initialTime;
		this.type = type;
		this.numberOfRetries = 0;
		culculateNextTime();
	}

	public SendingElement getElement() {
		return this.element;
	}

	public long getInitialTime() {
		return this.initialTime;
	}

	public boolean isContinue(){
		this.numberOfRetries++;
		if (this.type == PostpondedElementType.SHORT){
			if (this.numberOfRetries < SHORT_NUMBER){
				if (logger.isDebugEnabled()){
					logger.debug("Will continue sending SHORT element, tries: " + this.numberOfRetries + ", returning true");
				}
				culculateNextTime();
				return true;
			} else {
				if (logger.isDebugEnabled()){
					logger.debug("Number of tries to send elapsed for SHORT element, returning false");
				}
			}
		} else if (this.type == PostpondedElementType.LONG) {
			if (this.numberOfRetries < LONG_NUMBER){
				if (logger.isDebugEnabled()){
					logger.debug("Will continue sending LONG element, tries: " + this.numberOfRetries + ", returning true");
				}
				culculateNextTime();
				return true;
			} else {
				if (logger.isDebugEnabled()){
					logger.debug("Number of tries to send elapsed for LONG element, returning false");
				}
			}
		}
		return false;
	}

	private void culculateNextTime() {
		if (logger.isDebugEnabled()){
			logger.debug("Culculating next try to send time, previous was: " + this.nextTrialTime);
		}
		if (this.numberOfRetries < OFFSET.length){
			this.nextTrialTime += OFFSET[this.numberOfRetries];
		} else {
			this.nextTrialTime += OFFSET[OFFSET.length-1];
		}
		if (logger.isDebugEnabled()){
			logger.debug("Next try to send time: " + this.nextTrialTime);
		}
	}

	public long getNextTrialTime() {
		return this.nextTrialTime;
	}

	/**
	 * @return
	 */
	public int getNumberOfRetriesLeft() {
		int left;
		if (PostpondedElementType.LONG == this.type){
			left = LONG_NUMBER - this.numberOfRetries;
		} else {
			left = SHORT_NUMBER - this.numberOfRetries;
		}
		return left;
	}

	/**
	 * @return
	 */
	public int getNumberOfRetriesPerformed() {
		return this.numberOfRetries;
	}
}
