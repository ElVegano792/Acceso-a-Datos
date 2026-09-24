package escrituraLecturaFicherosXMLyCSV;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*; // esto es para cargar la estructura de los XML

public class CasoXML {

	public static void main(String[] args) throws Exception {
		
		String fichero = "agenda.xml";
		
		leerAgenda(fichero);
		System.out.println();
		buscarContacto(fichero,"José María");
		eliminarContacto(fichero, "Rosa Melano");
		eliminarContacto(fichero, "Pepito Perez");
		buscarContacto(fichero, "Rosa Melano");
		
	}
	
	public static void leerAgenda(String fichero) throws Exception {
		
		// Primero se crea el DocumentBuilderFactory, luego el DocumentBuilder y para rematar el Document
		// Leemos el XML y lo almacenamos en el objeto doc
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(fichero);
		
		// Esto es parar decir que coja los objetos bajo el tag de Contacto y lo mete en una lista iterable
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		
		// Para recorrer la lista de contactos
		for(int i=0;i<listaContactos.getLength();i++) {
			
			// Para pillar un contacto
			// Cojo el elemento I y lo guardo en el objeto contacto
			Node nodo = listaContactos.item(i);
			Element contacto = (Element)nodo;
			// Lo mismo que de arriba pero compacto
//			Element contacto = (Element)listaContactos.item(i);
			
			// Esto es para obtener el nombre directamente en 
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
			
			System.out.printf("Nombre: %s, telefono: %s\n",nombre,telefono);
			
		}
		
	}
	
	public static void buscarContacto(String fichero,String nombre) throws Exception {
		ArrayList<String> listas = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(fichero); // En este paso el fichero se cierra y cualquier cambio efectuado no queda reflejado en el XML
		
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		int contador = 0;
		boolean encontrado = false;
		for(int i=0;i<listaContactos.getLength() && encontrado == false;i++) {
			
			Node nodo = listaContactos.item(i);
			Element contacto = (Element)nodo;
			 
			String nombre1 = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
			
			if(nombre.equalsIgnoreCase(nombre1)) {
				System.out.println("El teléfono de " + nombre1 + " es " + telefono);
				encontrado = true;
			}
			
		}
		if(encontrado == false) {
			System.out.println("No tienes nigún contacto que se llame " + nombre);
		}
		
	}
	
	public static void eliminarContacto(String fichero,String nombre) throws Exception {
		ArrayList<String> listas = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(fichero);
		
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		int contador = 0;
		boolean encontrado = false;
		for(int i=0;i<listaContactos.getLength() && encontrado == false;i++) {
			
			Node nodo = listaContactos.item(i);
			Element contacto = (Element)nodo;
			 
			String nombre1 = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
			
			if(nombre.equalsIgnoreCase(nombre1)) {
				encontrado = true;
				// TODO Para borrar de un XML hay que borrarlo y luego guardarlo debido a que todos los cambios se realizan en memoria, no en el propio archivo
				Element raiz = doc.getDocumentElement();
				raiz.removeChild(contacto);
				System.out.println("¡Contaco eliminado correctamente!");

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(fichero);
				transformer.transform(source, result);
			}
			
		}
		if(encontrado == false) {
			System.out.println("El contacto " + nombre + " no se ha podido eliminar debido a que no existe.");
		}
		
	}
	
}
