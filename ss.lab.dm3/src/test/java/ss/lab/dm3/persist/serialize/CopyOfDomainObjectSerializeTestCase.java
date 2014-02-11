package ss.lab.dm3.persist.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import ss.lab.dm3.utils.ReflectionHelper;

public class CopyOfDomainObjectSerializeTestCase {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		final ByteArrayOutputStream outArr = new ByteArrayOutputStream(1000);
		ObjectOutputStream os = new ObjectOutputStream(outArr);
		MyObject myObject = new MyObject2( "Hello from 2");
		os.writeObject( myObject );
		os.reset();
		myObject.setTitle( "Hello from 3" );
		os.writeObject( myObject );
		os.close();
		byte[] bytes = outArr.toByteArray();
		System.out.println( bytes.length );
		
		final ByteArrayInputStream inArr = new ByteArrayInputStream( bytes );
		ObjectInputStream in = new ObjectInputStream(inArr);
		MyObject myObject1 = (MyObject) in.readObject();
		MyObject myObject2 = (MyObject) in.readObject();
		System.out.println( myObject1 );
		System.out.println( myObject2 );
		in.close();		
	}

	public static class MyObject2 extends MyObject {

		/**
		 * @param title
		 */
		public MyObject2(String title) {
			super(title);
		}

		/**
		 * 
		 */
		public MyObject2() {
			super();
		}
		
	}

	public static class MyObject implements Serializable {

		private String title;

		/**
		 * @param title
		 */
		public MyObject(String title) {
			super();
			this.title = title;
		}

		/**
		 * 
		 */
		public MyObject() {
			super();
		}

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		protected Object writeReplace() throws ObjectStreamException {
			return new MyObjectProxy(getClass(), this.title);
		}

		@Override
		public String toString() {
			return hashCode() + "_" + getClass().getSimpleName() + ":" + this.title;
		}
	}

	public static class MyObjectProxy implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5645247246547102930L;

		private final Class<?> objectClazz;

		private final String title;

		/**
		 * @param title
		 */
		public MyObjectProxy(Class<?> objectClazz, String title) {
			super();
			this.objectClazz = objectClazz;
			this.title = title;
		}

		private Object readResolve() throws ObjectStreamException {
			MyObject obj = (MyObject) ReflectionHelper.create( this.objectClazz );
			obj.setTitle( this.title + " from proxy" );
			return obj;
		}

	}
}
