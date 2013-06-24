/**
 * 
 */
package ss.server.debug.commands;

import ss.server.debug.ssrepair.PersonalVisibilityRepairer;

/**
 *
 */
public class RepairPersonalVisibility extends AbstractRepairerCommand {

	/**
	 * @param repairerClass
	 */
	public RepairPersonalVisibility() {
		super(PersonalVisibilityRepairer.class);
	}

}