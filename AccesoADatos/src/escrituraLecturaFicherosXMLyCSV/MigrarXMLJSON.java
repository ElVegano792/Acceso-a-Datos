package escrituraLecturaFicherosXMLyCSV;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MigrarXMLJSON {

	public static void main(String[] args) {
		
		// Leer una agenda en XML y guardarla en JSON, por cada contacto crear un ContactoJSON y luego meterlo en un List y ahí ya crear el JSON
		
		
		
	}
	
	public static Document leerXML(String fichero) throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(fichero);
		
	}
	
}