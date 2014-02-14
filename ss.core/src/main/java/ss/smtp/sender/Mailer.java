/**
 * 
 */
package ss.smtp.sender;

import java.io.File;
import java.io.FileOutputStream;

import ss.common.PathUtils;
import ss.common.ThreadUtils;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.MailerTasksContainer.MailerTasksContainerListener;
import ss.smtp.sender.SendingElement.SendingElementMode;
import ss.smtp.sender.SendingResultStatusLine.SendingElementSendStatus;
import ss.smtp.sender.resendcontrol.PostpondedMailProvider;
import ss.smtp.sender.resendcontrol.PostpondedElement.PostpondedElementType;

/**
 * @author zobo
 * 
 */
public class Mailer {

	private final class MailerSenderThread extends Thread {
		
		private volatile boolean alive = true;
		
		public void setAlive( final boolean alive ){
			this.alive = alive;
		}
		
		@Override
		public void run() {
			while (this.alive){
				try {
					final SendingElement sendingElement = getNextElement();
					try {
						deliver(sendingElement);
					} catch (Exception e){
						if (logger.isDebugEnabled()){
							logger.error(e);
						}
						elementFailed(sendingElement);
					}
				} catch (Throwable ex) {
					logger.error(ex);
				}
			}
		}
	}

	private final class MailerResultProcessThread extends Thread {
		
		private volatile boolean alive = true;
		
		public void setAlive( final boolean alive ){
			this.alive = alive;
		}
		@Override
		public void run() {
			while ( this.alive ) {
				final SendingElementSendStatus reply = getNextReply();
				if ( reply.isSent() ) {
					elementSent( reply.getElement() );
				} else {
					elementFailed( reply.getElement() );
				}
			}
		}

	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Mailer.class);
	
	public static final Mailer INSTANCE = new Mailer();

	private static final boolean DEBUG_MODE = false;

	private final SendingLine line;
	
	private final SendingResultStatusLine replyedLine;
	
	private MailerSenderThread mailer;
	
	private MailerResultProcessThread resultProcessor;
	
	private final MailerTasksContainer mailerTasksContainer;

	private static int fileN = 0;
	
	private Mailer() {
		this.line = new SendingLine();
		this.replyedLine = new SendingResultStatusLine();
		this.mailerTasksContainer = new MailerTasksContainer();
		this.mailerTasksContainer.addListener(new MailerTasksContainerListener(){
			public void elementFailed(SendingElement sendingElement) {
				Mailer.this.replyedLine.put(sendingElement, false);
			}

			public void elementSended(SendingElement sendingElement) {
				Mailer.this.replyedLine.put(sendingElement, true);
			}
		});
		this.mailer = new MailerSenderThread();
		ThreadUtils.startDemon( this.mailer, "Email sender" );
		this.resultProcessor = new MailerResultProcessThread();
		ThreadUtils.startDemon( this.resultProcessor, "Email sent status processor" );
	}

	private SendingElement getNextElement() {
		return this.line.take();
	}
	
	private SendingElementSendStatus getNextReply(){
		return this.replyedLine.take();
	}
	
	public void send(SendingElement element){
		this.line.put(element);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	private void deliver(SendingElement element) throws Exception {
		if (DEBUG_MODE) {
			deliverDebugMode(element);
		} else {
			deliverImpl(element);
		}
	}
	
	private void deliverDebugMode( final SendingElement element ) throws Exception{
		final File outFile = new File(PathUtils.combinePath(PathUtils
				.getBaseDir(), "test-mail_" + (++fileN)  + ".txt"));
		logger.warn("DEBUG_MODE: Write email to local file "
				+ outFile.getCanonicalPath());
		if (!outFile.exists()) {
			outFile.createNewFile();
		}
		final FileOutputStream out = new FileOutputStream(outFile);
		try {
			element.getSmessage().writeTo(out);
		} finally {
			out.close();
		}
	}
	
	private void deliverImpl( final SendingElement element ) throws Exception {
		this.mailerTasksContainer.addTask( element );
	}
	
	private void elementFailed( final SendingElement sendingElement ){
		if (sendingElement.getMode() == SendingElementMode.FORWARDED){
			Responcer.INSTANCE.responceFailed(sendingElement);
			PostpondedMailProvider.INSTANCE.postpone(sendingElement, PostpondedElementType.SHORT);
		}
		if (sendingElement.getMode() == SendingElementMode.CREATED){
			Responcer.INSTANCE.responceFailed(sendingElement);
			PostpondedMailProvider.INSTANCE.postpone(sendingElement, PostpondedElementType.LONG);
		}
	}
	
	private void elementSent( final SendingElement sendingElement ){
		if ((sendingElement.getMode() == SendingElementMode.FORWARDED)||
				(sendingElement.getMode() == SendingElementMode.CREATED)){
			Responcer.INSTANCE.responceSuccessfull(sendingElement);
		} else if (sendingElement.getMode() == SendingElementMode.POSTPONED){
			PostpondedMailProvider.INSTANCE.sent(sendingElement);
		}
	}
		
	public void stop(){
		this.mailer.interrupt();
		this.resultProcessor.interrupt();
	}
}