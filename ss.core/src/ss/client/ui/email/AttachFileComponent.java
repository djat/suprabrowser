package ss.client.ui.email;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.common.ExceptionHandler;
import ss.common.PathUtils;
import ss.domainmodel.FileStatement;

/**
 * 
 */
public class AttachFileComponent {

	private static final String OPEN = "ATTACHFILECOMPONENT.OPEN";

	private static final String REMOVE = "ATTACHFILECOMPONENT.REMOVE";

	private static final String ATTACH = "ATTACHFILECOMPONENT.ATTACH";

	private static final String ATTACHED_FILES = "ATTACHFILECOMPONENT.ATTACHED_FILES";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_ATTACHFILECOMPONENT);

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AttachFileComponent.class);

	private final Composite content;

	private ListViewer listView;
	
	private Button removeButton;

	private final AttachedFileCollection files = new AttachedFileCollection();

	/**
	 * @param parent
	 * @param style
	 */
	public AttachFileComponent(Composite parent) {
		this(parent, SWT.NONE);
	}
	
	public AttachFileComponent(Composite parent, int style) {
		this.content = new Composite(parent, style);
		createContent(this.content);
	}

	/**
	 * @param parent
	 */
	private void createContent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
    	layout.marginHeight = 1;
    	layout.marginTop = 1;
    	layout.marginBottom = 1;
		parent.setLayout(layout);

		GridData data;
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 3;
		Label label = new Label(parent, SWT.LEFT);
		label.setText(this.bundle.getString(ATTACHED_FILES));
		label.setLayoutData(data);
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 3;

		this.listView = new ListViewer(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.HORIZONTAL );
		this.listView.getList().setLayoutData(data);
		this.listView.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((AttachedFileCollection) inputElement).toObjectArray();
			}

			public void dispose() {
				// do nothing
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// do nothing
			}
		});
		this.listView.setInput(this.files);
		this.listView.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				final IAttachedFile file = (IAttachedFile) element;
				return file.getName() + " " + file.getSize();
			}
		});

		this.listView
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						AttachFileComponent.this.removeButton.setEnabled( !event.getSelection().isEmpty() );
					}
				});
		this.listView.getList().addKeyListener(new KeyAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					removeSelectedAttachedFiles();
				}
			}

		});

		data = new GridData();
		data.horizontalAlignment = SWT.LEFT;
		data.verticalAlignment = SWT.BEGINNING;

		final Button attachButton = new Button(parent, SWT.PUSH);
		attachButton.setLayoutData(data);
		attachButton.setText(this.bundle.getString(ATTACH));
		attachButton.addSelectionListener(new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				attachNewFile();
			}

		});
		
		this.removeButton = new Button(parent, SWT.PUSH);
		this.removeButton.setLayoutData(data);
		this.removeButton.setText(this.bundle.getString(REMOVE));
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedAttachedFiles();
			}
		});

	}

	private void attachNewFile() {
		final FileDialog fileDialog = new FileDialog(this.content.getShell(),
				SWT.OPEN | SWT.MULTI );
		fileDialog.setText(this.bundle.getString(OPEN));
		fileDialog.setFilterPath("C:/");
		String[] filterExt = { "*.*" };
		fileDialog.setFilterExtensions(filterExt);
		if (fileDialog.open() == null) {
			return;
		}
		for (String fileName : fileDialog.getFileNames()) {
			final String fullFileName = PathUtils.combinePath(fileDialog
					.getFilterPath(), fileName);
			logger.info("add file " + fullFileName);
			try {
				this.files.add(new AttachedFile(fullFileName));
			} catch (IOException ex) {
				ExceptionHandler.handleException(this, ex);
			}
		}
		this.listView.refresh(false);
	}

	public void attachNewForwardingFile(final FileStatement file) {
		getFiles().add(new AttachedFileProxy(file));
		this.listView.refresh(false);
	}
	/**
	 * @return the content
	 */
	public Composite getContent() {
		return this.content;
	}

	/**
	 * @return the files
	 */
	public AttachedFileCollection getFiles() {
		return this.files;
	}

	@SuppressWarnings("unchecked")
	private void removeSelectedAttachedFiles() {
		IStructuredSelection selection = (IStructuredSelection) this.listView
				.getSelection();
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			final IAttachedFile file = (IAttachedFile) iterator.next();
			this.files.remove(file);
		}
		this.listView.refresh(false);
	}

}
