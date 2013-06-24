/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import ss.client.networking.DialogsMainCli;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.ModerateAccessMember;
import ss.domainmodel.configuration.ModerationAccessModel;
import ss.global.SSConstants;

/**
 * @author david
 * 
 */
public class VariousUtils {

    public static String DYN_CLIENT_TEMPLATE = "<dyn_client><address/><port/><supra_sphere/></dyn_client>";

    public final static boolean IS_CLUBDEALABLE = true;
    
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VariousUtils.class);

    @SuppressWarnings("unchecked")
	public static String countStats(Document doc) {

        if (doc.getRootElement().element("search") != null) {
            return (new Integer(new Vector(doc.getRootElement().element(
                    "search").element("interest").elements()).size())
                    .toString());
        } else {
            return "0";
        }
    }
    
    public static String firstLetterToUpperCase(String arg) {
		String firstLetter = arg.substring(0, 1);
		firstLetter = firstLetter.toUpperCase();
		arg = arg.substring(1);
		arg = firstLetter+arg;
    	return arg;
    }
    
    public static String stripFinalSlashAndWWW(String inStr) {
    	
    	if (inStr==null) 
    		return null;
    	
    	inStr = inStr.toLowerCase(); 
    	 if (inStr.endsWith("/")) {
    		 inStr = inStr.substring(0,inStr.length()-1);
    	 }
    	 inStr = inStr.replace("www.", "");
    	 return inStr;
            	
    }
    
    public static boolean isDomain(String text) {
      text = text.toLowerCase();
      if (text.startsWith("http")||text.startsWith("feed")||text.startsWith("www")||text.endsWith(".com")||text.endsWith(".net")||text.endsWith(".org")||text.endsWith(".ru")) {
        if (text.lastIndexOf(' ')==-1) {
          return true;
        }
      }
      return false;
    }

    public static String getFnameFromDataFname(String data_filename) {

        int fnameNdx = data_filename
                .lastIndexOf(SSConstants.DATA_FILENAME_UNDERSCORES);

        String fName = "";

        if (fnameNdx > 0) {

            fnameNdx += SSConstants.DATA_FILENAME_UNDERSCORES.length();

            fName = new String(data_filename.substring(fnameNdx));
        }
        return (fName);
    }

    private static Random tableIdGenerator = new Random();

    public static synchronized String getNextRandomLong() {

        return new Long(Math.abs(tableIdGenerator.nextLong())).toString();

    }

    public static synchronized long getNextLong() {

        return new Long(Math.abs(tableIdGenerator.nextLong())).longValue();

    }

    // Adds all elements of the first vector to the second vector, but only if
    // the second vector does not already contain the same string value
    @SuppressWarnings("unchecked")
	public static Vector addAllWithoutDuplicates(Vector firstVector,
            Vector secondVector) {

        for (int i = 0; i < firstVector.size(); i++) {
            String string = (String) firstVector.get(i);

            if (!vectorContains(string, secondVector)) {

                secondVector.add(string);

            }

        }
        return secondVector;

    }

    public static boolean vectorContains(String value, Vector vec) {
        for (int i = 0; i < vec.size(); i++) {
            String one = (String) vec.get(i);
            if (one.equals(value)) {
                return true;
            }
        }
        return false;

    }

    public static String createMessageId() {
        return new Long(Math.abs(tableIdGenerator.nextLong())).toString();

    }

    public static String convertInviteURLtoSphereURL(String supraSphere,
            String inviteURL) {

        String sphereURL = null;
        try {

            sphereURL = inviteURL.replace("invite", "sphere");

            int index = sphereURL.lastIndexOf(',');

            sphereURL = sphereURL.substring(0, index);
            sphereURL = sphereURL + "," + supraSphere;

        } catch (Exception e) {

            return null;
        }

        return sphereURL;

    }

    public static String returnEmailAddressStringFromVector(Vector forwarding) {
        String emailList = "";

        logger.warn("Forwarding size! : " + forwarding.size());
        for (int i = 0; i < forwarding.size(); i++) {

            Element emailAddress = (Element) forwarding.get(i);
            String address = emailAddress.attributeValue("value");

            if (i == 0) {
                emailList = address;

            } else if (i <= forwarding.size()) {

                emailList = address + "," + emailList;

            }

        }
        return emailList;

    }

    @SuppressWarnings("unchecked")
	public static boolean checkElementAttributeValueExists(Document doc,
            String elementName, String value) {

        try {
            Vector list = new Vector(doc.getRootElement().elements(elementName));
            for (int i = 0; i < list.size(); i++) {
                Element one = (Element) list.get(i);
                if ((one.attributeValue("value")).equals(value)) {
                   return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
	public static boolean checkElementAttributeValuesExists( final Document doc,
            final String elementName, final List<String> values) {
    	if (values == null) {
    		return false;
    	}
        try {
            Vector list = new Vector(doc.getRootElement().elements(elementName));
            for (int i = 0; i < list.size(); i++) {
                Element one = (Element) list.get(i);
                if ( values.contains(one.attributeValue("value")) ) {
                   return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    @SuppressWarnings("unchecked")
	public static Vector createVectorFromCommaSeparatedEmails(String commaString) {

        Vector vector = new Vector();

        StringTokenizer st = new StringTokenizer(commaString, ",");
        while (st.hasMoreElements()) {
            Element address = new DefaultElement("email_address").addAttribute(
                    "value", st.nextToken());

            vector.addElement(address);

        }

        return vector;
    }

    public static void printContentsOfSession(Hashtable session) {

        for (Enumeration enumer = session.keys(); enumer.hasMoreElements();) {

            String key = (String) enumer.nextElement();
            try {
                logger.info("Key/Value: " + key + " / "
                        + (String) session.get(key));
            } catch (ClassCastException cce) {
                logger.error("Key: " + key+"  exception: "+cce.getMessage(), cce);
            }

        }

    }

    public static boolean isTextHTML(String text) {
        boolean isHTML = false;
        
        if(text==null)
        	return false;

        // if
        // (text.toLowerCase().lastIndexOf("<body")!=-1||(text.toLowerCase().lastIndexOf("<div")!=-1)||(text.toLowerCase().lastIndexOf("<br")!=-1))
        // {
        if (text.toLowerCase().lastIndexOf("<body") != -1
                || (text.toLowerCase().lastIndexOf("<div") != -1)
                || (text.toLowerCase().lastIndexOf("<br") != -1)
                || (text.toLowerCase().lastIndexOf("<p") != -1)
                || (text.toLowerCase().lastIndexOf("<b") != -1)) {

            isHTML = true;
        }
        return isHTML;

    }

    public static void printKeysOfHash(Hashtable hash) {
        for (Enumeration enumer = hash.keys(); enumer.hasMoreElements();) {

            String key = (String) enumer.nextElement();
            try {
                logger.warn("Key: " + key);
            } catch (ClassCastException cce) {
                logger.error(cce.getMessage(), cce);
            }

        }

    }

    public static String convertFseps(String remoteFsep, String localFsep,
            String name) {
        String result = null;

        if (name.lastIndexOf(remoteFsep) == -1) {
            return name;
        } else {

            result = name.replace(remoteFsep, localFsep);
            return result;

        }

    }

    public static String escapeSingleQuotes(String text) {
        StringBuffer sb = new StringBuffer(text.length());

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\'') {
                sb.append("\\");
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static void addXMLToFile(String filename, String xmlText) {

        try {
            File f = new File(filename);

            Document doc = DocumentHelper.parseText(xmlText);

            OutputFormat format = OutputFormat.createCompactFormat();
            FileOutputStream fout = new FileOutputStream(f);
            XMLWriter writer = new XMLWriter(fout, format);
            writer.write(doc);
            writer.close();
            fout.close();

        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }

    }

    public static boolean isNumber(String checkString) {
        try {
            for (int i = 0; i < checkString.length(); i++) {
                char character = checkString.charAt(i);
                if (character < '0' || character > '9')
                    return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
    
    public static String stripQuotes(String inStr) {
    	
    	StringBuffer outBuf;

        outBuf = new StringBuffer(inStr.length());
        for (int i = 0; i < inStr.length(); ++i)
            switch (inStr.charAt(i)) {
            case '"':
                // do nothing
                break;
            default:
                outBuf.append(inStr.charAt(i));

            }
        return outBuf.toString();
    	
    }

    public static String strip(String inStr) {

        StringBuffer outBuf;

        outBuf = new StringBuffer(inStr.length());
        for (int i = 0; i < inStr.length(); ++i)
            switch (inStr.charAt(i)) {
            case '<':

                // case '\t':
                // case ' ' :

                // do nothing
                break;
            case 'T':
                break;
            case 'O':
                break;
            case ':':
                break;
            default:
                outBuf.append(inStr.charAt(i));

            }
        return outBuf.toString();
    }

    public static String increaseStringByNumeroUno(String number) {
        String largerByOne = null;

        if (number != null) {
            Integer integer = new Integer(number);
            int num = integer.intValue();
            num = num + 1;
            Integer result = new Integer(num);
            largerByOne = result.toString();

            return largerByOne;
        } else {
            return "0";
        }

    }

    public static File getSupraFile(String filename) {
        // look for the requested filename in the $HOME/.SupraSphere directory
        // return a to-be-created file, if necessary, by not checking that the
        // file already exists

        File reqFile = new File(System.getProperty("user.dir") + File.separator
                + filename);
        return reqFile;
        /*
         * String supraRunDir = System.getProperty("supra.run.dir");
         * 
         * if ( supraRunDir == null ) { supraRunDir =
         * System.getProperty("user.home") + File.separator + ".SupraSphere"; }
         * 
         * File supraDir = new File( supraRunDir ); if ( supraDir == null ) { //
         * handle this unexpected condition... } if ( ! supraDir.exists() ) {
         * supraDir.mkdirs(); }
         *  // this is the same dir as the supraDir! - we just don't want to
         * catch the exception File reqFile = new File( supraRunDir +
         * File.separator + filename); return reqFile;
         */
    }

    public static File getSupraFile(String parentDir, String filename) {

        String supraRunDir = System.getProperty("supra.run.dir");

        if (supraRunDir == null) {
            supraRunDir = System.getProperty("user.home") + File.separator
                    + ".SupraSphere";
        }
        File supraDir = new File(supraRunDir);
        if (supraDir == null) {
            // handle this unexpected condition...
        }
        if (!supraDir.exists()) {
            supraDir.mkdirs();
        }
        String supraParentStr = System.getProperty("user.home")
                + File.separator + ".SupraSphere" + File.separator + parentDir;

        File supraParentDir = new File(supraParentStr);
        if (!supraParentDir.exists()) {
            supraParentDir.mkdirs();
        }
        File reqFile = new File(supraParentStr + File.separator + filename);
        return reqFile;
    }

	public static String convertToFullURL(String shortDomain) {
		if (shortDomain.toLowerCase().startsWith("feed://")) {
            shortDomain = shortDomain.replace("feed://","http://");
        }
        if (shortDomain.toLowerCase().startsWith("www")) {
        	shortDomain = "http://"+shortDomain;
        }
        else if (!shortDomain.toLowerCase().startsWith("http")) {
          shortDomain = "http://www."+shortDomain;
        }
        return shortDomain;
	}
	
	public final static boolean canAccessClubdealAdministrate(final DialogsMainCli client) {
		if(client.getVerifyAuth().isAdmin()) {
			return true;
		}
		ConfigurationValue configuration = SsDomain.CONFIGURATION.getMainConfigurationValue();
		if ( configuration == null ) {
			logger.error( "configuration is null" );
			return false;
		}
		final String contactName = (String)client.session.get(SessionConstants.REAL_NAME);
		for(ModerationAccessModel cdAccess : configuration.getClubdealModerateAccesses()) {
			for(ModerateAccessMember member : cdAccess.getMemberList()) {
				if(!member.getContactName().equals( contactName )) {
					continue;
				}
				if(!member.isModerator()) {
					continue;
				}
				return true;
			}
		}
		return false;
	}
	
	public static synchronized long getNextUniqueId() {
		return Math.abs(tableIdGenerator.nextLong());
	}
}
