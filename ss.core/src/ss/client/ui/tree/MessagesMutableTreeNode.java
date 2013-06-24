package ss.client.ui.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.ui.MessagesPane;
import ss.common.debug.DebugUtils;
import ss.domainmodel.Statement;
import ss.global.SSLogger;

class MessagesMutableTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = -6281111076620082073L;

	private static final Logger logger = SSLogger.getLogger(MessagesMutableTreeNode.class);

    private String title = null;

    private String messageId = null;

    private String responseId = null;

    private boolean added = false;

    private String type = null;
    
    private String status = null;

    private MessagesPane mP = null;

    public MessagesMutableTreeNode(MessagesPane mP, String title,
            String messageId, String responseId, String type, String status) {

        this.mP = mP;
        
        if (mP == null) {
            logger.warn("Constructing MMTN with null object");
        }
        
        this.title = title;
        this.messageId = messageId;

        this.responseId = responseId;
        this.type = type;
        this.status = status;

    }

    public Object getUserObject() {
        try {
            if (this.mP.getDocFromHash(this.messageId) != null) {
                return this.mP.getDocFromHash(this.messageId);
            } else {
                logger.error("HOW ON EARTH IS IT NULL????? :" + this.title+"  "+DebugUtils.getCurrentStackTrace());
                return null;
            }
        } catch (NullPointerException npe) {
            if (this.mP == null) {
                logger.warn("MP WAS NULL");
            }
            logger.error(npe.getMessage(), npe);
            return null;
        }
    }

    public void replaceUserObject(Document newDoc) {
    	Statement statement = Statement.wrap(newDoc);
        try {
            this.mP.replaceDocInHash(this.messageId, statement);
            this.messageId = statement.getMessageId();
        } catch (NullPointerException npe) {

        }

    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getType() {
        return this.type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        try {
            return this.mP.getDocFromHash(this.messageId).getStatus();
        } catch (NullPointerException npe) {
            return null;
        }

    }

    public org.dom4j.Document returnDoc() {
        try {
            return this.mP.getDocFromHash(this.messageId).getBindedDocument();
        } catch (NullPointerException npe) {
            return null;

        }
    }

    public String getResponseId() {
        return this.responseId;
    }

    public String toString() {
        return this.title;
    }

    public void addPresence(String new_title) {
        this.title = new_title;
        this.added = true;
    }

    public void removePresence(String new_title) {
        this.title = new_title;
        this.added = false;
    }

    public boolean getAlreadyAdded() {
        return this.added;
    }

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		this.status = string;
	}
	
	public String getNodeStatus() {
		return this.status;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] childrenAsArray() {
		Enumeration enumeration = children();
		List nodesList = new ArrayList();
		while( enumeration.hasMoreElements() ) {
			Object nextElement = enumeration.nextElement();
			if (nextElement == null) { 
				logger.error("Element in messages tree content is null");
			} else {
				nodesList.add(nextElement);
			}
		}
		return nodesList.toArray();
	}
}