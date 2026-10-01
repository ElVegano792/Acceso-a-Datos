package escrituraLecturaFicherosXMLyCSV;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.google.gson.*;

public class CasoJSON {

	public static void main(String[] args) {
		
		final String ruta = "agenda.json";
		leerAgenda(ruta);
		ContactoJSON nuevo = new ContactoJSON("José María","132646584","9564264X");
		ContactoJSON nuevo1 = new ContactoJSON("El Pepe","56975345","9564264Y");
		System.out.println("--------------------------");
		crearContacto(nuevo1, ruta);
//		leerAgenda(ruta);
		
	}

	public static void leerAgenda(String ruta) {
		
		try(Reader lector = new FileReader(ruta)){
			
			Gson gson = new Gson();
			// Para crearlo ponemos el lector y la clase que tendrá que crear
			AgendaJSON agenda = gson.fromJson(lector, AgendaJSON.class);
			List<ContactoJSON> contactos = cargarListaContactos(ruta);
			if(contactos!=null) {
				for(ContactoJSON c: contactos) {
					System.out.println(c);
				}
			}
			
		} catch(Exception e) {
			System.out.println("Error al leer el fichero");
		}
		
	}
	
	public static List<ContactoJSON> cargarListaContactos(String fichero) {
		List<ContactoJSON> contactos = null;
		try(Reader lector = new FileReader(fichero)){
			
			Gson gson = new Gson();
			AgendaJSON agenda = gson.fromJson(lector, AgendaJSON.class);
			contactos = agenda.getContactos();
			// Vv Esto es lo mismo que lo de arriba pero en menos líneas vV \\
//			contactos = gson.fromJson(lector, AgendaJSON.class).getContactos();
			
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		return contactos;
	}
	
	// Para grabar un JSON en disco
	public static void guardarAgenda(List<ContactoJSON> contactos, String fichero) {
		AgendaJSON agenda = new AgendaJSON();
		agenda.setContactos(contactos);
		try (Writer escritor = new FileWriter(fichero)){
			Gson gson = new Gson();
			gson.toJson(agenda,escritor);
			
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static void crearContacto(ContactoJSON contacto, String fichero) {
		List<ContactoJSON> contactos = cargarListaContactos(fichero);
		boolean encontrado = false;
		if(contactos!=null) {
			for(ContactoJSON c:contactos) {
				if(c.getNombre().equalsIgnoreCase(contacto.getNombre())) {
					encontrado = true;
				}
			}
			if(encontrado == false) {
				System.out.println("Guardando contacto " + contacto.getNombre());
				// Para añadir el contacto al JSON, eso si, estamos trabajando en memoria, por lo cual hay que grabarlo en disco usando el método guardarAgenda
				contactos.add(contacto);
				guardarAgenda(contactos,fichero);
				System.out.println("¡Contacto guardado con éxito!");
			} else {
				System.out.println("Ya existe un contacto con ese nombre.");
			}
		}
		
	}
	
}
