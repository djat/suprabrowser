package ss.framework.networking2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.EventListenerList;

import ss.common.ArgumentNullPointerException;
import ss.common.CantRestoreObjectFromByteArrayException;
import ss.common.ThreadUtils;
import ss.common.operations.AbstractLoopOperation;
import ss.common.operations.OperationBreakException;
import ss.common.operations.OperationProgressEvent;
import ss.framework.networking2.executors.SingleThreadHandlerExecutor;
import ss.framework.networking2.io.Packet;
import ss.framework.networking2.io.PacketInputStream;
import ss.framework.networking2.io.PacketLoadingListener;
import ss.framework.networking2.io.PacketSendingQueue;
import ss.framework.networking2.io.PacketSendingQueueEvent;
import ss.framework.networking2.io.PacketSendingQueueListener;
import ss.framework.networking2.keepalive.KeepAlivePingHandler;
import ss.framework.networking2.keepalive.KeepAlivePingSender;
import ss.framework.networking2.properties.ProtocolDisplayName;
import ss.framework.networking2.properties.ProtocolProperties;

public class Protocol {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Protocol.class);

	private static final int CLOSE_GUARD_TIMEOUT = 60000; // 1 min

	private final PacketSendingQueue sendingQueue;

	private final ReceivingLoop receivingLoop;

	private final EventListenerList listeners = new EventListenerList();

	private final AtomicBoolean disposed = new AtomicBoolean(false);

	private final AtomicBoolean closeBegun = new AtomicBoolean(false);

	private final AtomicBoolean closeCommandSended = new AtomicBoolean(false);

	private final AtomicBoolean closeCommandReceived = new AtomicBoolean(false);

	private boolean started = false;

	private final ActiveMessageHandlingManager activeMessageHandlerManager = new ActiveMessageHandlingManager(
			this);

	private final ReplyHandlingManager replyHandlingManager = new ReplyHandlingManager();

	private final ReplyHandler defaultResultHandler = new ReplyHandler();

	private final SingleThreadHandlerExecutor defaultActiveExecutor = new SingleThreadHandlerExecutor(
			"-ActiveExecutor");

	private final SingleThreadHandlerExecutor defaultReplyExecutor = new SingleThreadHandlerExecutor(
			"-RepliesExecutor");

	private final ProtocolProperties properties;

	private ProtocolManager manager = null;

	private final KeepAlivePingSender keepAlivePingSender;

	public Protocol(DataInputStream datain, DataOutputStream dataout,
			String displayName) {
		this(datain, dataout, new ProtocolProperties(displayName));
	}

	public Protocol(DataInputStream datain, DataOutputStream dataout,
			ProtocolProperties protocolProperties) {
		this.properties = protocolProperties;
		this.receivingLoop = new ReceivingLoop(datain);
		this.sendingQueue = new PacketSendingQueue(dataout,
				new PacketSendingQueueListener() {

					public void beforePacketSend(PacketSendingQueueEvent e) {
						Message message = (Message) e.getPacket().getData();
						beforeMessageSend(message);
					}

					public void afterPacketSend(PacketSendingQueueEvent e) {
						Message message = (Message) e.getPacket().getData();
						afterMessageSend(message);
					}

					public void queueTeardowned() {
						Protocol.this.afterCloseEventSent();
						logger.info("PacketSendingQueue teardowned"
								+ Protocol.this);
					}
				}, getDisplayName() + "-out");
		this.registerHandler(new CloseProtocolEventHanlder());
		this.registerHandler(new KeepAlivePingHandler());
		this.keepAlivePingSender = new KeepAlivePingSender(this);
	}

	public void addPacketLoadingListener(PacketLoadingListener listener) {
		this.receivingLoop.packetIn.addPacketLoadingListener(listener);
	}

	public void removePacketLoadingListener(PacketLoadingListener listener) {
		this.receivingLoop.packetIn.removePacketLoadingListener(listener);
	}

	/**
	 * @param message
	 */
	private void processMessage(Message message) throws OperationBreakException {
		if (message instanceof ActiveMessage) {
			this.activeMessageHandlerManager
					.dispatchMessage((ActiveMessage) message);
		} else if (message instanceof Reply) {
			this.replyHandlingManager.dispatchReply((Reply) message);
		} else {
			// Should never happens.
			// TODO:#think about reaction
			logger.error("Invalid command class " + message);
		}
	}

	/**
	 * 
	 */
	protected void onBeginClose(ProtocolLifetimeEvent e) {
		for (ProtocolLifetimeListener listener : getProtocolLifetimeListeners()) {
			listener.beginClose(e);
		}
	}

	/**
	 * 
	 */
	protected void onStarted(ProtocolLifetimeEvent e) {
		logger.info("protocol setupped " + this);
		if (getManager() != null) {
			getManager().protocolStarted(this);
		}
		for (ProtocolLifetimeListener listener : getProtocolLifetimeListeners()) {
			listener.started(e);
		}
	}

	/**
	 * 
	 */
	public final void beginClose() {
		if (isDisposed()) {
			return;
		}
		if (this.closeBegun.compareAndSet(false, true)) {
			logger.warn("close began " + this);
			this.keepAlivePingSender.stop();
			onBeginClose(new ProtocolLifetimeEvent(Protocol.this));
			if (this.sendingQueue.isAlive()) {
				new CloseProtocolEvent().fireAndForget(this);
			} else {
				logger.warn("Sending queue die, can not send close event "
						+ this);
				afterCloseEventSent();
			}
			final ProtocolManager currentManager = getManager();
			if (currentManager != null) {
				currentManager.protocolBeginClose(this);
			} else {
				logger.error("Manager is null for " + this);
			}
			ThreadUtils.startDemon(new ProtocolCloseGuard(),
					ProtocolCloseGuard.class);
		}
	}

	/**
	 * 
	 */
	private final void disposeIfReadyToDispose() {
		if (this.isDisposed()) {
			return;
		}
		beginClose();
		if (this.closeCommandReceived.get() && this.closeCommandSended.get()) {
			if (this.disposed.compareAndSet(false, true)) {
				// Sending and Receiving parts die, so dispose protocol
				logger.warn("disposing in/out for " + this);
				this.receivingLoop.dispose();
				this.sendingQueue.dispose();
				this.replyHandlingManager.dispose();
				this.defaultReplyExecutor.shootdown();
				this.defaultActiveExecutor.shootdown();
				logger.info("disposed");
			}
		}
	}

	/**
	 * Begin message executing.
	 * 
	 */
	final void beginExecute(MessageSendingContext messageSendingContext) {
		if (messageSendingContext == null) {
			throw new ArgumentNullPointerException("commandSendBundle");
		}
		messageSendingContext.prepareToSend();
		if (messageSendingContext instanceof CommandSendingContext) {
			addCommandSendingContext((CommandSendingContext) messageSendingContext);
		} else if (messageSendingContext instanceof FireAndForgetMessageSendingContext) {
			addFireAndForgetMessage(messageSendingContext.getMessageToSend());
		} else {
			throw new IllegalArgumentException("messageSendingContext");
		}
	}

	private final void addFireAndForgetMessage(Message message) {
		try {
			if (!addMessageToSendingQueue(message)) {
				// Because it is fire and forget message display warn only
				logger.warn("Fire and forget message rejected " + message
						+ ". Protocol is valid " + this.isValid());
			}
		} catch (InterruptedException ex) {
			// Because it is fire and forget message display warning only
			logger.warn("Fire and forget message rejected " + message
					+ ". Protocol is valid " + this.isValid(), ex);
		}
	}

	private final void addCommandSendingContext(
			CommandSendingContext commandSendingContext) {
		Message message = commandSendingContext.getMessageToSend();
		if (canSendMessage(message)) {
			this.replyHandlingManager.registryReceiver(commandSendingContext);
			try {
				if (!addMessageToSendingQueue(message)) {
					this.replyHandlingManager
							.notifyCannotSendCommand(commandSendingContext);
				}
			} catch (InterruptedException ex) {
				this.replyHandlingManager.notifyCannotSendCommand(
						commandSendingContext, ex);
			}
		} else {
			commandSendingContext.cannotSendInitiationCommand();
		}
	}

	private final boolean addMessageToSendingQueue(Message message)
			throws InterruptedException {
		if (canSendMessage(message)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Offer message " + message + " queue size "
						+ this.sendingQueue.size());
			}
			Packet packet;
			try {
				packet = new Packet(message);
			} catch (IOException ex) {
				throw new RuntimeException("Can't create packet to send.", ex );
			}
			this.sendingQueue.put(packet);
			return true;
		} else {
			return false;
		}
	}

	private boolean canSendMessage(Message message) {
		if (message instanceof CloseProtocolEvent) {
			return !isDisposed() && this.areAllPartsAlive();
		} else {
			return isValid();
		}
	}

	final String formatExceptionMessage(IMessageHandler handler, Throwable ex) {
		return "Command handler " + handler + " in protocol " + this
				+ " throws exeption " + ex;
	}

	/**
	 * Registry active message handler
	 * 
	 * @param handler
	 *            handler
	 */
	public final void registerHandler(ActiveMessageHandler handler) {
		registerHandler(handler, getDefaultActiveExecutor());
	}

	/**
	 * Registry active message handler
	 * 
	 * @param handler
	 *            handler
	 */
	public final void registerHandler(ActiveMessageHandler handler,
			IHandlerExecutor handlerExecutor) {
		if (hasAliveParts()) {
			throw new IllegalStateException("Protocol already running");
		}
		this.activeMessageHandlerManager.addHandler(handler, handlerExecutor);
	}

	/**
	 * @return true is sendig or receving queue is running
	 */
	private boolean hasAliveParts() {
		return this.receivingLoop.isAlive() || this.sendingQueue.isAlive();
	}

	private boolean areAllPartsAlive() {
		return this.receivingLoop.isAlive() && this.sendingQueue.isAlive();
	}

	/**
	 * @return the protocol manager
	 */
	public final ProtocolManager getManager() {
		return this.manager;
	}

	/**
	 * @return true is sendig or receving queue is running
	 */
	public final boolean isValid() {
		return this.areAllPartsAlive() && !this.isCloseBegun() && !isDisposed();
	}

	/**
	 * 
	 */
	public final ProtocolProperties getProperties() {
		return this.properties;
	}

	/**
	 * Start protocol
	 */
	public final void start(ProtocolManager manager)
			throws IllegalStateException {
		synchronized (this) {
			if (!canStart()) {
				logger.error("Cannot start protocol " + this);
				throw new IllegalStateException("Cannot start protocol " + this);
			}
			this.started = true;
		}
		if (manager == null) {
			throw new ArgumentNullPointerException("manager");
		}
		this.manager = manager;
		logger.warn("Starting protocol " + this);
		this.defaultReplyExecutor.start(getDisplayName());
		this.defaultActiveExecutor.start(getDisplayName());
		this.replyHandlingManager.start(getDisplayName());
		this.sendingQueue.start();
		this.receivingLoop.start();
		this.keepAlivePingSender.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.operations.IOperation#addProgressListener(ss.common.operations.OperationProgressListener)
	 */
	public final void addProtocolListener(ProtocolLifetimeListener listener) {
		this.listeners.add(ProtocolLifetimeListener.class, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.operations.IOperation#removeProgressListener(ss.common.operations.OperationProgressListener)
	 */
	public final void removeProtocolListener(ProtocolLifetimeListener listener) {
		this.listeners.remove(ProtocolLifetimeListener.class, listener);
	}

	/**
	 * Returns list of progress listener
	 * 
	 * @return
	 */
	protected final ProtocolLifetimeListener[] getProtocolLifetimeListeners() {
		return this.listeners.getListeners(ProtocolLifetimeListener.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.getProperties() + "]";
	}

	private void afterMessageSend(Message message) {
		if (message instanceof CloseProtocolEvent) {
			afterCloseEventSent();
		}
	}
	
	/**
	 * @param message
	 */
	private void beforeMessageSend(Message message) {
		if ( message instanceof Command ) {
			this.replyHandlingManager.afterCommandWasSent((Command)message);
		}
	}

	/**
	 * 
	 */
	private void afterCloseEventSent() {
		if (this.closeCommandSended.compareAndSet(false, true)) {
			logger.warn("Protocol close command sended " + this);
			disposeIfReadyToDispose();
		}
	}

	/**
	 * @param message
	 */
	private void afterCloseEventReceived() {
		if (this.closeCommandReceived.compareAndSet(false, true)) {
			logger.warn("Protocol close command received " + this);
			disposeIfReadyToDispose();
		}
	}

	/**
	 * @return the closed
	 */
	private final boolean isCloseBegun() {
		return this.closeBegun.get();
	}

	/**
	 * @return
	 */
	public boolean canStart() {
		return !isCloseBegun() && !isDisposed() && !hasAliveParts()
				&& !this.started;
	}

	/**
	 * @return the disposed
	 */
	public boolean isDisposed() {
		return this.disposed.get();
	}

	protected final void executeMessageForSelf(final Message message) {
		if (message == null) {
			throw new ArgumentNullPointerException("message");
		}
		ThreadUtils.startDemon(new RunnableSelfMessage(message),
				RunnableSelfMessage.class);
	}

	/**
	 * @return default result handler
	 */
	@SuppressWarnings("unchecked")
	public final ReplyHandler getDefaultResultHandler() {
		return this.defaultResultHandler;
	}

	/**
	 * @return
	 */
	protected final IHandlerExecutor getDefaultActiveExecutor() {
		return this.defaultActiveExecutor;
	}

	/**
	 * @return
	 */
	final IHandlerExecutor getDefaultReplyExecutor() {
		return this.defaultReplyExecutor;
	}

	/**
	 * @param string
	 */
	final void handlerNotFound(Message message, String cause) {
		final String warningStr = "Cannot find handler to " + message
				+ ". Cause: " + cause;
		if (message instanceof Command) {
			CommandHandlingContext<Command> replyWorningContext = new CommandHandlingContext<Command>(
					this, (Command) message);
			replyWorningContext.reply(new FailedReply(warningStr));
		}
		logger.warn(warningStr);
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.properties.requireProperty(ProtocolDisplayName.class)
				.getValue();
	}

	private final class ReceivingLoop extends AbstractLoopOperation {

		private final PacketInputStream packetIn;

		/**
		 * @param sleepTime
		 * @param packetIn
		 */
		public ReceivingLoop(final DataInputStream datain) {
			this.packetIn = new PacketInputStream(datain);
			this.setDisplayName(Protocol.this.getDisplayName() + "-in");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.operations.AbstractOperation#onSetup(ss.common.operations.OperationProgressEvent)
		 */
		@Override
		protected void onSetup(OperationProgressEvent e) {
			super.onSetup(e);
			Protocol.this.onStarted(new ProtocolLifetimeEvent(Protocol.this));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.operations.AbstractOperation#onTeardown(ss.common.operations.OperationProgressEvent)
		 */
		@Override
		protected void onTeardown(OperationProgressEvent e) {
			super.onTeardown(e);
			dispose();
			Protocol.this.afterCloseEventReceived();
			logger.info("ReceivingLoop teardowned " + this);
		}

		/**
		 * Read message from packetIn stream
		 * 
		 * @return readed packet
		 * @throws OperationBreakException
		 */
		@SuppressWarnings("unchecked")
		private Message readMessage() throws OperationBreakException {
			final Packet packet;
			try {
				packet = this.packetIn.read();
			} catch (EOFException e) {
				logger.info("Protocol datain is EOF " + this);
				Protocol.this.afterCloseEventReceived();
				throw new OperationBreakException(this, e);
			} catch (IOException e) {
				if (!Protocol.this.isDisposed()) {
					logger.error("Cannot read command " + this, e);
				}
				throw new OperationBreakException(this, e);
			} catch (CantRestoreObjectFromByteArrayException ex) {
				logger.error("Broken packet", ex);
				return null;
			}
			return (Message) packet.getData();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.operations.AbstractLoopOperation#performLoopAction()
		 */
		@Override
		protected final void performLoopAction() throws OperationBreakException {
			final Message message = readMessage();
			if (message != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Protocol received command " + message);
				}
				processMessage(message);
			} else {
				logger.error("Message is null");
			}
		}

		public final void dispose() {
			queryBreak();
			try {
				this.packetIn.close();
			} catch (IOException ex) {
				logger.error("Cannot close input stream ", ex);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Receiving loop " + Protocol.this.toString();
		}

	}

	/**
	 * 
	 */
	private static class CloseProtocolEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3015802296234911L;

	};

	/**
	 * 
	 */
	private class CloseProtocolEventHanlder extends
			EventHandler<CloseProtocolEvent> {

		/**
		 * @param commandClass
		 */
		public CloseProtocolEventHanlder() {
			super(CloseProtocolEvent.class);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.networking2.EventHandler#handleEvent(ss.common.networking2.EventHandlingContext)
		 */
		@Override
		protected void handleEvent(
				EventHandlingContext<CloseProtocolEvent> context) {
			afterCloseEventReceived();
		}

	}

	private final class ProtocolCloseGuard implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				Thread.sleep(CLOSE_GUARD_TIMEOUT);
			} catch (InterruptedException ex) {
				logger.info("ProtocolDisposer interrupted");
			}
			if (!Protocol.this.isDisposed()) {
				try {
					afterCloseEventSent();
				} catch (RuntimeException ex) {
					logger.warn("afterCloseCommandSended failed", ex);
				}
				try {
					afterCloseEventReceived();
				} catch (RuntimeException ex) {
					logger.warn("afterCloseCommandReceived failed", ex);
				}
			}
		}
	}

	private class RunnableSelfMessage implements Runnable {

		private final Message message;

		/**
		 * @param message
		 */
		public RunnableSelfMessage(final Message message) {
			super();
			this.message = message;
		}

		public void run() {
			try {
				Protocol.this.processMessage(this.message);
			} catch (OperationBreakException ex) {
				ss.common.ExceptionHandler.handleException(this, ex);
			}
		}
	}

}
