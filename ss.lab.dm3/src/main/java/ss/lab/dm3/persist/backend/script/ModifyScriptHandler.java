package ss.lab.dm3.persist.backend.script;

import ss.lab.dm3.persist.backend.DataManagerBackEnd;
import ss.lab.dm3.persist.script.ModifyScript;
import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;

public abstract class ModifyScriptHandler<T extends ModifyScript> extends ScriptHandler<T> {

	private DataManagerBackEnd dataManager;

	/**
	 * @param scriptClass
	 */
	public ModifyScriptHandler(Class<T> scriptClass) {
		super(scriptClass);
	}

	public DataManagerBackEnd getDataManager() {
		return this.dataManager;
	}

	public void setDataManager(DataManagerBackEnd dataManager) {
		this.dataManager = dataManager;
	}

	public abstract void handle(Authentication authentication, ModifyScript script);

	/**
	 * 
	 */
	public ISecurityManagerBackEnd getSucurityManager() {
		throw new UnsupportedOperationException( "Method not yet implemented" );
	}
}
