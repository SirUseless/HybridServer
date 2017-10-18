package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 * Class defining an object wich represents a MySQL Connection
 */

public class MySQLConnection {
	protected Connection connection;
	
	private final String JDBC_DRIVER = "";
	
	private String db_url;
	private String db_user;
	private String db_passwd;
	
	public void connect() throws SQLException {
		try{
			connection = DriverManager.getConnection(db_url,db_user,db_passwd);
			//Class.forName(JDBC_DRIVER);
		}catch(SQLException e){
			throw e;
		}
	}
	
	public void close() throws SQLException {
		if(this.connection != null){
			if(!this.connection.isClosed()){
				connection.close();
			}
		}
	}
	
	//TODO getters and setters
}
