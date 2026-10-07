package escrituraLecturaFicherosXMLyCSV;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class AgendaJSON2 {
	@SerializedName("agenda")
	private List<ContactoJSON2> contactos;
	public static void main(String[] args) throws Exception {
		
		// Ni Gson ni Jackson funcionan con esto
		
		String fichero = "agenda.json";
		try (Reader lector = new FileReader(fichero)){
			
			Gson gson = new Gson();
			AgendaJSON2 agenda = gson.fromJson(lector, AgendaJSON2.class);
			List<ContactoJSON2> contactos = agenda.getContactos();
			System.out.println("Contactos: " + contactos.size());
			for(ContactoJSON2 c:contactos) {
				System.out.println(c);
			}
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
	}

	public List<ContactoJSON2> getContactos() {
		return contactos;
	}
	
}
