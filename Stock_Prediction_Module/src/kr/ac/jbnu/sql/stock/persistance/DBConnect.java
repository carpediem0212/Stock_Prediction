package kr.ac.jbnu.sql.stock.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kr.ac.jbnu.sql.stock.Constants;

public class DBConnect {
	private static DBConnect connectInstance = null;
	private Connection conn;
	private String jdbc_driver = Constants.JDBC_DRIVER;
	private String jdbc_url = Constants.JDBC_URL;

	private DBConnect() {
	}

	public static DBConnect getDBInstance() {
		if (connectInstance == null) {
			connectInstance = new DBConnect();
		}

		return connectInstance;
	}

	public void connect() {
		try {
			Class.forName(jdbc_driver);
			conn = DriverManager.getConnection(jdbc_url, Constants.DB_USER, Constants.DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Connection getConnection() {
		return conn;
	}
}
