package ss.lab.dm3.blob.backend;

import java.io.InputStream;
import java.io.OutputStream;

import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 */
public interface IBlobStreamProvider {

	InputStream openRead(QualifiedObjectId<?> resourceId);
	
	OutputStream openWrite(QualifiedObjectId<?> resourceId);
	
	void delete(QualifiedObjectId<?> resourceId);
	
}
