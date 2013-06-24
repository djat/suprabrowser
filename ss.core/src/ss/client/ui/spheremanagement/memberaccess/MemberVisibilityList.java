/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;



/**
 *
 */
public class MemberVisibilityList implements Iterable<MemberAccess>{

	SortedSet<MemberAccess> members = new TreeSet<MemberAccess>();

	/**
	 * @return
	 */
	public Object[] toArray() {
		return this.members.toArray();
	}

	/**
	 * @param memberAccess
	 */
	public void add(MemberAccess memberAccess) {
		this.members.add( memberAccess );
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	public Iterator<MemberAccess> iterator() {
		return this.members.iterator();
	}

	/**
	 * @param contactName
	 * @return
	 */
	public MemberAccess find(String contactName) {
		if ( contactName == null ) {
			return null;
		}		
		for( MemberAccess member : this ) {
			if ( contactName.equals( member.getContactName() ) ) {
				return member;
			}
		}
		return null;
	}
	
	/**
	 * @param contactName
	 * @return
	 */
	public MemberAccess require(String contactName) {
		MemberAccess member = find(contactName);
		if ( member == null ) {
			throw new IndexOutOfBoundsException( "Can't find member by name " + contactName + " in " + this );
		}		
		return member;
	}
	
	
}
