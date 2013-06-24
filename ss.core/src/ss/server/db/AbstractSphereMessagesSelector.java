package ss.server.db;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.domain.service.ISupraSphereFacade;
import ss.domainmodel.SupraSphereStatement;

public class AbstractSphereMessagesSelector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractSphereMessagesSelector.class);

	protected final XMLDB xmldbOwner;

	/**
	 * @param xmldbOwner
	 */
	public AbstractSphereMessagesSelector(final XMLDB xmldbOwner) {
		super();
		this.xmldbOwner = xmldbOwner;
	}

	/**
	 * @return
	 */
	protected ISupraSphereFacade getSupraSphere() {
		return this.xmldbOwner.getSupraSphere();
	}

	/**
	 * @param personalSphere
	 * @param sphere
	 * @return
	 */
	protected Document getStatisticsDoc(String sphereCore, String sphereId) {
		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereCore
				+ "' and type='stats' and XMLDATA like '%last_launched sphere_id=\""
				+ sphereId + "%'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;

		logger.info("STATEMENT: " + statement);
		return this.xmldbOwner.safeQueryFirstDocument( statement );
	}

	/**
	 * @return the writeLock
	 */
	protected Object getWriteLock() {
		return this.xmldbOwner.getWriteLock();
	}

	/**
	 * @return the utils
	 */
	protected XmldbUtils getUtils() {
		return this.xmldbOwner.getUtils();
	}

}
