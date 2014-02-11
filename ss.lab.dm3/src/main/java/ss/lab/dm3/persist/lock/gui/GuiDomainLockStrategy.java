/**
 * 
 */
package ss.lab.dm3.persist.lock.gui;

import java.util.concurrent.atomic.AtomicReference;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainException;
import ss.lab.dm3.persist.DomainLockStrategy;

/**
 * @author Dmitry Goncharov
 */
public abstract class GuiDomainLockStrategy extends DomainLockStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.persist.DomainLockStrategy#install(ss.lab.dm3.persist.Domain)
	 */
	@Override
	public synchronized void install(final Domain domain) {
		super.install(domain);
		final AtomicReference<Throwable> exRef = new AtomicReference<Throwable>();
		executeFromNotDomainThread( new Runnable() {
			public void run() {
				try {
					domain.lockOrThrow();
				} catch (RuntimeException ex) {
					exRef.set(ex);
				}
			}
		});
		final Throwable lockFailedEx = exRef.get();
		if (lockFailedEx != null) {
			if (lockFailedEx instanceof RuntimeException) {
				throw ((RuntimeException) lockFailedEx);
			} else {
				throw new DomainException(
					"Can't lock domain to the display thread", lockFailedEx);
			}
		}
	}
	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.DomainLockStrategy#uninstall()
	 */
	@Override
	public Domain uninstall() {
		final Domain domain = super.uninstall();
		executeFromNotDomainThread( new Runnable() {
			public void run() {
				domain.unlock();
			}
		});
		return domain;
	}

}
