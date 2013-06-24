package ss.client.ui.sphereopen;

public class SphereLifetimeTracker {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereLifetimeTracker.class);

	private static final int WAIT_TIME = 60*1000;

	private String sphereId;
	
	private SphereState state = null;
	
	private long requestedTime;
	
	private int panesCount = 0;

	public SphereLifetimeTracker(String sphereId) {
		super();
		if ( sphereId == null ){
			throw new NullPointerException( "SphereId for Time Tracker is null" );
		}
		this.sphereId = sphereId;
		if (logger.isDebugEnabled()){
			logger.debug("SphereLifetimeTracker created for sphereId: " + sphereId);
		}
	}

	public String getSphereId() {
		return this.sphereId;
	}

	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	public SphereState getState() {
		return this.state;
	}

	public void setState(SphereState state) {
		if ((state == SphereState.OPENING)&&(this.state == SphereState.OPENING)){
			if (logger.isDebugEnabled()){
				logger.debug("Trying to set OPENING state for OPENING tracker, denied");
			}
			return;
		}
		this.state = state;
		if (this.state == SphereState.OPENING){
			this.requestedTime = System.currentTimeMillis();
			if (logger.isDebugEnabled()){
				logger.debug("Set OPENING state, requestedTime: " + this.requestedTime);
			}
		} else if (this.state == SphereState.OPENED){
			if (logger.isDebugEnabled()){
				logger.debug("Set OPENED state");
			}
			inc();
		}
	}
	
	public void inc(){
		this.panesCount++;
		if (logger.isDebugEnabled()){
			logger.debug("Increased number of panes, panes count: " + this.panesCount);
		}
	}
	
	public void dec(){
		this.panesCount--;
		if (logger.isDebugEnabled()){
			logger.debug("Decreased number of panes, panes count: " + this.panesCount);
		}
	}
	
	public boolean isClosed(){
		return (this.panesCount <= 0);
	}

	/**
	 * @return
	 */
	public boolean isTimeOut() {
		final long current = System.currentTimeMillis();
		if ((this.state == SphereState.OPENING) && (current >= (this.requestedTime + WAIT_TIME))){
			if (logger.isDebugEnabled()){
				logger.debug("Is time out performed, returning true");
			}
			return true;
		}
		if (logger.isDebugEnabled()){
			logger.debug("Is time out performed, returning false");
		}
		return false;
	}
}
