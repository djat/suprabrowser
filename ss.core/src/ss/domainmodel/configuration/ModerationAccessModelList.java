/**
 * 
 */
package ss.domainmodel.configuration;

import org.apache.log4j.Logger;

import ss.framework.entities.xmlentities.XmlListEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ModerationAccessModelList extends XmlListEntityObject<ModerationAccessModel> {

	private static final Logger logger = SSLogger.getLogger(ModerationAccessModelList.class);
	
	public ModerationAccessModelList() {
		super(ModerationAccessModel.class, ModerationAccessModel.ROOT_ELEMENT_NAME );
	}
	
	public void addClubdealAccess(final ModerationAccessModel item) {
		if(item==null) {
			logger.error("Can't add null ClubDealAccess object");
			return;
		}
		super.internalAdd(item);
	}
	
	public void removeClubdealAccess(final ModerationAccessModel item) {
		if(item==null) {
			logger.error("Can't remove null ClubDealAccess object");
			return;
		}
		super.internalRemove(item);
	}
	
	public ModerationAccessModel getByDisplayName(final String displayName) {
		if(displayName==null) {
			logger.error("null display name");
			return null;
		}
		for(ModerationAccessModel access : this) {
			if(access.getDisplayName().equals(displayName)) {
				return access;
			}
		}
		return null;
	}
	
	public ModerationAccessModel getBySystemName(final String systemName) {
		if(systemName==null) {
			logger.error("null display name");
			return null;
		}
		for(ModerationAccessModel access : this) {
			if(access.getSystemName().equals(systemName)) {
				return access;
			}
		}
		return null;
	}
}
