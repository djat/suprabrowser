package ss.client.ui;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.event.createevents.CreateContactAction;
import ss.client.event.createevents.CreateEmailAction;
import ss.client.event.createevents.CreateFileAction;
import ss.client.event.createevents.CreateFilesystemAction;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.event.createevents.CreateMessageAction;
import ss.client.event.createevents.CreateResearchAction;
import ss.client.event.createevents.CreateRssAction;
import ss.client.event.createevents.CreateSphereAction;
import ss.client.event.createevents.CreateTerseAction;
import ss.client.event.createevents.FactoryOfActions;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.docking.DockingTopTitle;
import ss.client.ui.tempComponents.DropDownToolItem;
import ss.common.UiUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

public abstract class AbstractControlPanel {

	public static ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_CONTROLPANEL);

	public static final String NORMAL = "CONTROLPANEL.NORMAL";

	public static final String CONFIRM_RECEIPT = "CONTROLPANEL.CONFIRM_RECEIPT";

	protected static final String WORKFLOW = "CONTROLPANEL.WORKFLOW";

	protected static final String DOUBLE_CLICK_ANY_ITEM_OR_SELECT_A_TAB_TO_BEGIN = "CONTROLPANEL.DOUBLE_CLICK_ANY_ITEM_OR_SELECT_A_TAB_TO_BEGIN";

	protected static final String CREATE = "CONTROLPANEL.CREATE";

	protected String previousType = null;

	protected static final int MIN_WIDTH = 100;

	protected static final double separation = 1.6;

	protected DropDownToolItem dropDownCreateItem;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractControlPanel.class);

	protected Combo deliveryCombo = null;

	protected SupraSphereFrame sF = null;

	protected MessagesPane mP = null;

	protected Composite parent;

	protected Composite buttonComposite;

	protected static int componentReplyPreferedWidth = 60;

	protected static int componentTagPreferedWidth = 50;

	// protected static int componentCreatePreferedWidth = 85;

	public AbstractControlPanel(SupraSphereFrame sF, Composite parentComposite,
			MessagesPane mP, DockingTopTitle headComposite) {
		this.sF = sF;
		this.mP = mP;
		this.parent = parentComposite;
	}

	abstract void layoutComposite(DockingTopTitle headComposite);

	public void activateP2PDeliveryCheck() {

	}

	protected void createCreateLabel() {
		GridData data;

		Label text = new Label(this.buttonComposite, SWT.SINGLE | SWT.LEFT);

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = SWT.CENTER;
		data.horizontalAlignment = GridData.BEGINNING;

		text.setLayoutData(data);
		text.setText(AbstractControlPanel.bundle.getString(CREATE));
		text.setVisible(true);
		text.setEnabled(true);
		if (logger.isDebugEnabled()) {
			logger.debug("Create label in Control Panel created");
		}
	}

	protected void createDropDownItem() {
		ToolBar toolbar = new ToolBar(this.buttonComposite, SWT.RIGHT);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		toolbar.setData(data);

		this.dropDownCreateItem = new DropDownToolItem(toolbar, this);

		toolbar.pack();
		if (logger.isDebugEnabled()) {
			logger.debug("Drop Down Item in Control Panel created");
		}
	}

	public void layout() {
		this.parent.layout();
	}

	public void setPreviousType(String type) {
		this.previousType = type;
	}

	public String getPreviousType() {
		return this.previousType;
	}

	public Combo getDelivery() {
		return this.deliveryCombo;
	}

	public AbstractDelivery getDeliveryType() {
		return DeliveryFactory.INSTANCE.getCurrentDeliveryForSphere(getMP()
				.getSystemName(), getDelivery().getText());
	}

	public SupraSphereFrame getSF() {
		return this.sF;
	}

	public int getMinWidth() {
		return MIN_WIDTH;
	}

	/**
	 * @return Returns the dropDownCreateItem.
	 */
	public DropDownToolItem getDropDownCreateItem() {
		return this.dropDownCreateItem;
	}

	public static String getTypeEmail() {
		return CreateEmailAction.EMAIL_TITLE;
	}

	public static String getTypeRss() {
		return CreateRssAction.RSS_TITLE;
	}

	public static String getTypeKeywords() {
		return CreateKeywordsAction.KEYWORD_TITLE;
	}

	public static String getTypeFile() {
		return CreateFileAction.FILE_TITLE;
	}

	public static String getTypeBookmark() {
		return CreateBookmarkAction.BOOKMARK_TITLE;
	}

	public static String getTypeMessage() {
		return CreateMessageAction.MESSAGE_TITLE;
	}

	public static String getTypeTerse() {
		return CreateTerseAction.TERSE_TITLE;
	}

	public static String getTypeSphere() {
		return CreateSphereAction.SPHERE_TITLE;
	}

	public static String getTypeContact() {
		return CreateContactAction.CONTACT_TITLE;
	}

	public static String getTypeResearch() {
		return CreateResearchAction.RESEARCH_TITLE;
	}

	public static String getTypeFilesystem() {
		return CreateFilesystemAction.FILESYSTEM_TITLE;
	}

	public static String getDefaultValueForRootTab() {
		return AbstractControlPanel.bundle
				.getString(DOUBLE_CLICK_ANY_ITEM_OR_SELECT_A_TAB_TO_BEGIN);
	}

	@SuppressWarnings("unchecked")
	public void setTypeAndDelivery(final Document sphereDefinition,
			final Document createDefinition, final Hashtable session) {
		if (logger.isDebugEnabled()) {
			logger
					.debug("setTypeAndDeliverysetTypeAndDelivery method performed");
		}

		this.dropDownCreateItem.clearActions();

		final SphereStatement sphere = SphereStatement.wrap(sphereDefinition);

		try {
			String default_type = null;
			String default_delivery = null;

			Vector<String> types = null;
			if (sphereDefinition != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("sphereDefinition is not null for sphere: "
							+ sphere.getDisplayName());
				}
				Vector vec = new Vector(sphereDefinition.getRootElement()
						.element("thread_types").elements());

				default_delivery = sphere.getDefaultDelivery();

				default_type = sphere.getDefaultType();

				if (logger.isDebugEnabled()) {
					logger.debug("Default delivery: " + default_delivery
							+ ", and default type: " + default_type);
				}

				if (default_type != null) {
					this.previousType = VariousUtils
							.firstLetterToUpperCase(default_type);
				}

				boolean ismember = false;
				Vector create = new Vector();
				if (createDefinition != null) {
					try {
						create = new Vector(createDefinition.getRootElement()
								.elements("member"));
					} catch (NullPointerException npe) {
					}
				} else {
					ismember = true;
				}

				for (int i = 0; i < create.size(); i++) {

					Element one = (Element) create.get(i);

					String check = one.attributeValue("contact_name");

					if (check.lastIndexOf((String) session.get("real_name")) != -1) {

						ismember = true;

					}

				}

				if (logger.isDebugEnabled()) {
					logger.debug("Is member?: " + ismember);
				}

				// Temp code for compability with previous databases
				if (sphere.isRoot()) {
					types = getTypesForSupraSphere();
				}

				if ((ismember == true) && (types == null)) {

					if (logger.isDebugEnabled()) {
						logger.debug("Types set from definition size is "
								+ vec.size());
					}

					types = new Vector<String>();

					for (int i = 0; i < vec.size(); i++) {

						Element one = (Element) vec.get(i);

						String modify = one.attributeValue("modify");
						if (modify == null) {
							modify = "null";
						}
						String enabled = one.attributeValue("enabled");
						if (enabled == null) {

							enabled = "null";
						}

						if (enabled.equals("true")) {

							if (!modify.equals("none")) { // &&(one.attributeValue("enabled").equals("true")))
								// {

								if (enabled.equals("member")) {
									String type = one.getName();

									String apath = "//sphere/thread_types/"
											+ type + "/"
											+ "member[@contact_name=\""
											+ (String) session.get("real_name")
											+ "\"]";

									if (logger.isDebugEnabled()) {
										logger.debug("apath in controlPanel:"
												+ apath);
									}

									try {
										Element elem = (Element) sphereDefinition
												.selectObject(apath);

										if (elem == null) {
											logger
													.warn("null element in getsystemname");
										} else {
											type = VariousUtils
													.firstLetterToUpperCase(type);
											if (!types.contains(type)) {
												if (logger.isDebugEnabled()) {
													logger.info("Adding type: "
															+ type);
												}
												types.add(type);
											}
										}
									} catch (Exception ex) {
										logger.error("error with defaul type",
												ex);
									}
								} else {
									String type = one.getName();

									char originalStart = type.charAt(0);
									char upperStart = Character
											.toUpperCase(originalStart);
									type = upperStart + type.substring(1);
									if (!types.contains(type)) {
										if (logger.isDebugEnabled()) {
											logger.info("Adding type: " + type);
										}
										types.add(type);
									}
								}
							} else if (one.attributeValue("enabled") != null) {

								if (one.attributeValue("enabled").equals(
										"member")) {
									String type = one.getName();

									String apath = "//sphere/thread_types/"
											+ type + "/"
											+ "member[@contact_name=\""
											+ (String) session.get("real_name")
											+ "\"]";

									logger.info("apath in controlPanel:"
											+ apath);

									try {
										Element elem = (Element) sphereDefinition
												.selectObject(apath);

										if (elem == null) {
											logger
													.warn("null element in getsystemname");
										} else {

											char originalStart = type.charAt(0);
											char upperStart = Character
													.toUpperCase(originalStart);
											type = upperStart
													+ type.substring(1);

											if (!types.contains(type)) {
												if (logger.isDebugEnabled()) {
													logger.info("Adding type: "
															+ type);
												}
												types.add(type);
											}
										}
									} catch (Exception ex) {
										logger.error(ex);
									}
								}
							}
						}

					}
				}
			} else {

				String sphereId = (String) session.get("sphere_id");
				String sphereName = this.sF.client.getVerifyAuth()
						.getDisplayName(sphereId);

				logger.warn("Sphere definition for sphere name, id: "
						+ sphereName + ", " + sphereId
						+ " is null, adding default set of types");

				types.add(ControlPanel.getTypeTerse());
				types.add(ControlPanel.getTypeMessage());
				types.add(ControlPanel.getTypeBookmark());
				types.add(ControlPanel.getTypeFile());

				if (sphereName.equals((String) session.get("real_name"))) {
					if (logger.isDebugEnabled()) {
						logger
								.info("Sphere name is contact, adding additional types");
					}
					types.add(ControlPanel.getTypeContact());
					types.add(ControlPanel.getTypeSphere());
				}
			}

			if (logger.isDebugEnabled()) {
				logger.info("Type to add in GUI size is " + types.size()
						+ ", performing inserting");
			}
			for (String s : types) {
				this.dropDownCreateItem.addAction(FactoryOfActions.getAction(s, this.mP.getRawSession()));
			}
			String defaultType = SsDomain.SPHERE_HELPER.getSpherePreferences(
					this.mP.getSystemName()).getWorkflowConfiguration()
					.getDefaultType();
			this.dropDownCreateItem.selectActiveAction(defaultType);
			if(defaultType.equals(ControlPanel.getTypeTerse())) {
				setTypeLocked(true);
			}

			if (this instanceof ControlPanel) {
				setDefaultDelivery(sphereDefinition, session, default_delivery,
						types);
			}

		} catch (RuntimeException e) {
			logger.error("runtime exception", e);
		}

		this.parent.redraw();
		this.parent.layout();
		if (logger.isDebugEnabled()) {
			logger
					.debug("setTypeAndDeliverysetTypeAndDelivery method finished");
		}
	}

	/**
	 * @param b
	 */
	public void setTypeLocked(boolean b) {
	}

	/**
	 * @return
	 */
	private Vector<String> getTypesForSupraSphere() {
		logger.warn("Setting types for suprasphre, temporary code!");
		Vector<String> types = new Vector<String>(9);
		types.add(ControlPanel.getTypeTerse());
		types.add(ControlPanel.getTypeMessage());
		types.add(ControlPanel.getTypeBookmark());
		types.add(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		types.add(ControlPanel.getTypeContact());
		types.add(ControlPanel.getTypeRss());
		types.add(ControlPanel.getTypeKeywords());
		types.add(ControlPanel.getTypeFile());
		types.add(ControlPanel.getTypeSphere());
		return types;
	}

	protected void setDefaultDelivery(final Document sphereDefinition,
			final Hashtable session, String default_delivery,
			Vector<String> types) {
		// if (sphereDefinition != null) {
		// if (default_delivery.equals("normal")) {
		// this.deliveryCombo.select(0);
		// } else if(default_delivery.equals("confirm_receipt")) {
		// this.deliveryCombo.select(1);
		// } else if(default_delivery.equals("workflow")) {
		// this.deliveryCombo.select(2);
		// } else {
		// String name = this.sF.getDC((String) session.get("sphereURL"))
		// .getVerifyAuth().getDisplayName(
		// (String) session.get("sphere_id"));
		// if (name.equals((String) session.get("real_name"))) {
		// this.deliveryCombo.select(0);
		// logger.info("setting delivery to one");
		//
		// } else {
		// this.deliveryCombo.select(1);
		// }
		// }
		//
		// if (types.size() != 0) {
		//		    		
		// } else {
		//
		// logger.warn("types was zero after all");
		//
		// }
		// } else {
		// String name = this.sF.getDC((String) session.get("sphereURL"))
		// .getVerifyAuth().getDisplayName(
		// (String) session.get("sphere_id"));
		// if (name.equals((String) session.get("real_name"))) {
		// this.deliveryCombo.select(0);
		//
		// logger.info("setting delivery to one");
		//
		// } else {
		// this.deliveryCombo.select(1);
		//
		// }
		//
		// }
	}

	public MessagesPane getMP() {
		return this.mP;
	}

	/**
	 * 
	 */
	public abstract void setFocusToSendField();

	/**
	 * 
	 */
	public void refreshdeliveryCombo() {
		final WorkflowConfiguration workflow = SsDomain.SPHERE_HELPER.getSpherePreferences(this.mP.getSystemName()).getWorkflowConfiguration();
		
		UiUtils.swtBeginInvoke(new Runnable(){
			Combo deliveryCombo = AbstractControlPanel.this.deliveryCombo;
			public void run() {
				this.deliveryCombo.removeAll();		
				for(AbstractDelivery delivery : workflow.getEnabledDeliveries()) {
					this.deliveryCombo.add(delivery.getDisplayName());
				}
				this.deliveryCombo.select(this.deliveryCombo.indexOf(workflow.getDefaultDelivery().getDisplayName()));
			}
			
		});	
	}

//	/**
//	 * 
//	 */
//	public void returnToDefaultType() {
//		
//	}

	
	public boolean isTypeLocked() {
		return false;
	}
}
