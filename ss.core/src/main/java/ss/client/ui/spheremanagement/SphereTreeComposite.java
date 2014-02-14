/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import ss.client.localization.LocalizationLinks;
import ss.common.UiUtils;
/**
 *
 */
public class SphereTreeComposite extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereTreeComposite.class);
	
	private final SphereManager manager;

	private static final String ALL_GROUP_SPHERES = "SPHERETREECOMPOSITE.ALL_GROUP_SPHERES";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_SPHEREMANAGEMENT_SPHERETREECOMPOSITE);
	
	private final TreeViewer tv;

	/**
	 * @param parent
	 * @param style
	 */
	public SphereTreeComposite(Composite parent, SphereManager manager) {
		this(parent, manager, SWT.BORDER);
	}

	/**
	 * @param shellSashForm
	 * @param manager2
	 * @param none
	 */
	public SphereTreeComposite(Composite parent, SphereManager manager, int style) {
		super(parent, SWT.NONE);
		this.manager = manager;
		setLayout(LayoutUtils.createFullFillGridLayout());
		
		this.tv = new TreeViewer(this, style);
		this.tv.getTree().setLayoutData(LayoutUtils.createFullFillGridData());
		
		this.tv.setContentProvider(new SphereTreeContentProvider());
		this.tv.setLabelProvider(new SphereTreeLabelProvider());
		
		this.tv.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ITreeSelection selection = (ITreeSelection) event
						.getSelection();
				ManagedSphere item = (ManagedSphere) selection
						.getFirstElement();
				selectedSphereChanged(item);
			}
		});
		
		this.tv.addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				ITreeSelection selection = (ITreeSelection) event
				.getSelection();
				ManagedSphere item = (ManagedSphere) selection
				.getFirstElement();
				sphereDoubleClick(item);
			}
		});
		this.tv.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if(event.button==3 && event.count==1) {
					SphereTreeComposite.this.manager.fireShowContextMenu();
				}
			}
		});
		this.tv.setInput(new Object[] { manager.getRootSphere() }); 
	}

	public void addHeader() {
		TreeColumn col = new TreeColumn(this.tv.getTree(), SWT.NONE);
		col.pack();
		col.setText(this.bundle.getString(ALL_GROUP_SPHERES));
		this.tv.getTree().setHeaderVisible(true);
	}

	public void addLabel() {
		Label label = new Label(this, SWT.BEGINNING | SWT.BOTTOM);
		label.setText(this.bundle.getString(ALL_GROUP_SPHERES));
		label.moveAbove(this.tv.getTree());
	}

	/**
	 * @param item
	 */
	private void sphereDoubleClick(ManagedSphere item) {
		this.manager.openSphere(item);
	}

	private void selectedSphereChanged(ManagedSphere item) {
		this.manager.setSelectedSphere(item);
	}

	/**
	 * @return
	 */
	public ManagedSphere getSelected() {
		try {
			TreeItem[] selection = this.tv.getTree().getSelection();
			if (selection==null || selection.length == 0){
				return null;
			}
			return (ManagedSphere)selection[0].getData();
		} catch (Exception ex) {
			logger.error("nul pointer in sphere tree composite", ex);
		}
		return null;
	}

	/**
	 * 
	 */
	public void rebuild() {
		logger.debug("start rebuild sphere hierarchy tree");
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				SphereTreeSelector selector = new SphereTreeSelector(getTV());
				SphereTreeComposite.this.manager.forseReload();
				SphereTreeComposite.this.tv.setInput(new Object[] { SphereTreeComposite.this.manager.getRootSphere() });
				SphereTreeComposite.this.tv.refresh();
				
				selector.findAndSelectNewSphere();
			}
		});
	}

	TreeViewer getTV() {
		return this.tv;
	}

	public void selectSphere(ManagedSphere sphereToFind) {
		tryFindAndSelect(this.tv.getTree().getItems(), sphereToFind);
	}
	
	private void tryFindAndSelect(TreeItem[] items, ManagedSphere sphereToFind) {
		for(TreeItem item : items) {
			if (logger.isDebugEnabled()) {
				logger.debug("item data : "+item.getData());
			}
			if(item.getData()!=null && item.getData().equals(sphereToFind)) {
				this.tv.getTree().setSelection(item);
			} else {
				tryFindAndSelect(item.getItems(), sphereToFind);
			}
		}
	}
	
	public List<ManagedSphere> getSelectedSubtree() {
		List<ManagedSphere> list = new ArrayList<ManagedSphere>();
		collectChildSpheres(getSelected(), list);
		return list;
	}

	private void collectChildSpheres(ManagedSphere sphere, List<ManagedSphere> list) {
		list.add(sphere);
		for(ManagedSphere childSphere : sphere.getChildren()) {
			collectChildSpheres(childSphere, list);
		}
	}
}
