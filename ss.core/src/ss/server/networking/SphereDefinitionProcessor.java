package ss.server.networking;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.VerifyAuth;
import ss.common.threads.ObjectRefusedException;
import ss.common.threads.SingleTheradExecutor;

/**
 * 
 */
class SphereDefinitionProcessor  {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereDefinitionProcessor.class);
	
	private SingleTheradExecutor<AbstractSphereSender> executor;
	/**
	 * @param subName
	 */
	public SphereDefinitionProcessor() {
		this.executor = new SingleTheradExecutor<AbstractSphereSender>( "-SphereDefProcessor" );
	}

	/**
	 * @param peer
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	public void sendGroupSphere(DialogsMainPeer peer, Hashtable session, Document sphereDefinition, VerifyAuth verifyAuth, String openBackground) {
		GroupSphereSender sender = new GroupSphereSender( peer, session, sphereDefinition, verifyAuth, openBackground );
		beginExecute(sender);
	}

	/**
	 * @param peer
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	public void sendPrivateSphere(DialogsMainPeer peer, Hashtable session, Document sphereDefinition, VerifyAuth verifyAuth, String openBackground) {
		PrivateSphereSender sender = new PrivateSphereSender( peer, session, sphereDefinition, verifyAuth, openBackground );
		beginExecute(sender);
	}
		
	/**
	 * @param sender
	 */
	private void beginExecute(AbstractSphereSender sender) {
		try {
			logger.info("sender : "+sender);
			this.executor.beginExecute(sender);
		} catch (ObjectRefusedException ex) {
			logger.error( "SphereDefinition refused",  ex );
		}
	}
	/**
	 * 
	 * @see ss.common.threads.SingleTheradExecutor#shootdown()
	 */
	public void shootdown() {
		this.executor.shootdown();
	}

	/**
	 * @param baseName
	 * @see ss.common.threads.SingleTheradExecutor#start(java.lang.String)
	 */
	public void start(String baseName) {
		this.executor.start(baseName);
	}

}