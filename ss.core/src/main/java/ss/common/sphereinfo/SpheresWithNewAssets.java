/**
 * 
 */
package ss.common.sphereinfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ss.common.ListUtils;

/**
 *
 */
public class SpheresWithNewAssets implements Serializable {

	private static final long serialVersionUID = 1642999081605213123L;

	public class SingleSphereWithNewAssets implements Serializable {
		
		private static final long serialVersionUID = -7247896997977531037L;

		private final String sphereId;
		
		private final String sphereName;
		
		private final int count;

		private SingleSphereWithNewAssets(String sphereId, String sphereName,
				int count) {
			super();
			this.sphereId = sphereId;
			this.sphereName = sphereName;
			this.count = count;
		}

		public String getSphereId() {
			return this.sphereId;
		}

		public String getSphereName() {
			return this.sphereName;
		}

		public int getCount() {
			return this.count;
		}

		@Override
		public String toString() {
			return "" + getCount() + " assets for (" + getSphereId() + "," + getSphereName() + ")";
		}
	}
	
	private final List<SingleSphereWithNewAssets> groupSpheres;
	
	private final List<SingleSphereWithNewAssets> personalSpheres;
	
	private SingleSphereWithNewAssets emailBox = null;
	
	public SpheresWithNewAssets(){
		this.groupSpheres = new ArrayList<SingleSphereWithNewAssets>();
		this.personalSpheres = new ArrayList<SingleSphereWithNewAssets>();
	}

	public void addPersonal(String sphereId, String sphereName, int count) {
		if ( checkIsWrongData(sphereId,sphereName,count) ) return;
		this.personalSpheres.add( new SingleSphereWithNewAssets(sphereId,sphereName,count) );
	}

	public void addEmail(String sphereId, String sphereName, int count) {
		if ( checkIsWrongData(sphereId,sphereName,count) ) return;
		this.emailBox = new SingleSphereWithNewAssets(sphereId,sphereName,count);
	}

	public void addGroup(String sphereId, String sphereName, int count) {
		if ( checkIsWrongData(sphereId,sphereName,count) ) return;
		this.groupSpheres.add( new SingleSphereWithNewAssets(sphereId,sphereName,count) );
	}
	
	private boolean checkIsWrongData( String sphereId, String sphereName, int count ){
		return ( (sphereId == null) || (sphereName == null) || (count <= 0) );			
	}
	
	public boolean isNewInGroups(){
		return !(this.groupSpheres.isEmpty());
	}
	
	public boolean isNewInPersonals(){
		return !(this.personalSpheres.isEmpty());
	}
	
	public boolean isNewInEmailBox(){
		return (this.emailBox != null);
	}
	
	public boolean isNew(){
		return  (isNewInGroups() || isNewInPersonals() || isNewInEmailBox());
	}

	public List<SingleSphereWithNewAssets> getGroupSpheres() {
		return this.groupSpheres;
	}

	public List<SingleSphereWithNewAssets> getPersonalSpheres() {
		return this.personalSpheres;
	}

	public SingleSphereWithNewAssets getEmailBox() {
		return this.emailBox;
	}

	@Override
	public String toString() {
		if (!isNew()) {
			return "There are no spheres with new assets";
		}
		String s = "";
		if ( isNewInEmailBox() ) {
			s += "Email box has " + getEmailBox().toString();
		} else {
			s += "Email box has no new assets";
		}
		s += ";  ";
		if ( isNewInGroups() ) {
			s += "New in groups : " + ListUtils.valuesToString( getGroupSpheres() );
		} else {
			s += "There are no group spheres with new assets";
		}
		s += ";  ";
		if ( isNewInPersonals() ) {
			s += "New in personals : " + ListUtils.valuesToString( getPersonalSpheres() );
		} else {
			s += "There are no personal spheres with new assets";
		}
		return s;
	}
}
