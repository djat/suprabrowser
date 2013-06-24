/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public abstract class AbstractManageComposite<L extends Object, T extends Object> extends Composite {

	private static final Logger logger = SSLogger.getLogger(AbstractManageComposite.class);
	
	private final ClubdealFolder parentFolder;
	
	protected CheckboxTableViewer viewer;
	
	protected Button applyButton;
	
	private IChangesCollector collector;
	
	public AbstractManageComposite(ClubdealFolder folder) {
		super(folder, SWT.BORDER);
		this.parentFolder = folder;
		setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected abstract void createContent();
	
	protected abstract void checkAvailableItems();
	
	public abstract L getSelection();
	
	public ClubdealFolder getFolder() {
		return this.parentFolder;
	}
	
	public ClubdealManager getManager() {
		return getFolder().getWindow().getManager();
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<T, Boolean> getTableItems() {
		Hashtable<T, Boolean> table = new Hashtable<T, Boolean>();
		for(TableItem item : this.viewer.getTable().getItems()) {
			table.put((T)item.getData(), new Boolean(item.getChecked()));
		}
		return table;
	}
	
	protected void setChanged(boolean value) {
		this.applyButton.setEnabled(value);
		getFolder().refreshOtherTabs(getClass());
	}
	
	protected SelectionListener getApplyListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveChanges();
			}
		};
	}
	

	@SuppressWarnings("unchecked")
	protected ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent e) {
				boolean reverted = revertCheckingIfNeed((T)e.getElement());
				if(reverted) {
					return;
				}
				setChanged(true);
			}
		};
	}
	
	/**
	 * @param e
	 */
	abstract protected boolean revertCheckingIfNeed(T element);

	protected ISelectionChangedListener getListListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent se) {
				if(se.getSelection()==null) {
					return;
				}
				if(((StructuredSelection)se.getSelection()).getFirstElement()==null) {
					return;
				}
				refreshViewer();
				setCollector(getNewCollector());
				setChanged(false);
				AbstractManageComposite.this.viewer.getTable().setEnabled(true);
			}
		};
	}

	protected abstract IChangesCollector getNewCollector();	

	private IChangesCollector getCollector() {
		return this.collector;
	}
	
	protected void setCollector(
			IChangesCollector collector) {
		this.collector = collector;
	}
	
	
	
	public void saveChanges() {
		if(getCollector()==null) {
			return;
		}
		getCollector().collectAndSaveChanges();
		getManager().saveToServer();
		setChanged(false);
		refreshViewer();
	}
	
	protected void packColumns() {
		for(TableColumn col : this.viewer.getTable().getColumns()) {
			col.setWidth(100);
		}
	}
	
	public void refreshViewer() {
		logger.debug("refresh viewer "+getClass());
		this.viewer.refresh();
		checkAvailableItems();
	}
	
	protected ChangesDetector getChangesDetector() {
		return this.parentFolder.getWindow().getChangesDetector();
	}
}
