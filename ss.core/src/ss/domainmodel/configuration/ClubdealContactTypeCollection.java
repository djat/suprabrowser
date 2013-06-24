/**
 * 
 */
package ss.domainmodel.configuration;

import org.apache.log4j.Logger;

import ss.common.StringUtils;
import ss.framework.entities.xmlentities.XmlListEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubdealContactTypeCollection extends XmlListEntityObject<ClubdealContactType> {

	private static final Logger logger = SSLogger.getLogger(ClubdealContactTypeCollection.class);
	
	public ClubdealContactTypeCollection() {
		super(ClubdealContactType.class, ClubdealContactType.ROOT_ELEMENT_NAME );
	}
	
	public void addType(final String type) {
		if(StringUtils.isBlank(type)) {
			logger.info("can't add null type");
			return;
		}
		ClubdealContactType contactType = new ClubdealContactType();
		contactType.setName(type);
		addType(contactType);
	}
	
	public void addType(final ClubdealContactType type) {
		if(type==null || StringUtils.isBlank(type.getName()) || getTypeByName(type.getName())!=null) {
			logger.info("can't add null type");
			return;
		}
		super.internalAdd(type);
	}
	
	public void removeType(final ClubdealContactType type) {
		if(type==null) {
			logger.error("Can't remove null type");
			return;
		}
		super.internalRemove(type);
	}
	
	public void removeType(final String typeName) { 
		removeType(getTypeByName(typeName));
	}
	
	public ClubdealContactType getTypeByName(final String typeName) {
		for(ClubdealContactType type : this) {
			if(type.getName().equals(typeName)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void clear() {
		super.internalClear();
	}

	/**
	 * @return
	 */
	public String[] toArray() {
		String[] objects = new String[getCount()];
		int i = 0;
		for(ClubdealContactType type : this) {
			objects[i] = type.getName();
			i++;
		}
		return objects;
	}

	/**
	 * @param role
	 * @return
	 */
	public boolean contains(String typeName) {
		return getTypeByName(typeName)!=null;
	}

	/**
	 * @param role
	 * @return
	 */
	public Object indexOf(String typeName) {
		int i = -1;
		if(typeName==null) {
			return i;
		}
		for(ClubdealContactType type : this) {
			i++;
			if(typeName.equals(type.getName())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param index
	 * @return
	 */
	public String get(int index) {
		int i = 0;
		if(index<0 || index>=getCount()) {
			throw new IndexOutOfBoundsException();
		}
		for(ClubdealContactType type : this) {
			if(i!=index) {
				i++;
				continue;
			}
			return type.getName();
		}
		return null;
	}
}
