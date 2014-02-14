/**
 * 
 */
package ss.client.ui.email;

import java.io.InputStream;

import javax.activation.DataSource;

/**
 * @author roman
 *
 */
public interface IAttachedFile {

	String getName();
	
	int getSize();
	
	InputStream createDataStream();
	
	DataSource createDataSource();
}
