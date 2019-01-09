package com.faradice.faraframe.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;

/**
 * Utility class to read a text file and store its content to a database table
 * using sql.  The table is created if it is not found, using the column names of
 * the header row which is excected to be the first row of the txt file
 *
 * @author ragnar.valdimarsson
 *
 */
public class TextFileLoader {
	private final SQLDB sqlDb;

	public TextFileLoader() {
		sqlDb = new SQLDB();
	}

    private void prepareTable(String textFile, String tableName)  throws Exception {
		PreparedStatement stmt = null;
		try {
			String sql = "Select count(*) from "+tableName;
			stmt = sqlDb.getConnection().prepareStatement(sql);
			stmt.execute();
		} catch (Exception ex) {
			try {

				// Create Table
				BufferedReader reader = new BufferedReader(new FileReader(textFile));
				String header = reader.readLine();
				String firstRow = reader.readLine();
				String[] cols = header.split("\t");
				String sql = "Create table "+tableName+"("+cols[0]+" varchar(50)";
				for (int i=1; i<cols.length; i++) {
					sql = sql+","+cols[i]+" varchar(50)";
				}
				sql = sql +")";
				System.out.println(sql);
				stmt.close();
				stmt =  sqlDb.getConnection().prepareStatement(sql);
				stmt.execute();
				System.out.println("Table "+tableName+" created");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			if (stmt != null) stmt.close();
		}
    }

    public void loadTextFile(String textFile, String tableName) throws Exception {
    	prepareTable(textFile, tableName);
		PreparedStatement stmt = null;
		BufferedReader reader = new BufferedReader(new FileReader(textFile));
		String line = null;
		try {
			int rowCount=0;
			// skip title
			line = reader.readLine();
			line = reader.readLine();
			while (line != null) {
				rowCount++;
				String[] cols = line.split("\t");
				if (stmt == null) {
					String sql = "Insert into "+tableName+ " values(?";
					for (int i=0; i<cols.length-1; i++) {
						sql = sql+",?";
					}
					sql = sql +")";
					stmt =  sqlDb.getConnection().prepareStatement(sql);
				}
				for (int i=0 ; i<cols.length; i++) {
					if (!cols[i].equalsIgnoreCase("null")) {
						stmt.setString(i+1, cols[i].trim());
					} else {
						stmt.setNull(i+1, java.sql.Types.VARCHAR);
					}
				}
				stmt.execute();
				line = reader.readLine();
			}
	    	System.out.println("Import Completed. rows: "+rowCount);
		} catch (Exception e) {
			sqlDb.getConnection().rollback();
			System.out.println(line);
			e.printStackTrace();
		} finally {
			reader.close();
			sqlDb.getConnection().commit();
			SQLDB.close(stmt);
		}
    }


    public static void main(String[] args) throws Exception {
    	TextFileLoader loader = new TextFileLoader();
        loader.loadTextFile("c:/TradeMapping.txt","trade.MaHenda");
	}

}
