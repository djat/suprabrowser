/**
 * 
 */
package ss.client.ui.clubdealmanagement.fileassosiation;

import java.util.Hashtable;

import org.eclipse.jface.viewers.TableViewer;

import ss.domainmodel.clubdeals.ClubdealWithContactsObject;

/**
 * @author zobo
 *
 */
public interface IDataHashProvider {
	public Hashtable<ClubdealWithContactsObject, Boolean> getHash();

	public TableViewer getViewer();
}
