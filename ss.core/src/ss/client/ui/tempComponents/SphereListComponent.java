/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.tempComponents.interfaces.ISphereListOwner;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SphereListComponent {
	
	private static final Logger logger = SSLogger.getLogger(SphereListComponent.class);
	
	private Composite mainComposite;
	
	private Composite radioComp;
	
	private List typeList;
	
	private ScrolledComposite scrolled;
	
	private java.util.List<SpheresListChangeListener> listeners = new ArrayList<SpheresListChangeListener>();
	
	private ISphereListOwner sphereListWindow;

	private static final String GROUP = "SPHERELISTCOMPOSITE.GROUP";
	private static final String PRIVATE = "SPHERELISTCOMPOSITE.PRIVATE";
	private static final String PERSONAL = "SPHERELISTCOMPOSITE.PERSONAL";

	private static ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SPHERELISTCOMPOSITE);

	private static final String SPHERE_TYPE_GROUP = bundle.getString(GROUP);
	private static final String SPHERE_TYPE_PRIVATE = bundle.getString(PRIVATE);
	private static final String SPHERE_TYPE_PERSONAL = bundle.getString(PERSONAL);
	/**
	 * 
	 */
	public SphereListComponent(Composite parent, ISphereListOwner sphereListWindow) {
		this.sphereListWindow = sphereListWindow;
		createContent(parent);
	}
	
	private void createContent(Composite parent) {
		this.mainComposite = new Composite(parent, SWT.BORDER);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.mainComposite.setLayoutData(data);

		this.mainComposite.setLayout(new GridLayout(2, false));

		this.typeList = new List(this.mainComposite, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.typeList.setLayoutData(data);

		this.typeList.setItems(new String[] { SPHERE_TYPE_GROUP, SPHERE_TYPE_PERSONAL,  SPHERE_TYPE_PRIVATE });
		this.typeList.select(1);
		this.typeList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent se) {
				createOrUpdateRadioButtons();
			}
		});

		this.scrolled = new ScrolledComposite(this.mainComposite, SWT.BORDER
				| SWT.V_SCROLL);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.scrolled.setLayoutData(data);

		this.radioComp = new Composite(this.scrolled, SWT.NONE);

		this.radioComp.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));

		this.radioComp.setLayout(new GridLayout());

		this.scrolled.setContent(this.radioComp);
		this.scrolled.setExpandVertical(true);
		this.scrolled.setExpandHorizontal(true);
		this.scrolled.setAlwaysShowScrollBars(true);

		createOrUpdateRadioButtons();
	}
	
	public void resizeScrolledComposite() {
		int prefHeight = this.radioComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		this.scrolled.setMinHeight(prefHeight);
	}

	@SuppressWarnings("unchecked")
	private void createOrUpdateRadioButtons() {
		logger.debug("create or update radio button");
		for (Control control : this.radioComp.getChildren()) {
			control.dispose();
		}
		Collection<String> spheres = getSpheresByType();
		if(spheres.size()==0) {
			createNoSpheresLabel();
		} else {
			for (String sphere : spheres) {
				createNextRadioButton(sphere);
			}
			selectFirstButton();
		}
		
		for(SpheresListChangeListener listener : this.listeners) {
			listener.sphereChanged(new SphereChangeEvent(this));
		}
		
		this.radioComp.layout();
		resizeScrolledComposite();
	}

	/**
	 * 
	 */
	private void createNoSpheresLabel() {
		Label label = new Label(this.radioComp, SWT.CENTER);
		int index = this.typeList.getSelectionIndex();
		String currentType = this.typeList.getItem(index);
		label.setText("No "+currentType+" spheres");
	}

	@SuppressWarnings("unchecked")
	private Collection<String> getSpheresByType() {
		Collection<String> spheres = new Vector<String>();

		int index = this.typeList.getSelectionIndex();
		String currentType = this.typeList.getItem(index);
		
		if (currentType.equals(SPHERE_TYPE_GROUP)) {
			spheres = this.sphereListWindow.getSphereOwner().getGroupSpheres();
		} else if (currentType.equals(SPHERE_TYPE_PRIVATE)) {
			spheres = this.sphereListWindow.getSphereOwner().getPrivateSpheres();
		} else if (currentType.equals(SPHERE_TYPE_PERSONAL)) {
			spheres = this.sphereListWindow.getSphereOwner().getPersonalSpheres();
		}
		return spheres;
	}

	/**
	 * 
	 */
	private void selectFirstButton() {
		Button firstButton = (Button) this.radioComp.getChildren()[0];
		firstButton.setSelection(true);
		this.sphereListWindow.setCurrent((firstButton).getText());
	}

	private void createNextRadioButton(String sphere) {
		Button radio = new Button(this.radioComp, SWT.RADIO);
		radio.setText(sphere);
		radio.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		radio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				SphereListComponent.this.sphereListWindow.setCurrent(((Button) se.widget).getText());
				SphereListComponent.this.sphereListWindow.setFocusToSubjectField();
			}
		});
	}
	
	public Composite asComposite() {
		return this.mainComposite;
	}
	
	public int getSpheresCount() {
		return getSpheresByType().size();
	}
	
	public void addSphereListChangedListener(SpheresListChangeListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeSpheresListChangedListener(SpheresListChangeListener listener) {
		if(!this.listeners.contains(listener)) {
			return;
		}
		this.listeners.remove(listener);
	}
}
