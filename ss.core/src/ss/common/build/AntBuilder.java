/*
 * Created on Jun 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.common.build;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.SupraSphereFrame;

public class AntBuilder {

	public void setSrcDir(String srcDir) {
	}

	public void setBaseAndSrcDirs() {
	}

	public void removeOtherPlatformSWTJar() {
	}

	public void replaceLibraryInRunPath(String oldName, String newName) {
	}

	public void addToRunPath(String newName) {
	}

	public void replaceSupraJarname(String filename) {
	}

	public void replacePort(String newPort) {
	}

	public void setPort(String port) {
	}

	public void setSF(SupraSphereFrame sF) {
	}

	public void runOnly(final SupraSphereFrame sF, boolean autoLogin) {
	}

	public void startBuild(String passArg, boolean passThread,
			final boolean exit) {
	}

}

class AntBuilderOld {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(AntBuilder.class);

	String fsep = System.getProperty("file.separator");

	String bdir = System.getProperty("user.dir");

	private SupraSphereFrame sF = null;

	public void setSrcDir(String srcDir) {

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"src\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			elem.addAttribute("value", srcDir);

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void setBaseAndSrcDirs() {

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"src\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			elem.addAttribute("value", "ss");

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void removeOtherPlatformSWTJar() {

		String os = System.getProperty("os.name");
		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			String text = doc.asXML();
			if (os.startsWith("Win")) {

				text = text.replaceAll("swt.jar", "swtwin.jar");
				File out = new File(this.bdir + this.fsep + "build.xml");
				FileOutputStream fout = new FileOutputStream(out);
				byte[] b = text.getBytes();
				fout.write(b);

			} else if (os.startsWith("Lin")) {

				/*
				 * text = text.replaceAll("swtwin.jar","swt.jar"); File out =
				 * new File(bdir+fsep+"build.xml"); FileOutputStream fout = new
				 * FileOutputStream(out); byte[] b = text.getBytes();
				 * fout.write(b);
				 */

			}

		} catch (Exception e) {

		}

	}

	public void replaceLibraryInRunPath(String oldName, String newName) {

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"runpath\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			String path = elem.attributeValue("value");
			path = path.replace(oldName, newName);
			elem.addAttribute("value", path);

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void addToRunPath(String newName) {

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"runpath\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			String path = elem.attributeValue("value");
			path = path + ";${library}/" + newName;

			elem.addAttribute("value", path);

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void replaceSupraJarname(String filename) {

		logger.info("REPLACE SUPRAJARNAME: " + filename);

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"jarname\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);

			elem.addAttribute("value", filename);

			try {
				String jaronlyxpath = "//project/property[@name=\"jaronly\"]";
				logger.info("xpath in builder: " + xpath);

				Element jaronlyelem = (Element) doc.selectObject(jaronlyxpath);

				jaronlyelem.addAttribute("value", filename);

			} catch (Exception e) {
				// Must not have the jaronly attribute
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public void replacePort(String newPort) {

		logger.info("REPLACING PORT");

		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);
			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"port\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			elem.addAttribute("value", newPort);

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error("Can't replace port in build.xml", e);
		}

	}

	public void setPort(String port) {
		File buildFile = new File(this.bdir + this.fsep + "build.xml");

		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(buildFile);

			doc.getRootElement().addAttribute("basedir", this.bdir);

			String xpath = "//project/property[@name=\"port\"]";
			logger.info("xpath in builder: " + xpath);

			Element elem = (Element) doc.selectObject(xpath);
			elem.addAttribute("value", port);

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(buildFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception e) {
			logger.error("Can't set port in build.xml", e);
		}

	}

	public void setSF(SupraSphereFrame sF) {
		this.sF = sF;

	}

	public void runOnly(final SupraSphereFrame sF, boolean autoLogin) {

		String preArg = "nospawn";

		if (autoLogin == true) {
			preArg = "autologin";
		}

		final String arg = preArg;

		sF.disposeButDontRemove();
		File buildFile = new File(System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "build.xml");
		final Project project = new Project();

		project.init();
		DefaultLogger log = new DefaultLogger();
		log.setErrorPrintStream(System.err);
		log.setOutputPrintStream(System.out);
		log.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(log);

		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		ProjectHelper.configureProject(project, buildFile);
		// project.executeTarget("run");

		logger.info("will execute target: " + arg);
		project.executeTarget(arg);
		// Thread thread = new Thread() {
		// public void run() {
		try {
			// Runtime.getRuntime().exec("cmd /c \"java
			// -Djava.library.path="+bdir+" -jar supra."+port+".jar
			// "+arg+ " "+port+"\"");
			// Runtime.getRuntime().exec("java
			// -Djava.library.path="+bdir+" -jar supra."+port+".jar
			// "+arg+ " "+port);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// }
		// };
		// Display display = Display.getDefault();
		// display.asyncExec(thread);

		logger.info("Ending");

		System.exit(0);

		// launch.main(args);
		/*
		 * if (exit==true) { logger.info("Exiting in server...must have set
		 * server to true");
		 * 
		 * 
		 * if (sF!=null) { logger.info("sf was not nulll...close");
		 * sF.closeFromWithin(); System.exit(0); } else { System.exit(0);
		 * logger.info("sf was null after all"); } } else { logger.info("not
		 * exiting"); }
		 */

	}

	public void startBuild(String passArg, boolean passThread,
			final boolean exit) {

		// boolean inThread = passThread;
		final String arg = passArg;

		if (true) {

			Thread t = new Thread() {

				public void run() {

					logger.info("Starting");
					File buildFile = new File(System.getProperty("user.dir")
							+ System.getProperty("file.separator")
							+ "build.xml");
					final Project project = new Project();
					// project.addBuildListener(new Log4jListener());
					project.init();
					DefaultLogger log = new DefaultLogger();
					log.setErrorPrintStream(System.err);
					log.setOutputPrintStream(System.out);
					log.setMessageOutputLevel(Project.MSG_INFO);
					project.addBuildListener(log);

					project.setUserProperty("ant.file",
						buildFile.getAbsolutePath());
					ProjectHelper.configureProject(project, buildFile);
					// project.executeTarget("run");

					logger.info("now exec...why no fork??");

					project.executeTarget(arg);
					// Thread thread = new Thread() {
					// public void run() {
					try {
						// Runtime.getRuntime().exec("cmd /c \"java
						// -Djava.library.path="+bdir+" -jar supra."+port+".jar
						// "+arg+ " "+port+"\"");
						// Runtime.getRuntime().exec("java
						// -Djava.library.path="+bdir+" -jar supra."+port+".jar
						// "+arg+ " "+port);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
					// }
					// };
					// Display display = Display.getDefault();
					// display.asyncExec(thread);

					logger.info("Ending");
					// launch.main(args);
					if (exit == true) {
						logger.info("Exiting in server...must have set server to true");

						if (AntBuilderOld.this.sF != null) {
							logger.info("sf was not nulll...close");
							AntBuilderOld.this.sF.closeFromWithin();
							Display.getCurrent().dispose();
							System.exit(0);

						} else {
							Display.getCurrent().dispose();
							System.exit(0);
							logger.info("sf was null after all");
						}

					} else {
						logger.info("not exiting");
					}

				}
			};
			t.start();

		} else {

			logger.info("Starting");
			File buildFile = new File(System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "build.xml");
			Project project = new Project();
			// project.addBuildListener(new Log4jListener());
			project.init();
			DefaultLogger log = new DefaultLogger();
			log.setErrorPrintStream(System.err);
			log.setOutputPrintStream(System.out);
			log.setMessageOutputLevel(Project.MSG_INFO);
			project.addBuildListener(log);
			project.setUserProperty("ant.file", buildFile.getAbsolutePath());

			ProjectHelper.configureProject(project, buildFile);
			// project.executeTarget("run");
			project.executeTarget(arg);

			logger.info("Ending");
			System.exit(0);

		}

	}

}
