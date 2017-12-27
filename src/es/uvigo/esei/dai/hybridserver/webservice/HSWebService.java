package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAODB;


@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.webservice.IHSWebService", 
serviceName = "HSWebService")
public class HSWebService implements IHSWebService {
	
	private HtmlDAODB htmlDAO;
	private XmlDAODB xmlDAO;
	private XsdDAODB xsdDAO;
	private XsltDAODB xsltDAO;
	
	public HSWebService(String url, String user, String password){
		super();
		try{
			this.htmlDAO = new  HtmlDAODB(url, user, password);
			this.xmlDAO = new  XmlDAODB(url, user, password);
			this.xsdDAO = new  XsdDAODB(url, user, password);
			this.xsltDAO = new  XsltDAODB(url, user, password);
		}catch(ClassNotFoundException e){
			//TODO handle exception
		}
		
	}
	

	@Override
	@WebMethod
	public List<String> listHTML() throws Exception {
		ArrayList<UUID> uuidList = new ArrayList<UUID>(this.htmlDAO.list().keySet());
		ArrayList<String> toret = new ArrayList<String>();
		
		for(UUID u : uuidList){
			toret.add(u.toString());
		}
		
		return toret;
	}

	@Override
	@WebMethod
	public List<String> listXML() throws Exception {
		ArrayList<UUID> uuidList = new ArrayList<UUID>(this.xmlDAO.list().keySet());
		ArrayList<String> toret = new ArrayList<String>();
		
		for(UUID u : uuidList){
			toret.add(u.toString());
		}
		
		return toret;
	}

	@Override
	@WebMethod
	public List<String> listXSD() throws Exception {
		ArrayList<UUID> uuidList = new ArrayList<UUID>(this.xsdDAO.list().keySet());
		ArrayList<String> toret = new ArrayList<String>();
		
		for(UUID u : uuidList){
			toret.add(u.toString());
		}
		
		return toret;
	}

	@Override
	@WebMethod
	public List<String> listXSLT() throws Exception {
		ArrayList<UUID> uuidList = new ArrayList<UUID>(this.xsltDAO.list().keySet());
		ArrayList<String> toret = new ArrayList<String>();
		
		for(UUID u : uuidList){
			toret.add(u.toString());
		}
		
		return toret;
	}

	@Override
	@WebMethod
	public String getHTML(String uuid) throws Exception {
		return this.htmlDAO.read(uuid);
	}

	@Override
	@WebMethod
	public String getXML(String uuid) throws Exception {
		return this.xmlDAO.read(uuid);
	}

	@Override
	@WebMethod
	public String getXSD(String uuid) throws Exception {
		return this.xsdDAO.read(uuid);
	}

	@Override
	@WebMethod
	public String getXSLT(String uuid) throws Exception {
		return this.xsltDAO.read(uuid);
	}

	@Override
	@WebMethod
	public String getXSDUUID(String xsltuuid) throws Exception {
		return this.xsltDAO.getXSD(xsltuuid);
	}

}
