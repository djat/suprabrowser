/**
 * 
 */
package ss.server.domain.service;

import java.lang.reflect.Field;
import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.VerifyAuth;
import ss.lab.dm3.utils.ReflectionHelper;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.refactor.supraspheredoc.old.AbstractSsDocFeature;
import ss.refactor.supraspheredoc.old.SsDocSupraSphereEditFacade;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;

/**
 * 
 */
@Refactoring(classify = SupraSphereRefactor.class)
public class SupraSphereProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(SupraSphereProvider.class);

	/**
	 * Singleton instance
	 */

	public final static SupraSphereProvider INSTANCE = new SupraSphereProvider();

	private final Utils utils = Utils.getUtils(new XMLDB());

	private final ImplementationResolver resolver = new ImplementationResolver();

	private SupraSphereProvider() {
		this.resolver.put(ss.server.domain.service.ICreateSphere.class,
			ss.refactor.supraspheredoc.old.CreateSphere.class);
		this.resolver.put(
			ss.server.domain.service.IEntitleContactForSphere.class,
			ss.refactor.supraspheredoc.old.EntitleContactForSphere.class);
		this.resolver.put(
			ss.server.domain.service.IEntitleCotactForOneSphere.class,
			ss.refactor.supraspheredoc.old.EntitleCotactForOneSphere.class);
		this.resolver.put(ss.server.domain.service.IRegisterMember.class,
			ss.refactor.supraspheredoc.old.RegisterMember.class);
		this.resolver.put(
			ss.server.domain.service.IReplaceUsernameInMembership.class,
			ss.refactor.supraspheredoc.old.ReplaceUsernameInMembership.class);
	}

	public static DialogsMainPeer getDialogMainPeer(
			ProtocolHandler protocolHandler) {
		if (protocolHandler != null) {
			Class<?> protocolClazz = protocolHandler.getClass();
			Field field = ReflectionHelper.findPropertyDeclaration(
				protocolClazz, "peer");
			if (field != null) {
				Object peerObj;
				try {
					field.setAccessible(true);
					peerObj = field.get(protocolHandler);
				} catch (Exception ex) {
					if (ex instanceof RuntimeException) {
						throw (RuntimeException) ex;
					} else {
						throw new RuntimeException(
							"Can't get field 'peer' from protocol "
									+ protocolHandler, ex);
					}
				}
				return (DialogsMainPeer) peerObj;
			}
		}
		return null;
	}

	public <T extends ISupraSphereFeature> T get(
			ProtocolHandler protocolHandler, Class<T> featureClazz) {
		DialogsMainPeer peer = getDialogMainPeer(protocolHandler);
		return get(peer, featureClazz);
	}

	public <T extends ISupraSphereFeature> T get(DialogsMainPeer peer,
			Class<T> featureClazz) {
		AbstractSsDocFeature feature = create(featureClazz);
		feature.setPeer(peer);
		XMLDB xmldb;
		if (peer != null && peer.getXmldb() != null) {
			xmldb = peer.getXmldb();
		} else {
			xmldb = new XMLDB();
		}
		feature.setXmldb(xmldb);
		feature.setUtils(Utils.getUtils(xmldb));
		return featureClazz.cast(feature);
	}

	public VerifyAuth createVerifyAuth() {
		VerifyAuth verifyAuth = new VerifyAuth(new Hashtable());
		configureVerifyAuth(verifyAuth);
		return verifyAuth;
	}

	/**
	 * @param verifyAuth
	 */
	public void configureVerifyAuth(VerifyAuth verifyAuth) {
		if (verifyAuth != null) {
			verifyAuth.setSphereDocument(getSupraSphereDocument());
		} else {
			logger.warn("verifyAuth is null");
		}
	}

	private Document getSupraSphereDocument() {
		return this.utils.getSupraSphereDocument();
	}

	private <T extends ISupraSphereFeature> AbstractSsDocFeature create(
			Class<T> featureClazz) {
		return (AbstractSsDocFeature) this.resolver.create(featureClazz);
	}

	/**
	 * @param xmldb
	 * @return
	 */
	public ISupraSphereEditFacade getEditableSupraSphere(XMLDB xmldb) {
		return new SsDocSupraSphereEditFacade( xmldb );
	}
}
