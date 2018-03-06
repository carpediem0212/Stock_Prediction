package kr.ac.jbnu.sql.stock;

import kr.ac.jbnu.sql.stock.persistance.PersistenceType;

public class Constants
{
	// persistence setting
	public static final PersistenceType persistence = PersistenceType.DB;
	
	// web setting
	public static final double NUM_OF_NEWS_IN_A_PAGE = 20; 
	public static final String USER_AGENT_INFO = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1 CoolNovo/2.0.4.16";
	
	public static final String HOME_DIR="";
	public static final String RESOURCE_DIR= HOME_DIR + "resource\\";
	
	//db setting
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "stock_estimation";
	public static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/"+DB_NAME;
	// "jdbc:mysql://127.0.0.1:3306/stock_estimation?useUnicode=true&characterEncoding=UTF-8";
	public static final String DB_USER = "root";
	public static final String DB_PASSWORD = "root";
	
}
