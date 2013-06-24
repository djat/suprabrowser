package ss.common.converter;

import java.io.File;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * The class <CODE>DocumentConverter</CODE> allows you to convert all
 * documents in a given directory and in its subdirectories to a given type. A
 * converted document will be created in the same directory as the origin
 * document.
 * 
 */
class DocumentConvert {
	/**
	 * Containing the loaded documents
	 */
	static XComponentLoader xcomponentloader = null;

	/**
	 * Containing the given type to convert to
	 */
	static String stringConvertType = "";

	/**
	 * Containing the given extension
	 */
	static String stringExtension = "";

	/**
	 * Containing the current file or directory
	 */
	static String indent = "";

	static File file = null;

	private static final Logger logger = SSLogger
			.getLogger(DocumentConvert.class);

	/**
	 * Traversing the given directory recursively and converting their files to
	 * the favoured type if possible
	 * 
	 * @param fileDirectory
	 *            Containing the directory
	 */

	public static void main(String[] args) {
		logger.info("Starting main");
		String filename = null;

		try {
			filename = args[0];
		} catch (Exception e) {

		}
		if (filename == null) {
			filename = "c:\\djathomson.doc";
		}

		File f = new File(filename);
		logger.info("Filename: " + f.getPath());

		DocumentConvert d = new DocumentConvert();

		d.init(f.getPath());
		try {
			d.convert();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void init(String filename) {
		logger.info("Starting");
		file = new File(filename);
		String sConnectionString = "uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager";

		try {
			XComponentContext xComponentContext = com.sun.star.comp.helper.Bootstrap
					.createInitialComponentContext(null);

			/*
			 * Gets the service manager instance to be used (or null). This
			 * method has been added for convenience, because the service
			 * manager is a often used object.
			 */
			XMultiComponentFactory xMultiComponentFactory = xComponentContext
					.getServiceManager();

			/*
			 * Creates an instance of the component UnoUrlResolver which
			 * supports the services specified by the factory.
			 */
			Object objectUrlResolver = xMultiComponentFactory
					.createInstanceWithContext(
							"com.sun.star.bridge.UnoUrlResolver",
							xComponentContext);

			// Create a new url resolver
			XUnoUrlResolver xurlresolver = (XUnoUrlResolver) UnoRuntime
					.queryInterface(XUnoUrlResolver.class, objectUrlResolver);

			// Resolves an object that is specified as follow:
			// uno:<connection description>;<protocol description>;<initial
			// object name>
			Object objectInitial = xurlresolver.resolve(sConnectionString);

			// Create a service manager from the initial object
			xMultiComponentFactory = (XMultiComponentFactory) UnoRuntime
					.queryInterface(XMultiComponentFactory.class, objectInitial);

			// Query for the XPropertySet interface.
			XPropertySet xpropertysetMultiComponentFactory = (XPropertySet) UnoRuntime
					.queryInterface(XPropertySet.class, xMultiComponentFactory);

			// Get the default context from the office server.
			Object objectDefaultContext = xpropertysetMultiComponentFactory
					.getPropertyValue("DefaultContext");

			// Query for the intrerface XComponentContext.
			xComponentContext = (XComponentContext) UnoRuntime.queryInterface(
					XComponentContext.class, objectDefaultContext);

			/*
			 * A desktop environment contains tasks with one or more frames in
			 * which components can be loaded. Desktop is the environment for
			 * components which can instanciate within frames.
			 */
			xcomponentloader = (XComponentLoader) UnoRuntime.queryInterface(
					XComponentLoader.class, xMultiComponentFactory
							.createInstanceWithContext(
									"com.sun.star.frame.Desktop",
									xComponentContext));

			// Getting the given starting directory

			// Getting the given type to convert to

			if (filename.toLowerCase().endsWith("doc")
					|| filename.toLowerCase().endsWith("odt")) {
				// System.out.println("Here");
				stringConvertType = "writer_pdf_Export";

			} else if (filename.toLowerCase().endsWith("ppt")
					|| filename.toLowerCase().endsWith("odp")) {

				stringConvertType = "impress_pdf_Export";

			} else if (filename.toLowerCase().endsWith("xls")
					|| filename.toLowerCase().endsWith("sxc")) {

				stringConvertType = "calc_pdf_Export";

			} else if (filename.toLowerCase().endsWith("rtf")) {

				stringConvertType = "writer_pdf_Export";
				stringExtension = "rtf";

			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

		// Starting the conversion of documents in the given directory and
		// subdirectories
		// convert();
		logger.info("Done init");

	}

	public void convert() throws Exception {
		try {
			// Composing the URL by replacing all backslashs

			String stringUrl = "file:///"
					+ file.getAbsolutePath().replace('\\', '/');

			// Loading the wanted document

			Object objectDocumentToStore = DocumentConvert.xcomponentloader
					.loadComponentFromURL(stringUrl, "_blank", 0,
							new PropertyValue[0]);

			// Getting an object that will offer a simple way to store a
			// document to a URL

			XStorable xstorable = (XStorable) UnoRuntime.queryInterface(
					XStorable.class, objectDocumentToStore);

			// Preparing properties for converting the document
			PropertyValue propertyvalue[] = new PropertyValue[2];
			// Setting the flag for overwriting
			// propertyvalue[ 0 ] = new PropertyValue();
			// propertyvalue[ 0 ].Name = "Overwrite";
			// propertyvalue[ 0 ].Value = new Boolean(true);
			// Setting the filter name
			propertyvalue[1] = new PropertyValue();
			propertyvalue[1].Name = "CompressionMode";
			propertyvalue[1].Value = "1";
			propertyvalue[0] = new PropertyValue();
			propertyvalue[0].Name = "FilterName";

			// propertyvalue[ 0 ].Value = "Text"; // This is to convert to
			// TEXT....switch comments to switch convert type

			propertyvalue[0].Value = DocumentConvert.stringConvertType; // This
																		// is to
																		// convert
																		// to
																		// PDF....switch
																		// comments
																		// to
																		// switch
																		// convert
																		// type
			// propertyvalue[ 0 ].Value = "writer_pdf_Export";

			// propertyvalue[ 0 ].Value = "HTML (StarWriter)";
			stringUrl = stringUrl + "." + "pdf";

			// Storing and converting the document
			if (!DocumentConvert.stringConvertType.equals("none")) {

				xstorable.storeToURL(stringUrl, propertyvalue);

				// Getting the method dispose() for closing the document
				XComponent xcomponent = (XComponent) UnoRuntime.queryInterface(
						XComponent.class, xstorable);

				// Closing the converted document
				xcomponent.dispose();
			}
		} catch (Exception exception) {
			logger.error(exception.getMessage(), exception);
		}

	}

	/**
	 * Connecting to the office with the component UnoUrlResolver and calling
	 * the static method traverse
	 * 
	 * @param args
	 *            The array of the type String contains the directory, in which
	 *            all files should be converted, the favoured converting type
	 *            and the wanted extension
	 */

}
