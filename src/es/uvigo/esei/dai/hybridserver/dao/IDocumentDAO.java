package es.uvigo.esei.dai.hybridserver.dao;

import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 * Document Data Access Object Interface defining basic CRUDL capabilities
 */
public interface IDocumentDAO {
	/**
	 * 
	 * @param doc string containing the document data
	 * @return UUID random UUID assigned to the document
	 * @throws Exception if there is an insertion problem
	 */
	public String create(String doc) throws Exception;
	/**
	 * 
	 * @param uuid pre-assigned uuid 
	 * @param doc string containing the document data
	 * @throws Exception if there is an insertion problem or uuid already exists
	 */
	public void rawCreate(String uuid, String doc) throws Exception;
	public String read(String uuid) throws Exception;
	public void update(String uuid, String content) throws Exception;
	/**
	 * @param uuid uuid of document to be deleted
	 * @return true if deleted || false if already non existent in persistent data
	 * @throws Exception
	 */
	public boolean delete(String uuid) throws Exception;
	public Map<UUID, String> list() throws Exception;
	public boolean isAvaliable();
}
