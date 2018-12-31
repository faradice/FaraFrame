package com.faradice.faraframe.properties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import com.faradice.faraframe.util.IDatabaseType;
import com.faradice.faraframe.util.SQLDB;

/**
 * Writes properties from a property model to a database
 *
 * @author ragnar.valdimarsson
*/

public class PropertyWriter<T extends IPropertyItem> {
	private final SQLDB sqldb;
	private final String tableName;
	private final String logTableName;
	private final boolean createTable;
	private final String[] INVALID_CHARS = {" ", "(", ")", "%", ",", ".",";","/", "\\","&","*","+","-"};
	private Date[] deletePeriod = null;

	public PropertyWriter(SQLDB database, String tableName, String logTableName, boolean createTable) {
		this.sqldb = database;
		this.tableName = tableName;
		this.logTableName = logTableName;
		this.createTable = createTable;
	}

	public PropertyWriter(SQLDB database, String tableName, String logTableName, boolean createTable, Date[] deletePeriod) {
		this.sqldb = database;
		this.tableName = tableName;
		this.logTableName = logTableName;
		this.createTable = createTable;
		this.deletePeriod = deletePeriod;
	}


	private String fixColumnName(String column) {
		String result = column;
		for (String invalid : INVALID_CHARS) {
			result = result.replace(invalid, "_");
		}
		if (Character.isDigit(result.charAt(0))) {
			result = "C_"+result;
		}
		return result;
	}

	private String getDBType(Object type) {
		String result = " varchar(100)";
		if (type instanceof Date) {
			result = " datetime";
		} else if (type instanceof Integer || type instanceof Long) {
			result = " numeric(18,0)";
		} else if (type instanceof Number) {
			result = " numeric(28,6)";
		}
		return result;
	}

	/**
	 * Checks if a table exists and creates it if it does not.
	 * @param columns
	 * @throws Exception
	 */
    private void prepareTable(T item) throws Exception {
		PreparedStatement stmt = null;
		try {
			String sql = "Select count(*) from "+tableName;
			stmt = sqldb.getConnection().prepareStatement(sql);
			stmt.execute();
		} catch (Exception ex) {
			try {
				stmt.close();
				String[] columns = item.getItems();

				// Create Table
				String sql = "Create table "+tableName+"("+fixColumnName(columns[0])+getDBType(item.get(columns[0]));
				for (int i=1; i<columns.length-1; i++) {
					sql = sql+","+fixColumnName(columns[i])+ getDBType(item.get(columns[i]));
				}
				sql = sql+",LogId numeric(18,0)";
				sql = sql +")";
				stmt =  sqldb.getConnection().prepareStatement(sql);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		} finally {
			if (stmt != null) stmt.close();
		}
    }

	public void write(String name, IPropertyModel<T> model) throws Exception {
		PreparedStatement logStmt = null;
		PreparedStatement dataStmt = null;

		try {
			// Write to log table
			int id = -1;
			if (logTableName != null) {
				StringBuilder logSQL = new StringBuilder("insert into "+logTableName+" (Source, Time, Rows, Name) ");
				logSQL.append(" values(?, getDate(), ?, ?)");

				logStmt = sqldb.getConnection().prepareStatement(logSQL.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				logStmt.setString(1, tableName);
				logStmt.setInt(2, model.size());
				logStmt.setString(3, name);
				logStmt.executeUpdate();
				ResultSet rs = logStmt.getGeneratedKeys();
				if(rs.next()) {
					id = rs.getInt(1);
				}
			}
			// Write all items to the data table
			List<T> items = model.getItems();
			T firstRow = items.get(0);
			String[] columns = items.get(0).getItems();

			// Create the table if it does not exists
			if (createTable) {
				prepareTable(firstRow);
			}

			if (deletePeriod != null) {
				deleteRowsInPeriod(tableName, deletePeriod);
			}

			IDatabaseType dbType = sqldb.getDatabaseTypes(tableName);

			StringBuilder insertSql = new StringBuilder("Insert into "+tableName+ " values(?");
			for (int col=0; col<columns.length-1; col++) {
				insertSql.append(",?");
			}
			insertSql.append(")");
			dataStmt =  sqldb.getConnection().prepareStatement(insertSql.toString());

			int row = 0;
			for (T item : items) {
				for (int col=0; col<columns.length-1; col++) {
					dbType.set(dataStmt, item.get(columns[col]), col+1);
				}

				// Set the LogId Id.
				dataStmt.setInt(columns.length, id);


				dataStmt.addBatch();
				if  (((++row) % 500) == 0) {
					dataStmt.executeBatch();
				}

			}
			dataStmt.executeBatch();
		} catch (Exception e) {
			sqldb.getConnection().rollback();
			throw(e);
		} finally {
			SQLDB.close(logStmt);
			SQLDB.close(dataStmt);
			sqldb.getConnection().commit();
		}
	}

	private void deleteRowsInPeriod(String tableName, Date[] deletePeriod) throws Exception {
		PreparedStatement deleteStmt = null;
		try {
			String sql = "Delete from "+tableName+" where Date >= ? and Date <= ?";
			deleteStmt = sqldb.getConnection().prepareStatement(sql);
			java.sql.Timestamp dateFrom = new java.sql.Timestamp(deletePeriod[0].getTime());
			java.sql.Timestamp dateTo = new java.sql.Timestamp(deletePeriod[1].getTime());
			deleteStmt.setTimestamp(1, dateFrom);
			deleteStmt.setTimestamp(2, dateTo);
			deleteStmt.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLDB.close(deleteStmt);
		}
	}

}
