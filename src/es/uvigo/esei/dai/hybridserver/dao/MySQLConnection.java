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
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getDb_url() {
		return db_url;
	}

	public void setDb_url(String db_url) {
		this.db_url = db_url;
	}

	public String getDb_user() {
		return db_user;
	}

	public void setDb_user(String db_user) {
		this.db_user = db_user;
	}

	public String getDb_passwd() {
		return db_passwd;
	}

	public void setDb_passwd(String db_passwd) {
		this.db_passwd = db_passwd;
	}

	protected Connection connection;
	//TODO setup jdbc driver
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
