package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HtmlDAODB implements IDocumentDAO {
	private IDBConnection connection;
	private String query;

	public HtmlDAODB(String url, String user, String password)
		throws ClassNotFoundException {
		this.connection = new MySQLConnection();
		this.connection.setURL(url);
		this.connection.setUser(user);
		this.connection.setPassword(password);
	}

	@Override
	public String create(String doc) throws Exception {
		String uuid = UUID.randomUUID().toString();
		
		this.rawCreate(uuid, doc);
		
		return uuid;
	}

	@Override
	public void rawCreate(String uuid, String doc) throws Exception {
		this.query = "INSERT INTO HTML (uuid, content) VALUES (?, ?)";
		try(Connection connectionInstance = this.connection.connect()){
			try(PreparedStatement stmt = connectionInstance.prepareStatement(this.query, Statement.RETURN_GENERATED_KEYS)){
				stmt.setString(1, uuid);
				stmt.setString(2, doc);
				int result = stmt.executeUpdate();
				
				if(result != 1){
					throw new SQLException();
				}
			}
		}
	}

	@Override
	public String read(String uuid) throws Exception {
		this.query = "SELECT * FROM HTML WHERE uuid = ?";
		try(Connection connectionInstance = this.connection.connect()){
			try(PreparedStatement stmt = connectionInstance.prepareStatement(this.query)){
				stmt.setString(1, uuid);
				
				try(ResultSet result = stmt.executeQuery()){
					while (result.next()) {
						return result.getString("content");
					}
				}catch(SQLException e){
					throw new SQLException();
				}
			}
		}
		return null;
	}

	@Override
	public void update(String uuid, String content) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(String uuid) throws Exception {
		this.query = "DELETE FROM HTML WHERE uuid = ?";
		try(Connection connectionInstance = this.connection.connect()){
			try(PreparedStatement stmt = connectionInstance.prepareStatement(this.query)){
				stmt.setString(1, uuid);
				int result = stmt.executeUpdate();
				
				if(result == 1){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Map<UUID, String> list() throws Exception {
		Map<UUID, String> toRet = new LinkedHashMap<UUID, String>();
		this.query = "SELECT * FROM HTML";
		try(Connection connectionInstance = this.connection.connect()){
			try(PreparedStatement stmt = connectionInstance.prepareStatement(this.query)){
				
				try(ResultSet result = stmt.executeQuery()){
					while (result.next()) {
						toRet.put(UUID.fromString(result.getString("uuid")), result.getString("content"));
					}
				}
			}
		}
		return toRet;
	}

	@Override
	public boolean isAvaliable() {
		try {
			this.connection.connect();
			this.connection.close();
			return true;
		} catch (SQLException e) {
			return false;
		}

	}

}
