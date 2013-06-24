/**
 * 
 */
package ss.common.domain.model.collections;

import ss.common.domain.model.clubdeals.AssociatedFile;

/**
 * @author roman
 *
 */
public class AssociatedFileCollection extends DomainObjectList<AssociatedFile> {

	/**
	 * @param messageId
	 * @return
	 */
	public AssociatedFile getById(final long messageId) {
		for(AssociatedFile file : this) {
			if(file.getId()==messageId) {
				return file;
			}
		}
		return null;
	}

}
