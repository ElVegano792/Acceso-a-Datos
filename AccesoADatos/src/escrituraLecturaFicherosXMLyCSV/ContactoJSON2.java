package escrituraLecturaFicherosXMLyCSV;

import java.util.List;

public class ContactoJSON2 {
	
	private String nombre;
	private List<String> telefono;
	private String dni;
	
	public ContactoJSON2(String n, List<String> t, String d) {
		
		this.nombre = n;
		this.telefono = t;
		this.dni = d;
		
	}
	
	public void mostrar() {
		System.out.println("Nombre: " + this.nombre);
		System.out.println("Telefono: " + this.telefono);
		System.out.println("DNI: " + this.dni);
		System.out.println();
	}

	public String getNombre() {
		return nombre;
	}
	
//	public String getTelefono() {
//		return telefono;
//	}
	
	@Override
	public String toString() {
		String entrada = "Nombre: " + this.nombre + "\nDNI: " + this.dni + "\nTeléfonos: ";
		for(String tlf:this.telefono) {
			entrada+="\n " + tlf;
		}
		entrada+="\n";
		return entrada;
	}
	
	
}
