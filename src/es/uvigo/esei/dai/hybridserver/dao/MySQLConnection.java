package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 * Class defining an object which represents a MySQL Connection
 */

//TODO use connection pooling instead of opening/closing a connection every time (open/close > realOpen/realClose)
public class MySQLConnection implements AutoCloseable, IDBConnection {
	private Connection connection;
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	
	private String db_url;
	private String db_user;
	private String db_passwd;
	
	public MySQLConnection() throws ClassNotFoundException {
		this.loadDriver();
	}
	
	private void loadDriver() throws ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
	}

	public String getURL() {
		return db_url;
	}

	public void setURL(String db_url) {
		this.db_url = db_url;
	}

	public String getUser() {
		return db_user;
	}

	public void setUser(String db_user) {
		this.db_user = db_user;
	}

	public String getPassword() {
		return db_passwd;
	}

	public void setPassword(String db_passwd) {
		this.db_passwd = db_passwd;
	}

	
	
	public Connection connect() throws SQLException {
		try{
			connection = DriverManager.getConnection(db_url,db_user,db_passwd);
			return this.connection;
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
}
