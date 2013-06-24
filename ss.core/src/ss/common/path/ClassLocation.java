/**
 * 
 */
package ss.common.path;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import ss.common.PathUtils;

/**
 * 
 */
public class ClassLocation {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClassLocation.class);

	private final Class clazz;

	private final Class compilationUnitClazz;

	private final String compilationUnitPath;

	private final URI compilationUnitUri;

	private final boolean jar;


	/**
	 * @param clazz
	 * @param compilationUnitClazz
	 * @param compilationUnitPath
	 * @param compilationUnitUri
	 * @param jar
	 */
	public ClassLocation(final Class clazz, final Class compilationUnitClazz,
			final String compilationUnitPath, URI compilationUnitUri,
			final boolean jar) {
		super();
		this.clazz = clazz;
		this.compilationUnitClazz = compilationUnitClazz;
		this.compilationUnitPath = compilationUnitPath;
		this.compilationUnitUri = compilationUnitUri;
		this.jar = jar;
	}

	/**
	 * @return the clazz
	 */
	public Class getClazz() {
		return this.clazz;
	}

	/**
	 * @return the compilationUnitPath
	 */
	public String getCompilationUnitPath() {
		return this.compilationUnitPath;
	}

	public boolean isJar() {
		return this.jar;
	}

	public String getBaseFolder() {
		if (isJar()) {
			return PathUtils.getParentFolder(this.compilationUnitPath);
		} else {
			return this.compilationUnitPath;
		}
	}

	/**
	 * @return the compilationUnitClazz
	 */
	public Class getCompilationUnitClazz() {
		return this.compilationUnitClazz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.compilationUnitPath;
	}

	public InputStream getResourceAsStream(String resorce) {
		if (resorce == null) {
			return null;
		}
		if (resorce.startsWith(String.valueOf(PathUtils.UNIFIED_SLASH))) {
			final StringBuilder urlBuilder = new StringBuilder();
			if (this.jar) {
				urlBuilder.append(ClassLocationBuilder.JAR_PREFIX);
			}
			urlBuilder.append(this.compilationUnitUri.toString());
			if (this.jar) {
				urlBuilder.append(ClassLocationBuilder.JAR_SUFFIX);
			}
			urlBuilder.append(resorce);
			final String urlStr = urlBuilder.toString();
			if (logger.isDebugEnabled()) {
				logger.debug("Url str is " + urlStr);
			}
			URL url;
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException ex) {
				logger.error("Can't create resource url " + urlStr, ex);
				return null;
			}
			try {
				return url.openStream();
			} catch (IOException ex) {
				logger.error("Can't open stream for " + url, ex);
				return null;
			}
		} else {
			return this.compilationUnitClazz.getResourceAsStream(resorce);
		}
	}

}
