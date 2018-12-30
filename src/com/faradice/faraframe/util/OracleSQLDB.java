package com.faradice.faraframe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleSQLDB extends SQLDB {
	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

	public OracleSQLDB() {
	}

	public OracleSQLDB(String server, String database) {
		throw new RuntimeException("Not supported");
	}

	public OracleSQLDB(String server, String sid, String user, String password) {
		connect("jdbc:oracle:thin:"+user+"/"+password+"@//"+server+":1521/"+sid);
	}

    protected Connection getSQLServerConnection(String connectionUrl) {
		Connection con = null;
    	try {
    		Class.forName(ORACLE_DRIVER);
     		con = DriverManager.getConnection(connectionUrl);
     		con.setAutoCommit(false);
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public static void main(String[] args) throws SQLException {
		OracleSQLDB db = new OracleSQLDB("rvk-ora-d01", "oradev", "Dev006", "Dev006");
		System.out.println(db.getConnection().getMetaData().getDriverVersion());
	}
}
