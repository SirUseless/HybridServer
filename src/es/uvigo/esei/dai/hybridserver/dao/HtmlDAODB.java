package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

public class HtmlDAODB implements IDocumentDAO {
	private IDBConnection connection;
	private String query;
	private ResultSet result;

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
		this.query = "INSERT INTO html (uuid, content) VALUES (?, ?)";
		try(Connection connectionInstance = this.connection.connect()){
			System.out.println(connectionInstance.getSchema());
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(String uuid, String content) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(String uuid) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<UUID, String> list() throws Exception {
		// TODO Auto-generated method stub
		return null;
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
