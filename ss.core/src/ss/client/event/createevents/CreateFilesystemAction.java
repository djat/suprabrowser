/**
 * 
 */
package ss.client.event.createevents;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.common.GenericXMLDocument;
import ss.common.UiUtils;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateFilesystemAction extends CreateAbstractAction {

    private static Image image;

    public static final String FILESYSTEM_TITLE = "Filesystem";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateFilesystemAction.class);
    
    private Hashtable session = null;
    /**
     * 
     */
    public CreateFilesystemAction(Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.FILE_SYSTEM).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    @Override
    public void performImpl() {

        String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);
        logger.info("selected type " + selectedType);
      
        final Display disp = SDisplay.display.get();
        Thread t = new Thread() {
            private CreateFilesystemAction action = CreateFilesystemAction.this;
            
            public void run() {

                Shell shell = new Shell(disp);
                // shell.open ();
                DirectoryDialog dialog = new DirectoryDialog(shell);
                // dialog.setFilterPath ("c:\\"); //Windows specific
                // System.out.println ("RESULT=" + dialog.open ());
                String result = dialog.open();
                File resFile = new File(result);
                boolean isDir = false;

                if (resFile.isDirectory()) {
                    isDir = true;
                }

                Document doc = GenericXMLDocument.XMLDoc(result, "", (String) this.action.session
                        .get("real_name"));

                if (isDir) {

                    doc.getRootElement().addElement("type").addAttribute(
                            "value", "filesystem");

                    doc.getRootElement().addElement("thread_type")
                            .addAttribute("value", "filesystem");
                    doc.getRootElement().addElement("status").addAttribute(
                            "value", "confirmed");
                    doc.getRootElement().addElement("confirmed")
                            .addAttribute("value", "true");

                    String fsep = System.getProperty("file.separator");

                    if (fsep.lastIndexOf("\\") != -1) {

                        fsep = "backwards";

                    } else {
                        fsep = "forwards";

                    }

                    doc.getRootElement().addElement("file_separator")
                            .addAttribute("value", fsep);
                    doc.getRootElement().addElement("physical_location")
                            .addAttribute("value",
                                    (String) this.action.session.get("profile_id"));

                    SupraSphereFrame.INSTANCE.getDC((String) this.action.session.get("sphereURL"))
                            .publishTerse(this.action.session, doc);

                }

                while (!shell.isDisposed()) {
                    if (!disp.readAndDispatch())
                        disp.sleep();
                }
                disp.dispose();
            }
        };
        UiUtils.swtBeginInvoke(t);
        
        super.performImpl();
    }
    public String getName() {
        return FILESYSTEM_TITLE;
    }
    public Image getImage() {
        return image;
    }

}
