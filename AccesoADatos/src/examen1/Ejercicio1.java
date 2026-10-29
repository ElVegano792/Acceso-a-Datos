package examen1;

import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Ejercicio1 {

	public static void main(String[] args) {
		
		String fichero = "/home/alumno/Descargas/pokedex.xml";
		generarArchivo(fichero);
		
		
	}

	public static Document leerXML(String fichero) throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(fichero);
	}
	
	public static void generarArchivo(String fichero) {
		
		try {
			
			Document doc = leerXML(fichero);
			
			BufferedWriter pluma = new BufferedWriter(new FileWriter("/home/alumno/pokmemon.txt"));
			pluma.write("=== POKEDEX DEL PROFESOR OAK ===");
			pluma.newLine();
			
			NodeList listaPokemon=doc.getElementsByTagName("pokemon");
			for(int i=0;i<listaPokemon.getLength();i++) {
				
				Element pokemonElement = (Element)listaPokemon.item(i);
				String nombre = pokemonElement.getElementsByTagName("nombre").item(0).getTextContent();
				String tipo1 = pokemonElement.getElementsByTagName("tipo").item(0).getTextContent();
//				String tipo2 = pokemonElement.getElementsByTagName("tipo").item(1).getTextContent();
				String id = pokemonElement.getAttribute("id");
				String nivel = pokemonElement.getAttribute("nivel");
				if(id.length()==2) {
					id = "0" + id;
				} else if(id.length() == 1) {
					id = "00" + id;
				}
				
				pluma.write("#");
				pluma.write(id);
				pluma.write(" ");
				pluma.write(nombre);
				pluma.write(" | ");
				pluma.write(tipo1.toUpperCase());
//				if(!tipo2.equals(null)){
//					pluma.write("/");
//					pluma.write(tipo2.toUpperCase());
//				}
				pluma.write(" | Nivel ");
				pluma.write(nivel);
				pluma.newLine();
				
			}
			int total = listaPokemon.getLength();
			// TODO No se porque pero a la hora de generar el archivo en la parte final de Total: X Pokemon pone el número pero antes otros 3 ceros más
			pluma.write("=== Total: ");
			pluma.write(total);
			pluma.write(" Pokemon ===");
			System.out.println("Pokemon procesados: " + total);
			pluma.close();
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
		}
		
	}
	
} // Fin clase
