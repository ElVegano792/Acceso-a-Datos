package escrituraLecturaFicherosXMLyCSV;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import com.google.gson.*;

public class CasoJSON {

	public static void main(String[] args) {
		
		final String ruta = "agenda.json";
		leerAgenda(ruta);
		
	}

	public static void leerAgenda(String ruta) {
		
		try(Reader lector = new FileReader(ruta)){
			
			Gson gson = new Gson();
			// Para crearlo ponemos el lector y la clase que tendrá que crear
			AgendaJSON agenda = gson.fromJson(lector, AgendaJSON.class);
			List<ContactoJSON> contactos = agenda.getContactos();
			for(ContactoJSON c:contactos) {
				c.mostrar();
			}
			
		} catch(Exception e) {
			System.out.println("Error al leer el fichero");
		}
		
	}
	
	public static void cargarListaContactos(String fichero) {
		List<ContactoJSON> contactos = null;
	}
	
}
