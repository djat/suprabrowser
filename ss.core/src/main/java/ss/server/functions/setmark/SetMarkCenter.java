/**
 * 
 */
package ss.server.functions.setmark;

import ss.common.ReflectionUtils;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SetMarkCenter {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetMarkCenter.class);
	
	public static final SetMarkCenter INSTANCE = new SetMarkCenter();

	private final MarkProceduresProvider registrator;
	
	private SetMarkCenter(){
		this.registrator = new MarkProceduresProvider();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void process( final SetMarkData data, final DialogsMainPeer peer ){
		if ( data == null ) {
			logger.error( "data is null" );
			return;
		}
		final Class<? extends SetMarkProcedure<? extends SetMarkData>> storedClazz = 
			this.registrator.get( data.getClass() );
		if ( storedClazz == null ) {
			logger.error("No procedure for data class : " + data.getClass().getName());
			return;
		}
		try {
			final SetMarkProcedure<? extends SetMarkData> procedure = ReflectionUtils.create(storedClazz, data, peer);
			if ( procedure == null ) {
				logger.error("Can not create procedure");
				return;
			}
			procedure.process();
		} catch (Throwable ex) {
			logger.error("Error creating procedure",ex);
		}
	}
}
