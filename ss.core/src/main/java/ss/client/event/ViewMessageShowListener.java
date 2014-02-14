/**
 * 
 */
package ss.client.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.viewers.ViewMessageSWT;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.Statement;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ViewMessageShowListener implements SelectionListener {

	ViewMessageSWT vm;
	
	private static final Logger logger = SSLogger.getLogger(ViewMessageShowListener.class);
	
	public ViewMessageShowListener(ViewMessageSWT vm) {
		this.vm = vm;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	
	public void widgetSelected(SelectionEvent arg0) {

        this.vm.addSaveXMLDocButton();
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {   
            File file = new File(this.vm.getViewStatement().getMessageId());
            
                  FileOutputStream fout = new FileOutputStream(file);
                  XMLWriter writer = new XMLWriter(fout, format);
                  if(this.vm.getViewDoc()==null) {
                	  Statement statement = this.vm.getMessagesPane().getDocFromHash(this.vm.getViewStatement().getMessageId());
                	  this.vm.getBodyEditor().setText(XmlDocumentUtils.toPrettyString(statement.getBindedDocument()));
                	  return;
                  }
                  writer.write(this.vm.getViewDoc());
                  writer.close();
                  fout.close();

                  SAXReader reader1 = new SAXReader();
                  Document doc = reader1.read(file);
                  
                  file.delete();
                  String originDoc = XmlDocumentUtils.toPrettyString(doc);
                  this.vm.getBodyEditor().setText(originDoc);
          
        } catch (UnsupportedEncodingException e1) {
          logger.error(e1.getMessage(), e1);
        }
        catch (IOException ioe) {
          logger.error(ioe.getMessage(), ioe);
        } 
        catch (DocumentException de) {
          logger.error(de.getMessage(), de);
        }
      

	}

}
