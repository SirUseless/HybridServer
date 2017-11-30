package es.uvigo.esei.dai.hybridserver.dao;

import java.util.Map;
import java.util.UUID;

public class XsdDAODB implements IDocumentDAO {
	private IDBConnection connection;
	private String query;

	public XsdDAODB(String url, String user, String password)
		throws ClassNotFoundException {
		this.connection = new MySQLConnection();
		this.connection.setURL(url);
		this.connection.setUser(user);
		this.connection.setPassword(password);
	}

	@Override
	public String create(String doc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rawCreate(String uuid, String doc) throws Exception {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return false;
	}

}
