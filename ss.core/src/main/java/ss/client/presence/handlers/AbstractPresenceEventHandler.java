package ss.client.presence.handlers;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.peoplelist.IPeopleList;
import ss.common.ArgumentNullPointerException;
import ss.common.UiUtils;
import ss.common.presence.AbstractPresenceEvent;
import ss.framework.exceptions.ObjectIsNotInitializedException;
import ss.framework.networking2.EventHandler;
import ss.framework.networking2.EventHandlingContext;

public abstract class AbstractPresenceEventHandler<E extends AbstractPresenceEvent>
		extends EventHandler<E> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractPresenceEventHandler.class);
	
//	private SupraSphereFrame supraSphereFrame;

	/**
	 * @param name
	 */
	public AbstractPresenceEventHandler(Class<E> eventClass) {
		super(eventClass);
	}

	/**
	 * @param supraSphereFrame the supraSphereFrame to set
	 */
	public void initialize(SupraSphereFrame supraSphereFrame) {
//		if ( supraSphereFrame == null ) {
//			throw new ArgumentNullPointerException("supraSphereFrame");
//		}
//		this.supraSphereFrame = supraSphereFrame;
	}


	/**
	 * @param sphereId
	 * @return
	 */
	public IPeopleList getPeopleTable(final String sphereId) {
//		if ( this.supraSphereFrame == null ) {
//			throw new ObjectIsNotInitializedException( this );
//		}
		MessagesPane messagesPane = SupraSphereFrame.INSTANCE.getMessagesPaneFromSphereId(sphereId);
		return messagesPane != null ? messagesPane.getPeopleTable() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.networking2.EventHandler#handleEvent(ss.framework.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<E> context) {
		final E event = context.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug( "Received event " + event );
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				swtHandleEvent(event);
			}
		});

	}

	protected abstract void swtHandleEvent(E event);

}
