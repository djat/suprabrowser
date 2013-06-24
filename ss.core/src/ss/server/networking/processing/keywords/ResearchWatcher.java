/**
 * 
 */
package ss.server.networking.processing.keywords;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import ss.common.ListUtils;

/**
 * @author zobo
 *
 */
public class ResearchWatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ResearchWatcher.class);
	
	public static final ResearchWatcher INSTANCE = new ResearchWatcher();
	
	private final Hashtable<String, List<String>> spheres;
	
	private final Hashtable<String, Date> dates;
	
	private ResearchWatcher(){
		this.dates = new Hashtable<String, Date>(); 
		this.spheres = new Hashtable<String, List<String>>();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void reseached( final String loginName ){
		if ( loginName == null ) {
			return;
		}
		this.dates.put(loginName, new Date());
	}
	
	public Date getLastReseached( final String loginName ){
		if ( loginName == null ) {
			return null;
		}
		return this.dates.get( loginName );
	}

	/**
	 * @return
	 */
	public List<String> getLastOpenedSpheres( final String loginName ) {
		if ( loginName == null ) {
			return null;
		}
		synchronized (this.spheres) {
			final List<String> recorder = this.spheres.get( loginName );
			if (recorder == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Recoder is null, returning null");
				}
				return null;
			} else {
				final List<String> list = new ArrayList<String>( recorder );
				if (logger.isDebugEnabled()) {
					logger.debug("Returning sphere list: " + ListUtils.valuesToString( list ));
				}
				return list;
			}
		}
	}
	
	public void sphereOpened( final String loginName, final String sphereId ){
		if ( (loginName == null) || (sphereId == null) ) {
			logger.error("loginName or SphereId is null");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("sphereOpened for LoginName: " + loginName);
			logger.debug("sphereId: " + sphereId);
		}
		synchronized (this.spheres) {
			List<String> recorder = this.spheres.get( loginName );
			if ( recorder == null ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Was null recoder, setting...");
				}
				recorder = new ArrayList<String>();
				recorder.add( sphereId );
				this.spheres.put( loginName, recorder );
				return;
			} 
			recorder.remove( sphereId );
			recorder.add( 0, sphereId );
		}
	}
}
