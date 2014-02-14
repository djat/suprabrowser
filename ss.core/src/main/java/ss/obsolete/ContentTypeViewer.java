package ss.obsolete;

/*

 A template for almost all asset types. It just enforces that certain variables
 and methods will be available.

 */

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.viewers.ContentTypeViewable;
import ss.global.SSLogger;

public abstract class ContentTypeViewer extends JFrame implements
        ContentTypeViewable {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ContentTypeViewer.class);
    String threshold = null;
    
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_CONTENTTYPEVIEWER);

    protected Hashtable session = new Hashtable();

    protected MessagesPane mP = null;

    // ContentTypeViewer typeWindow = null;

    String model_path = null;

    String another = null;

    protected Document viewDoc = null;

    boolean isFillMessage = true;
    
    private static final String YES = "CONTENTTYPEVIEWER.YES";
    private static final String NO = "CONTENTTYPEVIEWER.NO";
    private static final String WARNING = "CONTENTTYPEVIEWER.WARNING";
    private static final String YOU_HAVE_NOT_SAVED_YOUR_MESSAGE_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW 
    	     = "CONTENTTYPEVIEWER.ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW";

    public ContentTypeViewer() {
    	logger.error("yyyyyyyyyyyy");
        this.addWindowListener(new WindowAdapter() {
            private ContentTypeViewer typeWindow = ContentTypeViewer.this;

            public void windowClosing(WindowEvent e) {
                if (!this.typeWindow.isFillMessage) {

                    Object[] options = { ContentTypeViewer.this.bundle.getString(ContentTypeViewer.YES)
                    		, ContentTypeViewer.this.bundle.getString(ContentTypeViewer.NO) };

                    final int YES = 0;

                    if (YES == JOptionPane
                            .showOptionDialog(
                                    null,
                                    ContentTypeViewer.this.bundle.getString(ContentTypeViewer
                                    		.YOU_HAVE_NOT_SAVED_YOUR_MESSAGE_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW),
                                    ContentTypeViewer.this.bundle.getString(ContentTypeViewer.WARNING), JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE, null, options,
                                    options[0])) {
                        this.typeWindow.dispose();

                    } else {

                    }
                }

            }
        });

    }

    public abstract void giveBodyFocus();

    public void setFillMessage(boolean trueFalse) {
        if (trueFalse == false) {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        } else {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        this.isFillMessage = trueFalse;

    }

    Document XMLDoc() {

        Document doc = null;
        return doc;

    }

    public void disposeAll() {

    }

    /**
     * @return Returns the session.
     */
    public Hashtable getSession() {
        return this.session;
    }

    /**
     * @return Returns the mP.
     */
    public MessagesPane getMessagesPane() {
        return this.mP;
    }

    /**
     * @return Returns the viewDoc.
     */
    public Document getViewDoc() {
        return this.viewDoc;
    }

    /**
     * @param viewDoc The viewDoc to set.
     */
    public void setViewDoc(Document viewDoc) {
        this.viewDoc = viewDoc;
    }

}
