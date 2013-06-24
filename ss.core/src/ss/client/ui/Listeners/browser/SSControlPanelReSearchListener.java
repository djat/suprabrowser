/**
 * 
 */
package ss.client.ui.Listeners.browser;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.common.UiUtils;

/**
 * @author zobo
 *
 */
public class SSControlPanelReSearchListener implements SelectionListener{

    @SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SSControlPanelReSearchListener.class);
    
    private SupraBrowser mb = null;

    public SSControlPanelReSearchListener(SupraBrowser mb) {
        super();
        this.mb = mb;
    }

    public void widgetSelected(SelectionEvent arg0) {
    	UiUtils.swtBeginInvoke(new Runnable() {
    		
    	@SuppressWarnings("unchecked")
		public void run() {
        logger.info("Clicked item ReSearch");
        // TODO implement
        
        Hashtable resultsContainer = SupraSphereFrame.INSTANCE.client.matchAgainstHistoryForHighlight(SSControlPanelReSearchListener.this.mb.getMozillaBrowserController().getCurrentSession());
        
        Hashtable assetsWithKeywordTag = (Hashtable)resultsContainer.get("assetsWithKeywordTag");
        Vector highlightKeywordsDocs = (Vector)resultsContainer.get("highlightKeywords");
        
        Vector highlightKeywords = new Vector(); 
        for (int i=0;i<highlightKeywordsDocs.size();i++) {
        	
        	Document doc = (Document) highlightKeywordsDocs.get(i);

			String keyword = doc.getRootElement().element("subject")
					.attributeValue("value");
        	
        	logger.info("Keyword: "+keyword);
        	Vector taggedItems = (Vector)assetsWithKeywordTag.get(keyword);
        	for (int j=0;j<taggedItems.size();j++) {
        
        		Document document = (Document) taggedItems.get(j);

    			String subject = document.getRootElement().element("subject")
    					.attributeValue("value");
            
        		logger.info("keyword: "+keyword+ " : tagged items: "+subject);
        		
        	}
        	
        	highlightKeywords.add(keyword);
        	
        }
        
        //for (Enumeration enumer = keywords.keys();enumer.hasMoreElements();) {
        //	String one = (String)enumer.nextElement();
        //	logger.info("One Keyword: "+one);
        	
        //}
        //logger.info("Got back: "+keywords.size());
        	
    		SSControlPanelReSearchListener.this.mb.getMozillaBrowserController().highlightKeywords(highlightKeywordsDocs,assetsWithKeywordTag,false,null);
    		//mb.execute("javascript:testJS();");	
    		
    	}
    	});
    	
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}
