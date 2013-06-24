package ss.launch;

/*

 Used to launch suprasphere with ant.

 */

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.tools.ant.*;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

public final class SupraLaunch {

	/**
	 * 
	 */
	private static final String RUN_XML_FILE_NAME = "run.xml";

	private static final org.apache.log4j.Logger logger = createLogger();

	private static final String fsep = System.getProperty("file.separator");

	private static final String bdir = System.getProperty("user.dir");

	public SupraLaunch() {
	}

	/**
	 * @return
	 */
	private static Logger createLogger() {
		BasicConfigurator.configure();
		return Logger.getLogger( SupraLaunch.class );
	}

	private static void setUpRunXml() {
		final File runFile = getRunXml();
		SAXReader reader1 = new SAXReader();
		try {
			Document doc = reader1.read(runFile);
			doc.getRootElement().addAttribute("basedir", bdir);
			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(runFile);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();

		} catch (Exception ex) {
			logger.error("Cant change run xml" + runFile, ex);
		}

	}

	/**
	 * @return
	 */
	private static File getRunXml() {
		return new File(bdir + fsep + RUN_XML_FILE_NAME);
	}

	public static void main(String[] args) {
		setUpRunXml();
		boolean invitePass = false;
		if (args.length > 0 && args[0].equals("invite") ) {
			invitePass = true;
		}
		final boolean invite = invitePass;
		startViaAnt(invite,false);
	}

	/**
	 * @param invite
	 */
	public static void restart( boolean invite) {
		startViaAnt(invite, true );
	}

	/**
	 * @param invite
	 */
	@SuppressWarnings("deprecation")
	private static void startViaAnt(boolean invite, boolean separateVm) {
		logger.info("Starting");
		File buildFile = getRunXml();
		Project project = new Project();
		project.init();
		final DefaultLogger log = new DefaultLogger();
		log.setErrorPrintStream(System.err);
		log.setOutputPrintStream(System.out);
		log.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(log);
		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		ProjectHelper.configureProject(project, buildFile);
		if (!invite) {
			project.setUserProperty( "separate.vm", separateVm ? "true" : "false" );
			project.executeTarget("run");
		} else {
			project.executeTarget("invite");
		}

		logger.info("Ending");
	}

}