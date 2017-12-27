package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDBConnection{
	public String getURL();
	public void setURL(String db_url);
	public String getUser();
	public void setUser(String db_user);
	public String getPassword();
	public void setPassword(String db_passwd);
	public Connection connect() throws SQLException;
	public void close() throws SQLException;
}
