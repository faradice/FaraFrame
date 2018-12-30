package com.faradice.faraframe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * General JDBC connection class with standard connection methods and
 * helpers to connect to a database using JDBC.
 * @author ragnar.valdimarsson
 *
 */
public class SQLDB {
	public static final String MS_SQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private Connection sqlServerConn;
	private List<String> columnNames = null;

	/**
	 * Creates a JDBC connection to an MS-SQL Database.
	 * @param server  The host name
	 * @param database The default database name
	 * @param user The database user
	 * @param password The password of the user
	 */
	public SQLDB(String server, String database, String user, String password) {
		connect(server, database, user, password);
	}

	/**
	 * Creates a JDBC connection to an MS-SQL Database using the integraded security
	 * @param server  The host name
	 * @param database The default database name
	 */
	public SQLDB(String server, String database) {
		// NOTE: integradedSecurty needs the MS authentication dll (sqljdbc_auth.dll)
		connect("jdbc:sqlserver://"+server+";databaseName="+database+";integratedSecurity=true;");
	}

	public SQLDB() {
	}

	/**
	 * Creates a JDBC connection with properties from a configuration file
	 */
	public SQLDB(String configFile) {
		try {
			Configuration datumConfig = new Configuration(configFile);
			String host = datumConfig.get("Host");
			String db = datumConfig.get("Database");
			String user = datumConfig.get("User");
			String pwd = datumConfig.get("Password");
			connect(host, db, user, pwd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void connect(String server, String database, String user, String password) {
		connect("jdbc:sqlserver://"+server+";databaseName="+database+";username="+user+";password="+password);
	}

	protected void connect(String url) {
		sqlServerConn = getSQLServerConnection(url);
	}

	public Connection getConnection() {
		return sqlServerConn;
	}

    protected Connection getSQLServerConnection(String connectionUrl) {
		Connection con = null;
    	try {
    		Class.forName(MS_SQL_DRIVER);
     		con = DriverManager.getConnection(connectionUrl);
     		con.setAutoCommit(false);
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public void close() {
		try {
			sqlServerConn.close();
		} catch (SQLException e) {
			// ignore
		}
    }


    public static void close(Statement stmt) {
    	if (stmt != null) {
    		try {
				stmt.close();
			} catch (SQLException e) {
				// ignore
			}
    	}
    }


    public void rollback() {
    	if (sqlServerConn != null) {
    		try {
    			sqlServerConn.rollback();
			} catch (SQLException e) {
				// ignore
			}
    	}
    }

    public void commit() {
    	if (sqlServerConn != null) {
    		try {
    			sqlServerConn.commit();
			} catch (SQLException e) {
				// ignore
			}
    	}
    }


    public static void close(ResultSet rSet) {
    	if (rSet != null) {
    		try {
				rSet.close();
			} catch (SQLException e) {
				// ignore
			}
    	}
    }

	public static String toInList(String col, List<String> list) {
		if (list.size() < 1) return col + " in ()";
		StringBuilder result = new StringBuilder(col + " in (");
		result.append("'");
		result.append(list.get(0));
		result.append("'");
		for (int i=1; i<list.size(); i++) {
			if ((i % 999) == 0) {
				result.append(") or "+col+" in (");
			} else {
				result.append(",");
			}
			result.append("'");
			result.append(list.get(i));
			result.append("'");
		}
		result.append(")");
		return result.toString();
	}

    public boolean execute(String sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			stmt = sqlServerConn.prepareStatement(sql);
			result = stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
		} finally {
			close(rs);
			close(stmt);
			commit();
		}
		return result;
	}

    /**
     * @return The list of columns in the last SQL query
     */
    public List<String> getColumnsOfQuery() {
    	return columnNames;
    }

    public List<SQLEntry[]> executeQuery(String query) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<SQLEntry[]> list = new ArrayList<SQLEntry[]>();
		try {
			stmt = sqlServerConn.prepareStatement(query);
			rs = stmt.executeQuery();

			// Create Column Name list
			ResultSetMetaData metaData = rs.getMetaData();
			columnNames = new ArrayList<String>();
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				columnNames.add(metaData.getColumnLabel(i+1));
			}

			// Iterate through the data in the result set and display it.
			int colCount = rs.getMetaData().getColumnCount();
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
				SQLEntry[] entries = new SQLEntry[colCount];
				entries[0] = new SQLEntry(rs.getObject(1), true);
				for (int col=1; col < colCount; col++) {
					entries[col] = new SQLEntry(rs.getObject(col+1), false);
				}
				list.add(entries);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt);
		}
		return list;
	}

	public static java.sql.Date getSQLDate(Object date) {
		if (date == null) return null;
		if (!(date instanceof java.util.Date)) return null;
		return new java.sql.Date(((java.util.Date)date).getTime());
	}

	public static java.sql.Timestamp getSQLTimeStamp(Object timestamp) {
		if (timestamp == null) return null;
		if (!(timestamp instanceof java.util.Date)) return null;
		return new java.sql.Timestamp(((java.util.Date)timestamp).getTime());
	}

	public IDatabaseType getDatabaseTypes(String tableName) throws Exception {
		return new SQLType(tableName);
	}

	// TODO locale and formatting.
	private static DateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.UK);
	private static DateFormat dateTimeFormat2 = new SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.UK);
	private static final DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", Locale.UK);
	private static final DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
	private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

    class SQLType implements IDatabaseType {
		private final String tableName;
		private final int[] types;


		public SQLType(String tableName) throws Exception {
			this.tableName = tableName;
			this.types = getTypesFromDB();
		}

		private int[] getTypesFromDB() throws Exception {
			PreparedStatement stmt = null;
			try {
				// Create an inexpensive query to get the metadata of the result
				String sql = "select * from "+tableName+ " where 1 = 2";
				stmt = getConnection().prepareStatement(sql);
				ResultSetMetaData metaData = stmt.getMetaData();
				int columnCount = metaData.getColumnCount();
			    int[] result = new int[columnCount];
			    for (int col=0; col<columnCount; col++) {
			    	result[col] = metaData.getColumnType(col+1);
			    }
			    return result;
			} finally {
				if (stmt != null) stmt.close();
			}
		}

		public void set(PreparedStatement stmt, Object data, int index) throws Exception {
			int type = types[index-1];

			if (data == null || data.toString().toString().trim().length() < 1) {
				stmt.setNull(index, type);
				return;
			}

			switch (type) {
				case Types.VARCHAR :
				case Types.CHAR :
					stmt.setString(index, data.toString());
					break;
				case Types.TIMESTAMP :
				case Types.DATE :
					Timestamp timestamp = null;
					if (Date.class.isAssignableFrom(data.getClass())) {
						Date date = (java.util.Date)data;
						timestamp = new Timestamp(date.getTime());
					}
					else {
						// TODO Ugly hack as Dates (only datetime) are not supported in MS-SQL 2005
						// TODO Needs refactoring
						String value = data.toString();
						if (value.equalsIgnoreCase("null")) {
							timestamp = null;
						} else if (value.length() < 18 ) {
							Date date;
							if (value.indexOf("/") > 0) {
								date = getSQLDate(dateFormat2.parse(data.toString()));
							} else {
								date = getSQLDate(dateFormat.parse(data.toString()));
							}
							timestamp = new Timestamp(date.getTime());
						} else {
							if (value.indexOf("/") > 0) {
								timestamp = getSQLTimeStamp(dateTimeFormat2.parse(data.toString()));
							} else {
								timestamp = getSQLTimeStamp(dateTimeFormat.parse(data.toString()));
							}
						}
					}
					stmt.setTimestamp(index, timestamp);
					break;
				case Types.NUMERIC :
				case Types.REAL :
				case Types.DECIMAL :
				case Types.DOUBLE :
				case Types.FLOAT :
				case Types.INTEGER :
					Double number = null;
					if (data instanceof Number) {
						number = ((Number)data).doubleValue();
					} else {
						String str = data.toString().trim();
						if (str.equalsIgnoreCase("null")) {
							number = null;
						} else if (str.equalsIgnoreCase("NaN")) {
							number = null;
						} else if (str.indexOf("(") == 0) {
							str = "-"+str.substring(1,str.length()-1);
							number = numberFormat.parse(str).doubleValue();
						} else {
							number = numberFormat.parse(str).doubleValue();
						}
					}
					if (number != null && number.doubleValue() == Double.NaN) {
						number = null;
					}
					if (number == null) {
						stmt.setNull(index, Types.DOUBLE);
					} else {
						stmt.setDouble(index, number.doubleValue());
					}
					break;
				default :
					stmt.setObject(index, data.toString());
			}
		}
	}

}
