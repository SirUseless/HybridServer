package es.uvigo.esei.dai.hybridserver.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/*
 * Helper class for common XML tasks, such as validation throught schemas and XSLT transformations
 */

public class XMLHelper {
	
	private static SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	public static String transformWithXSLTfromURI(String xmlURI, String xsltURI) throws MalformedURLException, IOException, TransformerException{
		HttpURLConnection xmlConn = (HttpURLConnection) new URL(xmlURI).openConnection();
	    InputStream isXml = xmlConn.getInputStream();
	    
		HttpURLConnection xsltConn = (HttpURLConnection) new URL(xsltURI).openConnection();
        InputStream isXslt = xsltConn.getInputStream();
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(isXslt);
        Transformer transformer = factory.newTransformer(xslt);

        
        Source xml = new StreamSource(isXml);
        
        StringWriter writer = new StringWriter();
        StreamResult html = new StreamResult(writer);
        
        transformer.transform(xml, html);
       
		return writer.toString();
	}
	
	public static boolean checkWithXSDFromFile(String xmlPath, String xsdPath){
		try {
			Schema schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean checkWithXSDFromURI(String xmlURI, String xsdURI){
		try {
            Schema schema = factory.newSchema(new URL(xsdURI));
   
            HttpURLConnection xml = (HttpURLConnection) new URL(xmlURI).openConnection();
            InputStream is = xml.getInputStream();
            
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(is));
		} catch (SAXException | IOException e) {
			return false;
		}
		return true;
	}

}
