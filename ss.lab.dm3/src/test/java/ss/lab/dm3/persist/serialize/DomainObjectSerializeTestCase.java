package ss.lab.dm3.persist.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Sphere;

public class DomainObjectSerializeTestCase extends AbstractDomainTestCase {

	private byte[] saveSphere;
	
	private Sphere sphere;
	
	public void test() throws Exception {
		Domain domain = getDomain();
		save( domain );
		load( domain );
	}
	
	public void save(Domain domain) throws Exception {
		this.sphere = domain.resolve( Sphere.class, 1L );
		assertEquals( "Sphere_Display#1", this.sphere.getDisplayName() );
		this.saveSphere = save( this.sphere );
	}
	
	public void load(Domain domain) throws Exception  {
		Sphere loadedSphere = (Sphere) load( this.saveSphere );
		assertEquals( "Sphere_Display#1", loadedSphere.getDisplayName() );
		assertSame( this.sphere, loadedSphere );
	}
	
	private static Object load( byte[] bytes ) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream inArr = new ByteArrayInputStream( bytes );
		ObjectInputStream in = new ObjectInputStream(inArr);
		final Object readObject = in.readObject();
		in.close();		
		return readObject;
	}

	/**
	 * @param myObject
	 * @return
	 * @throws IOException
	 */
	private static byte[] save(Object myObject) throws IOException {
		final ByteArrayOutputStream outArr = new ByteArrayOutputStream(1000);
		ObjectOutputStream os = new ObjectOutputStream(outArr);
		os.writeObject( myObject );
		os.close();
		byte[] bytes = outArr.toByteArray();
		return bytes;
	}
	
}
