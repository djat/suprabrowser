/**
 * 
 */
package ss.domainmodel.workflow;

import org.apache.log4j.Logger;

import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ModelMemberCollection extends XmlListEntityObject<ModelMemberEntityObject> {
	
	private static final Logger logger = SSLogger.getLogger(ModelMemberCollection.class);


	public ModelMemberCollection(){
		super(ModelMemberEntityObject.class, ModelMemberEntityObject.ROOT_ELEMENT_NAME);
	}
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( ModelMemberEntityObject item ) {
		ModelMemberEntityObject existedMember = getByLogin( item.getUserName() );
		if ( existedMember != null ) {
			remove( existedMember );
		}
		super.internalAdd(item);
	}
	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(ModelMemberEntityObject entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public ModelMemberEntityObject get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(ModelMemberEntityObject fav: this) {
			this.remove(fav);
		}
	}
	/**
	 * @param loginName
	 * @return
	 */
	public String getRoleByUsername(String loginName) {
		ModelMemberEntityObject member = getByLogin(loginName);
		return member != null ? member.getRoleName() : null;
	}
	
	/**
	 * @param loginName
	 */
	public ModelMemberEntityObject getByLogin(final String loginName) {
		if ( loginName == null ) {
			return null;
		}
		return findFirst( new IXmlEntityObjectFindCondition<ModelMemberEntityObject>() {
			public boolean macth(ModelMemberEntityObject entityObject) {
				return entityObject.getUserName().equals(loginName);
			}			
		});
	}
	/**
	 * @return
	 */
	public ModelMemberCollection copy() {
		ModelMemberCollection members = new ModelMemberCollection();
		for(ModelMemberEntityObject member : this) {
			logger.error(member);
			members.add(member.copy());
		}
		return members;
	}
	/**
	 * @param login
	 * @return
	 */
	public boolean contains(String login) {
		for(ModelMemberEntityObject member : this) {
			if(member.getUserName().equals(login)) {
				return true;
			}
		}
		return false;
	}

}
