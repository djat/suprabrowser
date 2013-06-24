/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zobo
 * 
 */
public class ResearchInfoController {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ResearchInfoController.class);

	public static final ResearchInfoController INSTANCE = new ResearchInfoController();

	private ResearchComponentDataContainer initial = null;

	private final Object mutex = new Object();

	private List<ReSearchToolItemComponent> components;

	private ResearchInfoController() {
		this.components = new ArrayList<ReSearchToolItemComponent>();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void fillInitialProvider(final ResearchGUIInfoProvider provider) {
		checkProvider(provider);
		if (checkInitial())
			return;
		provider.setLookInOthers(this.initial.isLookInOthers());
		provider.setLookInOwn(this.initial.isLookInOwn());
		provider.setNewFromLastResearch(this.initial.isNewFromLastResearch());
	}

	public ResearchComponentDataContainer getDataProvider() {
		synchronized (this.mutex) {
			if (this.initial == null) {
				load();
			}
			return this.initial;
		}
	}

	public void setDataProvider(final ResearchComponentDataContainer data) {
		if (data == null) {
			throw new NullPointerException("Data for research can not be null");
		}
		synchronized (this.mutex) {
			this.initial = data;
		}
		updateRegister();
	}

	private boolean checkInitial() {
		if (this.initial == null) {
			load();
		}
		if (this.initial == null) {
			logger.error("Can not obtain initial information");
			return true;
		}
		return false;
	}

	/**
	 * @param provider
	 */
	private void checkProvider(ResearchGUIInfoProvider provider) {
		if (provider == null) {
			throw new NullPointerException(
					"ResearchInfoProvider can not be null");
		}
	}

	private void load() {
		// TODO implement

		this.initial = new ResearchComponentDataContainer(true, false, false,
				null, false, 10, 10, true, 3);
	}

	public void register(final ReSearchToolItemComponent component) {
		synchronized (this.components) {
			if (component == null) {
				logger.error("Can not register null component");
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Component registered");
			}
			if (!this.components.contains(component)) {
				this.components.add(component);
			}
		}
	}

	public void unregister(final ReSearchToolItemComponent component) {
		synchronized (this.components) {
			if (component == null) {
				logger.error("Can not unregister null component");
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Component unregistered");
			}
			this.components.remove(component);
		}
	}

	private void updateRegister() {
		synchronized (this.components) {
			List<ReSearchToolItemComponent> toErase = null;
			for (ReSearchToolItemComponent component : this.components) {
				if (component.isDead()) {
					if (toErase == null) {
						toErase = new ArrayList<ReSearchToolItemComponent>();
					}
					toErase.add(component);
				} else {
					component.getReseachState().update(getDataProvider());
				}
			}
			if (toErase != null) {
				this.components.removeAll(toErase);
			}
		}
	}

	/**
	 * @param returnedContainer
	 */
	public void updateDataRecieved(ResearchComponentDataContainer returnedContainer) {
		if ( returnedContainer == null ) {
			logger.error("Container is null");
			return;
		}
		synchronized (this.mutex) {
			this.initial.setContactStrings( returnedContainer.getContacts() );
		}
	}
}
