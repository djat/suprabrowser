/**
 * 
 */
package ss.domainmodel.workflow;

/**
 * @author roman
 *
 */
public class Role {
	
	private String roleTitle;
	
	public Role() {		
	}
	
	public Role(String roleTitle) {
		this.roleTitle = roleTitle;
	}
	
	public String getTitle() {
		return this.roleTitle;
	}
}
