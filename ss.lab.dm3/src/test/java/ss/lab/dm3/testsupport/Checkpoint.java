package ss.lab.dm3.testsupport;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

public class Checkpoint {

	private final AtomicBoolean passed = new AtomicBoolean( false );
	
	public synchronized void pass() {
		this.passed.set( true );	
		notifyAll();
	}
	
	public synchronized void waitToPass() {
		if ( isPassed() ) {
			return;
		}
		try {
			wait( 10000 );			
		} catch (InterruptedException ex) {
		}
		if ( !isPassed() ) {
			Assert.fail( "Checkpoint failed " + this );
		}
	}
	
	/**
	 * @return
	 */
	private boolean isPassed() {
		return this.passed.get();
	} 
	
}
