package escrituraLecturaFicherosXMLyCSV;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AgendaJSON {
	
	// Necesitamos meterle una anotación e indicar el elemento raíz, en este caso es agenda
	@SerializedName("agenda")
	private List<ContactoJSON> contactos;
	
}