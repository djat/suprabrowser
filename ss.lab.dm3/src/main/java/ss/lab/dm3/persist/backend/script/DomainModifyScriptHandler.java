package ss.lab.dm3.persist.backend.script;

import ss.lab.dm3.persist.script.ModifyScript;
import ss.lab.dm3.security2.Authentication;

public class DomainModifyScriptHandler<T extends ModifyScript> extends ModifyScriptHandler<T> {

	/**
	 * @param scriptClass
	 */
	public DomainModifyScriptHandler(Class<T> scriptClass) {
		super(scriptClass);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.backend.script.ModifyScriptHandler#handle(ss.lab.dm3.security2.Authentication, ss.lab.dm3.persist.script.ModifyScript)
	 */
	@Override
	public void handle(Authentication authentication, ModifyScript script) {
		// TODO Auto-generated method stub

	}

}
