package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/*
 * Web Service interface
 */


@WebService
public interface HybridServerService {

	@WebMethod
	public List<String> listHTML() throws Exception;
	@WebMethod
	public List<String> listXML() throws Exception;
	@WebMethod
	public List<String> listXSD() throws Exception;
	@WebMethod
	public List<String> listXSLT() throws Exception;
	
	@WebMethod
	public String getHTML(String uuid) throws Exception;
	@WebMethod
	public String getXML(String uuid) throws Exception;
	@WebMethod
	public String getXSD(String uuid) throws Exception;
	@WebMethod
	public String getXSLT(String uuid) throws Exception;
	
	@WebMethod
	public String getXSDUUID(String xsltuuid) throws Exception;
	
	
}
