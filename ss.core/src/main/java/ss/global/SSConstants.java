package ss.global;

public class SSConstants {

	// system properties
	public static String fsep = System.getProperty("file.separator");

	public static String bdir = System.getProperty("user.dir");

	// for log4j

	public final static String LOG_SS = "SSLOGGER";

	// for fonts
	public final static String[] FAMILY_ACTION_NAMES = new String[] {
			"font-family-SansSerif", "SanSerif", "font-family-Monospaced",
			"Monospaced", "font-family-Serif", "Serif", };

	public final static String[] STYLE_ACTION_NAMES = new String[] {
			"font-italic", "Italic", "italic.gif", "font-bold", "Bold",
			"bold.gif", "font-underline", "Underline", "underline.gif", };

	public final static String[] SIZE_ACTION_NAMES = new String[] {
			"font-size-8", "8", "font-size-10", "10", "font-size-12", "12",
			"font-size-14", "14", "font-size-16", "16", "font-size-18", "18",
			"font-size-24", "24", "font-size-36", "36", "font-size-48", "48", };

	// Parsing
	public final static String DATA_FILENAME_UNDERSCORES = new String("_____");

}
