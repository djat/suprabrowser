package ss.common.reflect;

public class MethodRunner {

	
	
	
	public Runnable createRunner( final Object component, final Object [] args ) {
		return new Runnable() {
			public void run() {	
				invoke( component, args );
			}			
		};
	}

	/**
	 * @param component
	 * @param args
	 */
	public void invoke( final Object component, final Object[] args) {
		//TODO: implement
	}

	
}
