package es.uvigo.esei.dai.hybridserver.dao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 * Map implementation of IDocumentDAO
 */
public class HtmlDAOMap implements IDocumentDAO{
	private Map<UUID, String> htmlMappedDB = new LinkedHashMap<UUID, String>();
		
	public HtmlDAOMap(){
		this.htmlMappedDB = new LinkedHashMap<UUID, String>();
	}
	
	@Override
	public String create(String doc){
		String uuid = UUID.randomUUID().toString();
		this.htmlMappedDB.put(UUID.fromString(uuid), doc);
				
		return uuid;
	}

	@Override
	public String read(String uuid){
		if(this.htmlMappedDB.containsKey(UUID.fromString(uuid))){
			return this.htmlMappedDB.get(UUID.fromString(uuid));
		}else{
			return null;
		}
	}

	@Override
	public void update(String uuid, String doc) throws Exception{
		if(this.htmlMappedDB.containsKey(UUID.fromString(uuid))){
			this.htmlMappedDB.replace(UUID.fromString(uuid), doc);
		}else{
			throw new Exception("Element does not exist.");
		}
		
	}

	@Override
	public boolean delete(String uuid){
		if(this.htmlMappedDB.containsKey(UUID.fromString(uuid))){
			this.htmlMappedDB.remove(UUID.fromString(uuid));
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Map<UUID, String> list(){
		return this.htmlMappedDB;
	}

	@Override
	public void rawCreate(String uuid, String doc) throws Exception {
		if(!this.htmlMappedDB.containsKey(UUID.fromString(uuid))){
			this.htmlMappedDB.put(UUID.fromString(uuid), doc);
		}else{
			throw new Exception("Duplicate key");
		}
	}

	@Override
	public boolean isAvaliable() {
		return true;
	}



}
