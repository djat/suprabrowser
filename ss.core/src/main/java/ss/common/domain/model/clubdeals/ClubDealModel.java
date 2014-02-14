/**
 * 
 */
package ss.common.domain.model.clubdeals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ss.common.StringUtils;
import ss.common.domain.model.DomainObject;
import ss.common.domain.model.collections.AssociatedFileCollection;
import ss.common.domain.model.collections.ClubDealCollection;
import ss.common.domain.model.collections.ClubDealContactCollection;
import ss.common.domain.model.collections.TypeCollection;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubDealModel extends DomainObject {

	private static final Logger logger = SSLogger.getLogger(ClubDealModel.class);
	
	private final ClubDealCollection clubdeals = new ClubDealCollection();

	private final TypeCollection types = new TypeCollection();

	/**
	 * @return the types
	 */
	public TypeCollection getTypes() {
		return this.types;
	}

	/**
	 * @return the clubdeals
	 */
	public ClubDealCollection getClubdeals() {
		return this.clubdeals;
	}

	public List<ClubDealObject> getClubdeals(final String contactName) {
		List<ClubDealObject> list = new ArrayList<ClubDealObject>();
		for (ClubDealObject cd : this.clubdeals) {
			if (cd.hasContact(contactName)) {
				list.add(cd);
			}
		}
		return list;
	}

	public ClubDealObject getClubDealById(final String id) {
		for (ClubDealObject cd : this.clubdeals) {
			if (cd.getClubdealId().equals(id)) {
				return cd;
			}
		}
		return null;
	}

	public Collection<TypeRecord> getClubDealContactTypes(
			final ClubDealObject cd) {
		return cd.getContactTypes();
	}

	public ClubDealContactCollection getClubDealContacts(
			final ClubDealObject cd, final TypeCollection types) {
		return cd.getClubDealContacts(cd, types);
	}
	
	public void addContactToClubdeal(final String id, final String contactName) {
		if(id==null || contactName==null) {
			return;
		}
		ClubDealObject clubdeal = getClubDealById(id);
		if(clubdeal==null) {
			return;
		}
		clubdeal.addContact(contactName);
	}
	
	public void removeContactFromClubdeal(final String id, final String contactName) {
		if(id==null || contactName==null) {
			return;
		}
		ClubDealObject clubdeal = getClubDealById(id);
		if(clubdeal==null) {
			return;
		}
		clubdeal.removeContact(contactName);
	}
	
	public void removeClubdeal(final String clubdealId) {
		if(clubdealId==null) {
			return;
		}
		ClubDealObject	clubDeal = getClubDealById(clubdealId);
		if(clubDeal==null) {
			return;
		}
		getClubdeals().remove(clubDeal);
	}
	
	public void addClubDeal( final ClubDealObject cd ) {
		if( cd==null ) {
			return;
		}
		getClubdeals().add(cd);
	}
	
	public void setContactType(final ClubDealObject cd, final String contactName, final TypeRecord type) {
		cd.setTypeToContact(contactName, type);
	}
	
	public boolean addNewType( final String name ){
		TypeRecord type = new TypeRecord();
		type.setName(name);
		boolean contains = getTypes().contains(type);
		getTypes().put(type);
		return !contains;
	}
	
	public boolean removeType( final TypeRecord type){
		final boolean res = getTypes().removeType( type );
		if (!res) {
			return res;
		}
		for(ClubDealObject cd : getClubdeals()) {
			cd.typeRemoved( type );
		}
		return res;
	}
	
	public TypeRecord getContactType(final ClubDealObject cd, final String contactName) {
		if(cd==null) {
			return null;
		}
		if(contactName==null) {
			return null;
		}
		ClubDealContactObject contact = cd.getContactByName(contactName);
		return contact.getType();
	}
	
	public AssociatedFileCollection getFilesAssosiatedList( final String idStr ){
		return getClubDealById(idStr).getFiles();
	}
	
	public ClubDealCollection getClubDealsAssossiatedWithFile( final Long messageId ){
		final ClubDealCollection list = new ClubDealCollection();
		if (messageId == null) {
			logger.error("MessageId is blank");
			return list;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("messageId: " + messageId);
		}
		for (ClubDealObject clubdeal : getClubdeals()) {
			if (logger.isDebugEnabled()) {
				logger.debug("clubdeal now: " + clubdeal.getName());
			}
			AssociatedFileCollection files = clubdeal.getFiles();
			if (files.getById( messageId ) != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug("File added");
				}
				list.add(clubdeal);
			}
		}
		return list;
	}
	
	public boolean renameType(String oldName, String newName) {
		if(StringUtils.isBlank(newName)) {
			logger.debug("new name is blank");
			return false;
		}
		if(oldName.equals(newName)) {
			logger.debug("name not changed");
			return true;
		}
		if(getTypes().containsName(newName)) {
			logger.debug("such type already exist");
			return false;
		}
		for(TypeRecord record : getTypes()) {
			if(!record.getName().equals(oldName)) {
				continue;
			}
			logger.debug(record.getName()+" change on "+newName);
			record.setName(newName);
		}
		for(ClubDealObject cd : getClubdeals()) {
			for(ClubDealContactObject contact : cd.getContacts()) {
				if(contact.getType()==null || !contact.getType().equals(new TypeRecord(oldName))) {
					continue;
				}
				logger.debug("rename type for "+contact.getContactName());
				contact.getType().setName(newName);
			}
		}
		
		return true;
	}
}
