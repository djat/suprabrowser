/**
 * 
 */
package ss.smtp.sender;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Transport;

import ss.common.PathUtils;
import ss.common.ThreadUtils;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.SendingElement.SendingElementMode;
import ss.smtp.sender.resendcontrol.PostpondedMailProvider;
import ss.smtp.sender.resendcontrol.PostpondedElement.PostpondedElementType;

/**
 * @author zobo
 *
 */
public class Mailer2 {
	/**
	 * @author zobo
	 *
	 */
	private final class MailerSenderThread2 extends Thread {
		
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
						if ((sendingElement.getMode() == SendingElementMode.FORWARDED)||
								(sendingElement.getMode() == SendingElementMode.CREATED)){
							Responcer.INSTANCE.responceSuccessfull(sendingElement);
						} else if (sendingElement.getMode() == SendingElementMode.POSTPONED){
							PostpondedMailProvider.INSTANCE.sent(sendingElement);
						}
					} catch (Exception e){
						if (logger.isDebugEnabled()){
							logger.error(e);
						}
						if (sendingElement.getMode() == SendingElementMode.FORWARDED){
							Responcer.INSTANCE.responceFailed(sendingElement);
							PostpondedMailProvider.INSTANCE.postpone(sendingElement, PostpondedElementType.SHORT);
						}
						if (sendingElement.getMode() == SendingElementMode.CREATED){
							Responcer.INSTANCE.responceFailed(sendingElement);
							PostpondedMailProvider.INSTANCE.postpone(sendingElement, PostpondedElementType.LONG);
						}
					}
				} catch (Throwable ex) {
					logger.error(ex);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Mailer.class);
	
	public static final Mailer2 INSTANCE = new Mailer2();

	private static final boolean DEBUG_MODE = false;

	private final SendingLine line;
	
	private MailerSenderThread2 mailer;
	
	private Thread mailerController;

	private static int fileN = 0;
	
	private volatile long deliverTimeStarted = Long.MAX_VALUE;
	
	private static final long ALLOWED_TIMEOUT = 3*60*1000;
	
	private Mailer2() {
		this.line = new SendingLine();
		createMailerControllerThread();
		createMailerThread();
	}
	
	private void restartMailer(){
		this.mailer.interrupt();
		this.mailer.setAlive(false);
		createMailerThread();
	}
	
	private void startDelivering(){
		if (logger.isDebugEnabled()) {
			logger.debug("start Delivering");
		}
		this.deliverTimeStarted = System.currentTimeMillis();
	}
	
	private synchronized void finishedDelivering(){
		if (logger.isDebugEnabled()) {
			logger.debug("finished Delivering");
		}
		this.deliverTimeStarted = Long.MAX_VALUE;
	}
	
	private void createMailerControllerThread(){
		this.mailerController = new Thread(){
			@Override
			public void run() {
				while (true) {
					try {
						sleep(500);
						if ((System.currentTimeMillis() - Mailer2.this.deliverTimeStarted) > ALLOWED_TIMEOUT){
							logger.error("mailer hanged, restarting mailer");
							finishedDelivering();
							restartMailer();
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("mailer not hanged");
							}
						}
					} catch (InterruptedException ex) {
						logger.error( "TODO error message",ex);
					}
				}
			}
		};
		this.mailerController.start();
	}
	
	private void createMailerThread(){
		this.mailer = new MailerSenderThread2();
		ThreadUtils.startDemon( this.mailer, "Email sender" );
	}
	
	private SendingElement getNextElement() {
		return this.line.take();
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
		final List<String> mxHosts = element.getMXHosts();
		if ((mxHosts == null)||(mxHosts.isEmpty())){
			throw new Exception("No MX hosts");
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Delivering to mxHosts... (size: " + mxHosts.size() + "):");
				for ( String h : mxHosts ) {
					logger.debug("mxHost : " + h);
				}
			}
			deliverImplOneMXHost(element, mxHosts.get(0));
		} catch (Throwable ex){
			if (logger.isDebugEnabled()) {
				logger.error("Error with mxHost " + mxHosts.get(0) + ", Trying to send to enother mx host", ex);
			}
			List<String> mxHostsOther = new ArrayList<String>(mxHosts);
			mxHostsOther.remove(0);
			for (String host : mxHostsOther) {
				try {
					SendingElement nextElement = SendingElementFactory.recreate(element, host);
					deliverImplOneMXHost(nextElement, host);
					return;
				} catch (Throwable ex1){
					if (logger.isDebugEnabled()) {
						logger.error("Error with mxHost " + host + ", Trying to send to enother mx host", ex1);
					}
				}
			}
			throw new Exception("Could not send to any of mxHosts");
		}
	}
	
	private void deliverImplOneMXHost( final SendingElement element, final String mxHost ) throws Exception {
		startDelivering();
		if (logger.isDebugEnabled()) {
			logger.debug("Connecting to host : " + mxHost);
		}
		final Transport transport = element.getSendsession().getTransport("smtp");
		transport.connect(mxHost, 25, "", "");
		if (logger.isDebugEnabled()){
			logger.debug("Sending message by " + element.getSendList());
		}
		transport.sendMessage(element.getSmessage(), element.getSendList().getAddresses());
		transport.close();
		finishedDelivering();
		if (logger.isDebugEnabled()){
			logger.debug("Sent to " + mxHost);
		}		
	}
	
	public void stop(){
		this.mailer.interrupt();
		this.mailerController.interrupt();
	}
}
