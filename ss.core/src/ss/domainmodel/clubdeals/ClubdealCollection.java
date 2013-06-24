/**
 * 
 */
package ss.domainmodel.clubdeals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ss.domainmodel.SphereStatement;

/**
 * @author roman
 *
 */
public class ClubdealCollection implements Iterable<ClubdealWithContactsObject> {

	private final Set<ClubdealWithContactsObject> clubdealList;
	
	private final HashMap<String,ClubdealWithContactsObject> idToClubdeal = new HashMap<String, ClubdealWithContactsObject>();
	
	public ClubdealCollection() {
		this(null);
	}
	
	public ClubdealCollection( final List<ClubdealWithContactsObject> clubdealList) {
		this.clubdealList = new TreeSet<ClubdealWithContactsObject>(new Comparator<ClubdealWithContactsObject>(){

			public int compare( final ClubdealWithContactsObject c1, final ClubdealWithContactsObject c2 ) {
				final String name1 = c1.getClubdeal().getDisplayName();
				final String name2 = c2.getClubdeal().getDisplayName();
				return name1.compareToIgnoreCase( name2 );
			}
			
		});
		if ( clubdealList != null ) {
			for( ClubdealWithContactsObject clubdealWithContactsObject : clubdealList ) {
				add(clubdealWithContactsObject);				
			}
		}
	}
	
	public Iterator<ClubdealWithContactsObject> iterator() {
		return this.clubdealList.iterator();
	}
	
	public ClubdealWithContactsObject getClubdealById(final String sphereId) {
		for(ClubdealWithContactsObject cd : this.clubdealList) {
			if(cd.getClubdealSystemName().equals(sphereId)) {
				return cd;
			}
		}
		return null;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public List<ClubdealWithContactsObject> getClubdealsForContact(String contactName) {
		List<ClubdealWithContactsObject> clubdeals = new ArrayList<ClubdealWithContactsObject>();
		for(ClubdealWithContactsObject cd : this.clubdealList) {
			if(!cd.hasContact(contactName)) {
				continue;
			}
			clubdeals.add(cd);
		}
		return clubdeals;
	}

	/**
	 * @param selectedClubdeal
	 */
	public void remove(ClubdealWithContactsObject cd) {
		if(!contains(cd)) {
			return;
		}
		this.clubdealList.remove(getById(cd.getClubdealSystemName()));
		this.idToClubdeal.remove(cd.getClubdealSystemName());
	}

	/**
	 * @param cd
	 */
	public void add(ClubdealWithContactsObject cd) {
		if(contains(cd)) {
			return;
		}
		this.clubdealList.add(cd);
		this.idToClubdeal.put( cd.getClubdealSystemName(), cd );
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return this.clubdealList.toArray();
	}
	
	public ClubdealWithContactsObject getById(final String id) {
		return id != null ? this.idToClubdeal.get(id) : null;
	}
	
	private boolean contains(final String id) {
		return getById(id)!=null;
	}
	
	private boolean contains(final ClubdealWithContactsObject cd) {
		return contains(cd.getClubdealSystemName());
	}

	/**
	 * @param clubdealName
	 * @return
	 */
	public ClubdealWithContactsObject getByName(String clubdealName) {
		if(clubdealName==null) {
			return null;
		}
		for(ClubdealWithContactsObject cd : this.clubdealList) {
			if(cd.getClubDealDisplayName().equals(clubdealName)) {
				return cd;
			}
		}
		return null;
	}
	
	public List<SphereStatement> toSpheres() {
		final List<SphereStatement> spheres = new ArrayList<SphereStatement>();
		for( ClubdealWithContactsObject clubdealWithContacts : this ) {
			final ClubDeal clubdeal = clubdealWithContacts.getClubdeal();
			spheres.add( clubdeal );
		}
		return spheres;
	}
}
