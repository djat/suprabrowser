/**
 * 
 */
package ss.server.debug.commands;

import ss.server.debug.ssrepair.InvalidSphereDefinitionRepairer;

/**
 *
 */
public class RepairInvalidSphereDefinition extends AbstractRepairerCommand {

	/**
	 * @param repairerClass
	 */
	public RepairInvalidSphereDefinition() {
		super(InvalidSphereDefinitionRepairer.class);
	}

}
