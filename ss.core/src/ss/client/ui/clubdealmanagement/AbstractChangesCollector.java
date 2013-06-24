/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * @author roman
 *
 */
public abstract class  AbstractChangesCollector<L extends Object, T extends Object> implements IChangesCollector {

	private final AbstractManageComposite<L, T> manageComposite;
	
	protected final static String START = "start";
	
	protected final static String ADD = "add";
	
	protected final static String REMOVE = "remove";
	
	private List<T> startList = new ArrayList<T>();
	
	private List<T> addList = new ArrayList<T>();
	
	private List<T> removeList = new ArrayList<T>();
	
	private Hashtable<String, List<T>> listTable = new Hashtable<String, List<T>>();
	
	private final L selection;
	
	protected AbstractChangesCollector(final AbstractManageComposite<L, T> composite) {
		this.manageComposite = composite;
		this.selection = this.manageComposite.getSelection();
		this.listTable.put(START, this.startList);
		this.listTable.put(ADD, this.addList);
		this.listTable.put(REMOVE, this.removeList);
		initStartList();
	}
	
	public final void collectAndSaveChanges() {
		collectChanges();
		saveChanges();
	}
	
	protected abstract void saveChanges();
	
	protected Hashtable<T, Boolean> getTableItemsFromManageComposite() {
		return getManageComposite().getTableItems();
	}
	
	protected L getManageCompositeSelection() {
		return getManageComposite().getSelection();
	}
	
	protected void collectChanges() {
		Hashtable<T, Boolean> stateList = getTableItemsFromManageComposite();
		for(T id : stateList.keySet()) {
			if(isInStartCheckedList(id) && !stateList.get(id).booleanValue()) {
				addToList(REMOVE, id);
			} else if(!isInStartCheckedList(id) && stateList.get(id).booleanValue()) {
				addToList(ADD, id);
			}
		}
	}

	public void initStartList() {
		clearLists();
		Hashtable<T, Boolean> table = getTableItemsFromManageComposite();
		for(T item : table.keySet()) {
			if(table.get(item).equals(new Boolean(true))) {
				addToList(START, item);
			}
		}
	}
	
	public final AbstractManageComposite<L, T> getManageComposite() {
		return this.manageComposite;
	}
	
	protected void addToList(String property, T addition) {
		this.listTable.get(property).add(addition);
	}
	
	protected void clearLists() {
		this.addList.clear();
		this.removeList.clear();
		this.startList.clear();
	}
	
	protected L getSelection() {
		return this.selection;
	}
	
	protected boolean isInStartCheckedList(final T item) {
		return this.startList.contains(item);
	}
	
	protected Collection<T> getAddList() {
		return Collections.unmodifiableCollection(this.addList);
	}
	
	protected Collection<T> getRemoveList() {
		return Collections.unmodifiableCollection(this.removeList);
	}
}
