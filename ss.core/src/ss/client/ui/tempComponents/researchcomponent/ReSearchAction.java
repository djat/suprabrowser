/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.ui.SupraSphereFrame;
import ss.common.ListUtils;
import ss.common.UiUtils;

/**
 * @author zobo
 * 
 */
class ReSearchAction {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReSearchAction.class);

	private final ReSearchToolItemComponent component;
	
	private Hashtable assetsWithKeywordTag;
	
	private Vector highlightKeywordsDocs;

	private ResearchComponentDataContainer returnedContainer;

	ReSearchAction(final ReSearchToolItemComponent component) {
		super();
		this.component = component;
	}

	void action ( ) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				perform();
			}
		});
	}

	private void perform() {
		if (logger.isDebugEnabled()) {
			logger.debug("Clicked item ReSearch");
		}
		clear();

		final Vector<String> highlightKeywords = new Vector<String>();
		for (int i = 0; i < getHighlightKeywordsDocs().size(); i++) {

			Document doc = (Document) getHighlightKeywordsDocs().get(i);

			String keyword = doc.getRootElement().element("subject")
					.attributeValue("value");

			if (logger.isDebugEnabled()) {
				logger.debug("Keyword: " + keyword);
			}
	
			Vector taggedItems = (Vector) getAssetsWithKeywordTag().get(keyword);
			if (taggedItems != null) {
				for (int j = 0; j < taggedItems.size(); j++) {

					Document document = (Document) taggedItems.get(j);

					String subject = document.getRootElement().element("subject")
						.attributeValue("value");

					logger.info("keyword: " + keyword + " : tagged items: "
						+ subject);

				}
			}

			highlightKeywords.add(keyword);

		}
		
		if (getHighlightKeywordsDocs().size() <= 0) {
			logger.warn("No keywords found");
		} else {
			ResearchInfoController.INSTANCE.updateDataRecieved( getReturnedContainer() );
			this.component.getBrowser().getMozillaBrowserController().highlightKeywords(
					getHighlightKeywordsDocs(), getAssetsWithKeywordTag(), true, getReturnedContainer());
		}
	}

	private void clear() {
		this.assetsWithKeywordTag = null;
		this.highlightKeywordsDocs = null;
	}

	private Hashtable getAssetsWithKeywordTag() {
		if (this.assetsWithKeywordTag == null) {
			load();
		}
		return this.assetsWithKeywordTag;
	}

	private Vector getHighlightKeywordsDocs() {
		if (this.highlightKeywordsDocs == null) {
			load();
		}
		return this.highlightKeywordsDocs;
	}
	
	private void load() {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading data for highlighting");
		}
		loadPerform();
		if (this.assetsWithKeywordTag == null) {
			this.assetsWithKeywordTag = new Hashtable();
		}
		if (this.highlightKeywordsDocs == null) {
			this.highlightKeywordsDocs = new Vector();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Result");
			logger.debug("Keywords: " + ListUtils.allValuesToString( this.highlightKeywordsDocs ));
			logger.debug("Docs size: " + this.assetsWithKeywordTag.size());
		}
	}

	private void loadPerform() {
		this.assetsWithKeywordTag = new Hashtable();
		this.highlightKeywordsDocs = new Vector();
//		if (this.component.getReseachState().isOwnKeywords()) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("Look in my keywords checked, performing...");
//			}
//			addData( SupraSphereFrame.INSTANCE.client
//					.matchAgainstHistoryForHighlight(SupraSphereFrame.INSTANCE.client
//							.getSession()) );
//		}
//		if (this.component.getReseachState().isOthersKeywords()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Look in others keywords checked, performing...");
			}
			ResearchComponentDataContainer data = (ResearchComponentDataContainer) ResearchInfoController.INSTANCE.getDataProvider().clone();
			data.setLookInOthers(this.component.getReseachState().isOthersKeywords());
			data.setLookInOwn(this.component.getReseachState().isOwnKeywords());
			data.setNewFromLastResearch(this.component.getReseachState().isNewFromLastResearch());
			addData( SupraSphereFrame.INSTANCE.client
					.matchAgainstOtherHistoryForHighlight(SupraSphereFrame.INSTANCE.client
							.getSession(), data ) );
//		}
	}
	
	private void addData( final Hashtable resultsContainer ){
		if (resultsContainer == null) {
			logger.warn("resultsContainer is null");
			return;
		}
		this.assetsWithKeywordTag = (Hashtable) resultsContainer.get("assetsWithKeywordTag");
		this.highlightKeywordsDocs = (Vector) resultsContainer.get("highlightKeywords");
		this.returnedContainer = (ResearchComponentDataContainer) resultsContainer.get("datacontainer");
//		if (resultsContainer.get("assetsWithKeywordTag") != null) {
//			Hashtable hashtable = ((Hashtable)resultsContainer.get("assetsWithKeywordTag"));
//			for (Object o : hashtable.keySet()) {
//				if (!this.assetsWithKeywordTag.containsKey(o)) {
//					this.assetsWithKeywordTag.put(o, hashtable.get(o));
//				}
//			}
//		}
//		if (resultsContainer.get("highlightKeywords") != null) {
//			Vector vector = (Vector) resultsContainer.get("highlightKeywords");
//			for (Object o : vector) {
//				if (!this.highlightKeywordsDocs.contains(o)) {
//					this.highlightKeywordsDocs.add(o);
//				}
//			}
//		}
	}

	public ResearchComponentDataContainer getReturnedContainer() {
		return this.returnedContainer;
	}
}
