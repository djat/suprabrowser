package ss.client.ui.sphereopen;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

class SphereStateRegistry {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereStateRegistry.class);

	private final Hashtable<String, SphereLifetimeTracker> spheres;

	private final List<String> pending;

	SphereStateRegistry() {
		this.spheres = new Hashtable<String, SphereLifetimeTracker>();
		this.pending = new ArrayList<String>();
	}

	SphereState getSphereState(String sphereId) {
		SphereLifetimeTracker tracker = getSphereTracker(sphereId);
		if (tracker == null) {
			return null;
		} else {
			return tracker.getState();
		}
	}

	boolean contains(String sphereId) {
		return this.spheres.containsKey(sphereId);
	}

	void setSphereState(final String sphereId, final SphereState state) {
		SphereLifetimeTracker tracker = getSphereTracker(sphereId);
		if (tracker == null) {
			tracker = createNewTracker(sphereId);
		}
		tracker.setState(state);
		synchronized (this.pending) {
			if (state == SphereState.OPENING) {
				this.pending.add(sphereId);
			} else if (state == SphereState.OPENED) {
				this.pending.remove(sphereId);
			}
		}
	}

	void close(String sphereId) {
		SphereLifetimeTracker tracker = getSphereTracker(sphereId);
		if (tracker == null) {
			return;
		} else {
			tracker.dec();
			clean(tracker);
		}
	}

	private SphereLifetimeTracker getSphereTracker(String sphereId) {
		return this.spheres.get(sphereId);
	}

	private SphereLifetimeTracker createNewTracker(String sphereId) {
		SphereLifetimeTracker tracker = new SphereLifetimeTracker(sphereId);
		this.spheres.put(sphereId, tracker);
		return tracker;
	}

	private void clean(SphereLifetimeTracker tracker) {
		if (tracker == null) {
			throw new NullPointerException("SphereLifetimeTracker is null");
		}
		if (tracker.isClosed()) {
			this.spheres.remove(tracker.getSphereId());
		}
	}

	void checkPending() {
		synchronized (this.pending) {
			if (this.pending.isEmpty()){
				return;
			}
			if (logger.isDebugEnabled()){
				logger.debug("checkPending performed, size in pending: " + this.pending.size());
			}
			List<String> toRemove = new ArrayList<String>();
			for (String sphereId : this.pending) {
				SphereLifetimeTracker tracker = getSphereTracker(sphereId);
				if (tracker == null) {
					toRemove.add(sphereId);
				} else {
					if (tracker.isTimeOut()) {
						tracker.dec();
						clean(tracker);
					}
				}
			}
			this.pending.removeAll(toRemove);
		}
	}
}
