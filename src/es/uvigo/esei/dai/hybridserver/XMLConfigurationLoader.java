/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.uvigo.esei.dai.hybridserver.helpers.XMLHelper;

public class XMLConfigurationLoader {
	public static Configuration load(File xmlFile)
	throws Exception {
		Configuration conf = new Configuration();
		
		if(XMLHelper.checkWithXSDFromFile( xmlFile.getPath(), "configuration.xsd")){
			
			//Get DOM
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList node;
			
			//Connection parsing
			node = doc.getElementsByTagName("connections");
			Element aux = (Element) node.item(0);
			
			try{
				conf.setHttpPort( Integer.parseInt(aux.getElementsByTagName("http").item(0).getTextContent()));
			}catch(NumberFormatException e){
				throw new XMLParseException("Wrong port format"); 
			}catch (Exception e) {
				throw new XMLParseException("Missing port parameter"); 
			}
			
			try{
				conf.setWebServiceURL(aux.getElementsByTagName("webservice").item(0).getTextContent());
			}catch(Exception e){
				throw new XMLParseException("Wrong WebService URL"); 
			}
			
			try{
				conf.setNumClients(Integer.parseInt(aux.getElementsByTagName("numClients").item(0).getTextContent()));
			}catch(Exception e){
				throw new XMLParseException("Wrong client number format"); 
			}
			
			
			//Database connection parsing
			try{
				node = doc.getElementsByTagName("database");
				aux = (Element) node.item(0);
				conf.setDbUser(aux.getElementsByTagName("user").item(0).getTextContent());
				conf.setDbPassword(aux.getElementsByTagName("password").item(0).getTextContent());
				conf.setDbURL(aux.getElementsByTagName("url").item(0).getTextContent());
			}catch(Exception e){
				throw new XMLParseException("Missing or malformed database configuration"); 
			}
			
			//Server configuration parsing
			node = doc.getElementsByTagName("server");
			ArrayList<ServerConfiguration> servers = new ArrayList<ServerConfiguration>();
			ServerConfiguration sc;

			for (int i = 0; i < node.getLength(); i++) {
				sc = new ServerConfiguration();
				Node auxnode = node.item(i);
				aux = (Element) auxnode;
				sc.setName(aux.getAttribute("name"));
				sc.setWsdl(aux.getAttribute("wsdl"));
				sc.setNamespace(aux.getAttribute("namespace"));
				sc.setService(aux.getAttribute("service"));
				sc.setHttpAddress(aux.getAttribute("httpAddress"));
				servers.add(sc);
			}
			conf.setServers(servers);
			return conf;
		}else{
			throw new XMLParseException("XML file invalid for \"configuration.xsd\"");
		}

	}
}
