/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.server.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.SearchCriteria;
import ss.common.SphereReferenceList;
import ss.common.VerifyAuth;
import ss.common.exception.SystemException;
import ss.domainmodel.SphereReference;
import ss.smtp.reciever.EmailProcessor;

public class AcrossTableUtils {

    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger logger = ss.global.SSLogger
            .getLogger(AcrossTableUtils.class);

    private XMLDB xmldb;

    public static void main(String[] args) {

        String email = args[0];
        AcrossTableUtils across = new AcrossTableUtils();
        // across.getLocationsOfSphereName("For First");
        logger.info("start");
        // across.searchAcrossAll();
        logger.info("end");

    }

    public AcrossTableUtils() {

    }

    public AcrossTableUtils(XMLDB xmldb) {

        this.xmldb = xmldb;

    }

    /**
     * searchAcrossAllowedSpheres will query across all Spheres that the user is
     * allowed to query. This is the implementation of the SupraSearch.
     * 
     * @param queryData
     *            contains the query criteria
     * @return an ArrayList of XML Documents
     * @throws SQLException
     * @throws DocumentException
     */
    public ArrayList searchAcrossAllowedSpheres(XMLDB xmldb, Hashtable session,
            VerifyAuth verifyAuth, SearchCriteria searchCriteria)
            throws SystemException, DocumentException {

        ArrayList searchData = new ArrayList();
//        ArrayList individualResults = new ArrayList();

        // try {

        // Query the table and add the document result if it's not null
        // if((individualResults = queryTable(session, searchCriteria,
        // tableName)) != null)
        // searchData.addAll(individualResults);

        // }
        // catch(SQLException exc) {
        // String excMessage = "SQL exception while doing supra search";
        // logger.error(excMessage, exc);
        // throw new SystemException(excMessage, exc);
        // }

        return searchData;
    }

    @SuppressWarnings("unchecked")
	public Hashtable getAllSpheresWithoutDuplicatesTableKeys() {
        Hashtable all = new Hashtable();
        Vector tables = this.xmldb.getConvertor().listTables();

        for (int i = 0; i < tables.size(); i++) {
            String table = (String) tables.get(i);
            if (!table.equals("recalled")) {

                Vector spheres = this.xmldb.getAllSpheres();
                for (int j = 0; j < spheres.size(); j++) {
                    Document doc = (Document) spheres.get(j);
                    doc.getRootElement().addElement("current_table").setText(
                            table);

                    if (doc.getRootElement().attributeValue("system_name") != null) {
                        all.put(doc.getRootElement().attributeValue(
                                "system_name"), doc);
                    }
                }
            }
        }

        return all;
    }

    public Hashtable oldGetAll() {
        Hashtable all = new Hashtable();
        //Vector tables = xmldb.getConvertor().listTables();

        return all;

    }

    @SuppressWarnings("unchecked")
	public Hashtable getAllSpheresFromSupraspheresTable() {

        Hashtable all = new Hashtable();
        Vector spheres = this.xmldb.getAllSpheres();
        for (int j = 0; j < spheres.size(); j++) {
            Document doc = (Document) spheres.get(j);

            if (doc.getRootElement().attributeValue("system_name") != null) {
                all
                        .put(
                                doc.getRootElement().attributeValue(
                                        "system_name"), doc);
            }
        }

        return all;

    }

    @SuppressWarnings("unchecked")
	public Hashtable getAllSpheresWithoutDuplicates() {

        Hashtable all = new Hashtable();
        Vector tables = this.xmldb.getConvertor().listTables();

        for (int i = 0; i < tables.size(); i++) {
            String table = (String) tables.get(i);
            if (!table.equals("recalled")) {

                Vector spheres = this.xmldb.getAllSpheres();
                for (int j = 0; j < spheres.size(); j++) {
                    Document doc = (Document) spheres.get(j);

                    if (doc.getRootElement().attributeValue("system_name") != null) {
                        all.put(doc.getRootElement().attributeValue(
                                "system_name"), doc);
                    }
                }
            }
        }
        return all;
    }

    @SuppressWarnings("unchecked")
	public Hashtable getLocationsOfSphereName(String sphereName) {

        Hashtable all = new Hashtable();

        XMLDB xmldb = new XMLDB();
        Vector tables = xmldb.getConvertor().listTables();

        logger.info("TABLE SIZE: " + tables.size());

        for (int i = 0; i < tables.size(); i++) {
            String table = (String) tables.get(i);

            Vector spheres = xmldb.getAllSpheres();

            for (int j = 0; j < spheres.size(); j++) {

                Document doc = (Document) spheres.get(j);

                if (doc.getRootElement().attributeValue("system_name") != null) {
                    if (doc.getRootElement().attributeValue("display_name")
                            .equals(sphereName)) {
                        all.put(doc.getRootElement().attributeValue(
                                "system_name"), doc);
                        logger.info("HERE: "
                                + doc.getRootElement().attributeValue(
                                        "system_name") + " : " + table);
                    }
                }

            }

        }

        return all;

    }

    @SuppressWarnings("unchecked")
	public Hashtable getLocationsOfSphereSystemName(String sphereName) {

        Hashtable all = new Hashtable();

        XMLDB xmldb = new XMLDB();
        // Vector tables = xmldb.listTables();

        // for (int i=0;i<tables.size();i++) {
        // String table = (String)tables.get(i);

        Vector spheres = xmldb.getAllSpheres();

        for (int j = 0; j < spheres.size(); j++) {

            Document doc = (Document) spheres.get(j);

            if (doc.getRootElement().attributeValue("system_name") != null) {
                if (doc.getRootElement().attributeValue("system_name").equals(
                        sphereName)) {
                    all.put(doc.getRootElement().element("current_sphere")
                            .attributeValue("value"), doc);

                }
            }

        }

        // }

        return all;

    }

}
