package ss.framework.entities.xmlentities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;
import ss.common.UnexpectedRuntimeException;
import ss.common.XmlDocumentUtils;

public final class XmlEntityUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XmlEntityUtils.class);
	
	private static final String defaulEntityResolverLoaderClassName = "ss.domainmodel.XmlEntityResolverLoader";

	private static XmlEntityObjectFactory factory;

	/**
	 * Returns object model by xml document, that represented object model data
	 */
	public static <T extends XmlEntityObject> T wrap(Document xmlDocument)
			throws CannotCreateEntityException {
		return XmlEntityUtils.<T> wrap(xmlDocument.getRootElement());
	}

	/**
	 * Returns object model by part of xml document, that represented object
	 * model data
	 */
	public static <T extends XmlEntityObject> T wrap(Element element)
			throws CannotCreateEntityException {
		if (element == null) {
			throw new ArgumentNullPointerException("element");
		}
		T entityObject = XmlEntityUtils.<T> createBlank(element);
		bind(element, entityObject);
		return entityObject;
	}

	/**
	 * Returns object model by part of xml document, that represented object
	 * model data
	 */
	public static <T extends XmlEntityObject> T wrap(Element element,
			Class<T> entityObjectClass) throws CannotCreateEntityException {
		if (element == null) {
			throw new ArgumentNullPointerException("element");
		}
		return safeWrap(element, entityObjectClass);
	}

	/**
	 * Returns object model by part of xml document, that represented object
	 * model data
	 */
	public static <T extends XmlEntityObject> T safeWrap(Element element,
			Class<T> entityObjectClass) throws CannotCreateEntityException {
		if (entityObjectClass == null) {
			throw new ArgumentNullPointerException("entityObjectClass");
		}
		try {
			T entityObject = entityObjectClass.newInstance();
			if (element != null) {
				bind(element, entityObject);
			}
			return entityObject;
		} catch (Exception e) {
			throw new CannotCreateEntityException(e);
		}
	}

	/**
	 * Returns object model by part of xml document, that represented object
	 * model data
	 */
	public static <T extends XmlEntityObject> T safeWrap(Document document,
			Class<T> entityObjectClass) throws CannotCreateEntityException {
		return safeWrap(document != null ? document.getRootElement() : null,
				entityObjectClass);
	}

	/**
	 * Returns object model by part of xml document, that represented object
	 * model data
	 */
	public static <T extends XmlEntityObject> T wrap(Document document,
			Class<T> entityObjectClass) throws CannotCreateEntityException {
		if (document == null) {
			throw new ArgumentNullPointerException("document");
		}
		return wrap(document.getRootElement(), entityObjectClass);
	}

	/**
	 * Create object determine object class on the specified element
	 */
	@SuppressWarnings("unchecked")
	public static <T extends XmlEntityObject> T createBlank(Element element)
			throws CannotCreateEntityException {
		if (element == null) {
			throw new ArgumentNullPointerException("element");
		}
		XmlEntityObject entityObject = getFactory().createBlankObject(element);
		return (T) entityObject;
	}

	/**
	 * Create object determine object class on the specified element
	 */
	public static <T extends XmlEntityObject> void bind(Element element,
			T entityObject) throws CannotCreateEntityException {
		if (element == null) {
			throw new ArgumentNullPointerException("element");
		}
		if (entityObject == null) {
			throw new ArgumentNullPointerException("entityObject");
		}
		entityObject.bindTo(new RootXmlElementDataProvider(element));
	}

	/**
	 * Returns xml entity factory. Initilize factory on the first call.
	 */
	@SuppressWarnings("unchecked")
	private static XmlEntityObjectFactory getFactory() {
		if (factory == null) {
			factory = new XmlEntityObjectFactory();
			try {
				Class<IXmlEntityResolverLoader> loaderClass = (Class<IXmlEntityResolverLoader>) Class
						.forName(defaulEntityResolverLoaderClassName);
				IXmlEntityResolverLoader loader;
				loader = loaderClass.newInstance();
				loader.loadTo(factory);
			} catch (Exception ex) {
				throw new UnexpectedRuntimeException(ex);
			}
		}
		return factory;
	}

	/**
	 * @param value
	 */
	public static String entityToString(XmlEntityObject value) {
		if (value == null) {
			return null;
		}
		return XmlDocumentUtils.toPrettyString(value.getDocumentCopy());
	}

	/**
	 * @param value
	 */
	public static <E extends XmlEntityObject> E wrap(String value,
			Class<E> entityObjectClass) {
		Document document = XmlDocumentUtils.parse(value, true);
		return safeWrap(document, entityObjectClass);
	}

	public static <T extends XmlEntityObject> List<T> wrapList(
			List<Element> elements, Class<T> entityObjectClass) {
		List<T> entities = new ArrayList<T>(elements.size());
		for (Element element : elements) {
			entities.add(XmlEntityUtils.wrap(element, entityObjectClass));
		}
		return entities;
	}

	/**
	 * @param resourceAsStream
	 * @param name
	 */
	public static <E extends XmlEntityObject> E safeLoad(InputStream stream, Class<E> entityObjectClazz) {
		if ( stream == null ) {
			logger.warn( "Stream is null" );
			return null;
		}
		try {
			Document doc = XmlDocumentUtils.load(stream);
			return safeWrap( doc, entityObjectClazz);
		} catch (DocumentException ex) {
			logger.error( "Can't load document from stream", ex );
		}
		return null;
	}

	/**
	 * @param configuration
	 */
	public static void safeSave(File file, XmlEntityObject object) {
		if (file == null) {
			throw new ArgumentNullPointerException("targetFile");
		}
		if (object == null) {
			throw new ArgumentNullPointerException("entityObject");
		}
		Document docToSave = object.getBindedDocument();
		if ( docToSave == null ) {
			docToSave = object.getDocumentCopy();
		}
		try {
			XmlDocumentUtils.save(file, docToSave );
		} catch (DocumentException ex) {
			logger.error( "Can't save entity object to file " + file,  ex );
		}
	}

	/**
	 * @param bindedFile
	 * @param configuration
	 */
	public static void safeLoad(File file, XmlEntityObject entity) {
		if (file == null) {
			throw new ArgumentNullPointerException("file");
		}
		if (entity == null) {
			throw new ArgumentNullPointerException("entity");
		}
		final FileInputStream in;
		try {
			in = new FileInputStream( file );
		} catch (FileNotFoundException ex) {
			logger.error( "Can't file target file " + file ,  ex );
			return;
		}
		try {
			Document doc = XmlDocumentUtils.load(in);
			if ( doc != null ) {
				bind(doc.getRootElement(), entity);				
			}
		} catch (Exception ex) {
			logger.error( "Can't load document from " + file,  ex );
		}
		finally {
			try {
				in.close();
			} catch (IOException ex) {
				logger.error( "Can't close file " + file,  ex );
			}
		}
	}
}