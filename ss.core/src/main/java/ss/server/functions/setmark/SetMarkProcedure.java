/**
 * 
 */
package ss.server.functions.setmark;

import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public abstract class SetMarkProcedure<T extends SetMarkData> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetMarkProcedure.class);
	
	private final T data;
	
	private final DialogsMainPeer peer;

	public SetMarkProcedure(final T data, final DialogsMainPeer peer){
		if ( peer == null ) {
			throw new NullPointerException("DialogsMainPeer can not be null");
		}
		if ( data == null ) {
			throw new NullPointerException("SetGlobalMarkData can not be null");
		}
		this.data = data;
		this.peer = peer;
	}
	
	public void process(){
		try {
			proccesImpl();
		} catch (Throwable ex) {
			logger.error( "Processing has failed", ex );
		}
	}
	
	protected abstract void proccesImpl();

	protected T getData() {
		return this.data;
	}

	protected DialogsMainPeer getPeer() {
		return this.peer;
	} 
}
