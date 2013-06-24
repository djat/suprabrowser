package ss.server.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

public class VolatileClassLoader {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VolatileClassLoader.class);

	/**
	 * 
	 */
	public static final String SS_SERVER_DEBUG_VOLATILECOMMANDS = "ss.server.debug.commands.";

	private final Hashtable<String, LoadedClass> classes = new Hashtable<String, LoadedClass>();

	public final static VolatileClassLoader INSTANCE = new VolatileClassLoader();

	private CustomClassLoader customClassLoader = new CustomClassLoader();
	
	private VolatileClassLoader() {
	}

	public synchronized Class<?> loadClass(String className )
			throws ClassNotFoundException {
		final LoadedClass cashedClass = this.classes.get(className);
		if (cashedClass != null ) {
			if ( cashedClass.isOutOfDate() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Loaded class is out of data. Reset class loader" );
				}
				this.customClassLoader = new CustomClassLoader();
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Use cashed class " + cashedClass);
				}
				return cashedClass.getClazz();
			}
		}
		Class<?> cl = null;
		/*
		 * Use default class loaded for not allowed packages.
		 */
		if ( !isVolatile( className ) ) {
			return Class.forName(className);
		}
		// Try to use custom load
		String fileName = getClassFileName(className);
		final byte[] bytes = findClassBytes(fileName);
		if (bytes != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Try to define class ");
			}
			try {
				cl = volatileDefineClass(className, bytes, 0,
						bytes.length);
				LoadedClass loadedClass = new LoadedClass(fileName, cl);
				this.classes.put(className, loadedClass);
			} catch (Throwable ex) {
				logger.error("Can't define class " + className, ex);
			}
		} else {
			// Use default class loader
			cl = Class.forName(className);
		}
		if (cl == null) {
			throw new ClassNotFoundException(className);
		}
		return cl;
	}

	/**
	 * @param string
	 * @param bytes
	 * @param i
	 * @param length
	 * @return
	 */
	private Class<?> volatileDefineClass(String name, byte[] buff, int off, int length) {
		return this.customClassLoader.volatileDefineClass(name, buff, off, length);
	}

	/**
	 * @param className
	 * @return
	 */
	private byte[] findClassBytes(String fileName) {
		final File file = new File(fileName);
		if (file.exists()) {
			try {
				final FileInputStream in = new FileInputStream(file);
				try {
					final byte[] buff = new byte[in.available()];
					in.read(buff);
					return buff;
				} catch (IOException ex) {
					logger.error("Can't read from file " + file, ex);
				} finally {
					try {
						in.close();
					} catch (IOException ex) {
						logger.error("Can't close file " + file, ex);
					}
				}
			} catch (FileNotFoundException ex) {
				logger.error("File not found " + file, ex);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("File not found " + fileName);
			}
		}
		return null;
	}

	/**
	 * @param className
	 * @return
	 */
	private String getClassFileName(String className) {
		return "./build/" + className.replace('.', '/') + ".class";
	}

	/**
	 * @param name
	 */
	public boolean isVolatile(String name) {
		return name.startsWith(SS_SERVER_DEBUG_VOLATILECOMMANDS);
	}	 

	private class CustomClassLoader extends ClassLoader {
		Class<?> volatileDefineClass( String name, byte[] buff, int off, int len ) {
			return super.defineClass(name, buff, off, len);
		}
	}
	
	static class LoadedClass {

		private final String fileName;

		private final long fileSize;

		private final long lastModified;

		private Class<?> clazz;

		/**
		 * @param fileSize
		 * @param lastModified
		 */
		public LoadedClass(String fileName, Class<?> clazz) {
			super();
			this.fileName = fileName;
			this.clazz = clazz;
			final File file = new File(fileName);
			this.fileSize = file.length();
			this.lastModified = file.lastModified();
		}

		public boolean isOutOfDate() {
			final File file = new File(this.fileName);
			return this.fileSize != file.length()
					|| this.lastModified != file.lastModified();
		}

		public Class<?> getClazz() {
			return this.clazz;
		}

	}

}
