package ss.lab.dm3.testsupport;

import ss.lab.dm3.persist.backend.script.ReadScriptContext;
import ss.lab.dm3.persist.backend.script.ReadScriptHandler;
import ss.lab.dm3.persist.script.builtin.StartupLoaderScript;

public class StartupLoaderScriptHandler extends ReadScriptHandler<StartupLoaderScript>{

	public StartupLoaderScriptHandler() {
		super(StartupLoaderScript.class);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.backend.script.ReadScriptHandler#handle(ss.lab.dm3.persist.backend.script.ReadScriptContext)
	 */
	@Override
	public void handle(ReadScriptContext<StartupLoaderScript> context) {
		this.log.debug( "Handle Start loader." );
	}

}
