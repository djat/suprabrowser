/*
 SupraSphere Inc. Copyright 2006
 */
package ss.server.admin;

import java.util.Vector;

import ss.server.db.XMLDB;

/**
 * Class to convert SupraSphere table structures
 * 
 * @author David Thomsom
 * 
 */
public class TableStructureConverter {

	public static void main(String[] args) {
		TableStructureConverter cts = new TableStructureConverter();
		cts.convertMediumBlobToMediumText();
	}

	public void convertMediumBlobToMediumText() {
		XMLDB xmldb = new XMLDB();
		Vector tables = xmldb.getConvertor().listTables();

		for (int i = 0; i < tables.size(); i++) {
			String tableName = (String) tables.get(i);

			String query = "alter table `" + tableName
					+ "` change xmldata xmldata mediumtext not null";

			xmldb.safeExecuteUpdate(query);
		}
	}
}
